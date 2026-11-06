package uk.co.davidlomas.gammon.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.SavedPlayers.SavedPlayer;
import uk.co.davidlomas.gammon.genes.SearchProgress;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MainMenuPanelTest {

	private static final String START = "Create Good Players";

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void theMenuHasItsTwoButtons() {
		final MainMenuPanel panel = new MainMenuPanel();

		assertEquals(List.of(START, "Play Best Two"), buttonNames(panel));
		assertTrue(button(panel, START).isEnabled());
		assertTrue(button(panel, "Play Best Two").isEnabled());
	}

	@Test
	public void bestTwoCantBePlayedDuringASearch() {
		final AtomicReference<Runnable> whenDone = new AtomicReference<>();
		final MainMenuPanel panel = new MainMenuPanel((menu, done) -> whenDone.set(done));

		button(panel, START).doClick();
		assertFalse(button(panel, "Play Best Two").isEnabled());

		whenDone.get().run();
		assertTrue(button(panel, "Play Best Two").isEnabled());
	}

	@Test
	public void withoutTwoSavedPlayersItSaysSoAndStaysOnTheMenu() {
		final SavedPlayer one = new SavedPlayer("One", new Individual(), 0);
		final MainMenuPanel panel = new MainMenuPanel((menu, done) -> {
		}, () -> List.of(one));

		button(panel, "Play Best Two").doClick();

		assertTrue(panel.status().contains(START), panel.status());
		assertNull(panel.watchWindow());
	}

	@Test
	public void playBestTwoSwapsTheMenuForTheGameAndBack() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final MainMenuContainerFrame menu = new MainMenuContainerFrame();
		final SavedPlayer one = new SavedPlayer("One", new Individual(), 0);
		final SavedPlayer two = new SavedPlayer("Two", new Individual(), 0);
		final MainMenuPanel panel = new MainMenuPanel((m, done) -> {
		}, () -> List.of(one, two));
		SwingUtilities.invokeAndWait(() -> {
			menu.run();
			menu.getContentPane().remove(menu.mmp);
			menu.add(panel);
		});
		try {
			SwingUtilities.invokeAndWait(button(panel, "Play Best Two")::doClick);
			final WatchGameFrame window = panel.watchWindow();
			assertTrue(window.isVisible());
			assertFalse(menu.isVisible());

			SwingUtilities.invokeAndWait(window::back);

			assertFalse(window.isDisplayable());
			assertTrue(menu.isVisible());
			window.gameThread().join(120_000);
			assertFalse(window.gameThread().isAlive(), "the game should have stopped");
		} finally {
			SwingUtilities.invokeAndWait(() -> {
				panel.watchWindow().dispose();
				menu.dispose();
			});
		}
	}

	@Test
	public void startSearchRunsOneSearchAtATime() {
		final AtomicInteger searches = new AtomicInteger();
		final AtomicReference<Runnable> whenDone = new AtomicReference<>();
		final MainMenuPanel panel = new MainMenuPanel((menu, done) -> {
			searches.incrementAndGet();
			whenDone.set(done);
		});
		final JButton startSearch = button(panel, START);

		// pressing it twice while the first search is running starts only one
		startSearch.doClick();
		startSearch.doClick();
		assertEquals(1, searches.get());
		assertFalse(startSearch.isEnabled());
		assertFalse(panel.status().isBlank(), "it should say a search is running");

		// once that search ends, it can be pressed again
		whenDone.get().run();
		assertTrue(startSearch.isEnabled());
		assertTrue(panel.status().isBlank());
		startSearch.doClick();
		assertEquals(2, searches.get());
	}

	@Test
	public void theSearchRunsTheGeneticAlgorithmOnItsOwnThread() throws Exception {
		// a small, quick run, set here so this test doesn't depend on what SettingsUtil chooses
		GenAlgSettings.setPopulationSize(2);
		GenAlgSettings.setGenerations(1);
		final int attempt = Individual.lastAttempt() + 1;
		final CountDownLatch done = new CountDownLatch(1);

		final SearchProgress progress = new SearchProgress();
		final Thread search = MainMenuPanel.searchThread(progress, done::countDown);
		// StartGa can't be stopped part way, so a run that's still going after this carries on in the background.
		// As a daemon it can't keep the test run open
		search.setDaemon(true);
		search.start();

		assertNotSame(Thread.currentThread(), search);
		assertTrue(done.await(120, TimeUnit.SECONDS), "the search didn't finish");
		assertEquals(attempt, GenAlgSettings.getAttemptCount());
		assertEquals(SearchProgress.State.FINISHED, progress.getState());
		assertTrue(new File(SettingsUtil.getPlayersDir() + "/Attempt - " + attempt + "/PlayerFromGen0").isFile());
	}

	@Test
	public void aSearchWaitsForAPersonOnlyWhenTheyPlayPlayerTwo() {
		try {
			GameSettings.setAreBothAIsCache(true);
			assertFalse(MainMenuPanel.waitsForAPerson());

			GameSettings.setAreBothAIsCache(false);
			assertTrue(MainMenuPanel.waitsForAPerson());
		} finally {
			SettingsUtil.resetSettings();
		}
	}

	@Test
	public void aSearchThatFailsIsReportedAndEnds() throws Exception {
		try {
			GenAlgSettings.setPopulationSize(0);
			GameSettings.setAreBothAIsCache(false);
			final List<String> messages = new CopyOnWriteArrayList<>();
			final CountDownLatch done = new CountDownLatch(1);
			final SearchProgress progress = new SearchProgress();

			final Thread search = MainMenuPanel.searchThread(progress, done::countDown,
					(message, type) -> messages.add(type + " " + message));
			search.setDaemon(true);
			search.start();

			assertTrue(done.await(120, TimeUnit.SECONDS), "the search didn't end");
			assertEquals(SearchProgress.State.FAILED, progress.getState());
			assertEquals(2, messages.size(), messages.toString());
			assertTrue(messages.get(0).startsWith(JOptionPane.INFORMATION_MESSAGE + " areBothAIs"), messages.get(0));
			assertTrue(messages.get(1).startsWith(JOptionPane.ERROR_MESSAGE + " The search failed"), messages.get(1));
		} finally {
			SettingsUtil.resetSettings();
		}
	}

	@Test
	public void aWindowLeftHiddenUntilTheSearchEndsGoesWithIt() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		// a search that only ends when this test says, so the window is certain to still be hidden when it does
		final SearchProgress progress = new SearchProgress();
		final AtomicReference<Runnable> whenDone = new AtomicReference<>();
		final MainMenuPanel[] panel = new MainMenuPanel[1];
		SwingUtilities.invokeAndWait(() -> {
			panel[0] = new MainMenuPanel((menu, done) -> {
				menu.showSearch(progress);
				whenDone.set(done);
			});
			button(panel[0], START).doClick();
		});
		final SearchProgressFrame window = panel[0].progressWindow();
		try {
			SwingUtilities.invokeAndWait(window::back);
			assertFalse(window.isVisible());
			assertTrue(window.isDisplayable());

			progress.finished();
			SwingUtilities.invokeAndWait(whenDone.get());

			assertFalse(window.isDisplayable(), "left hidden, it would stop the program ending");
			assertFalse(button(panel[0], "Back to Search").isVisible());
		} finally {
			SwingUtilities.invokeAndWait(window::dispose);
		}
	}

	@Test
	public void closingTheMenuDuringASearchBringsBackItsWindow() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final MainMenuContainerFrame menu = openMenuForASmallSearch();
		final JButton startSearch = button(menu.mmp, START);
		try {
			// all in one go on the Swing thread, so the search can't be reported as over part way through
			SwingUtilities.invokeAndWait(() -> {
				startSearch.doClick();
				final SearchProgressFrame window = menu.mmp.progressWindow();
				window.back();

				closeTheWayItsXDoes(menu);

				assertFalse(menu.isVisible());
				assertTrue(menu.isDisplayable(), "it should still be there to go back to");
				assertTrue(window.isVisible(), "something should show that the search is still going");
			});

			awaitTheSearchEnding(startSearch);
			SwingUtilities.invokeAndWait(menu.mmp.progressWindow()::back);
			assertTrue(menu.isVisible());

			// with no search running, closing it really does close it
			SwingUtilities.invokeAndWait(() -> closeTheWayItsXDoes(menu));
			assertFalse(menu.isDisplayable());
		} finally {
			close(menu);
		}
	}

	private static void closeTheWayItsXDoes(final MainMenuContainerFrame menu) {
		menu.dispatchEvent(new WindowEvent(menu, WindowEvent.WINDOW_CLOSING));
	}

	@Test
	public void theMenuAndTheSearchsWindowSwapPlaces() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final MainMenuContainerFrame menu = openMenuForASmallSearch();
		final JButton startSearch = button(menu.mmp, START);
		final JButton backToSearch = button(menu.mmp, "Back to Search");
		assertFalse(backToSearch.isVisible());
		try {
			// all in one go on the Swing thread, so the search can't be reported as over part way through
			SwingUtilities.invokeAndWait(() -> {
				startSearch.doClick();
				final SearchProgressFrame window = menu.mmp.progressWindow();
				assertFalse(menu.isVisible(), "the window should replace the menu");
				assertTrue(window.isVisible());
				assertTrue(backToSearch.isVisible());

				window.back();
				assertTrue(menu.isVisible());
				assertFalse(window.isVisible());

				backToSearch.doClick();
				assertFalse(menu.isVisible());
				assertTrue(window.isVisible());

				window.back();
			});

			awaitTheSearchEnding(startSearch);

			assertFalse(backToSearch.isVisible());
			assertTrue(menu.isVisible());
			assertFalse(menu.mmp.progressWindow().isDisplayable(), "left hidden, it would stop the program ending");
		} finally {
			close(menu);
		}
	}

	@Test
	public void theSearchsWindowStaysUntilLeftAfterTheSearchEnds() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final MainMenuContainerFrame menu = openMenuForASmallSearch();
		final JButton startSearch = button(menu.mmp, START);
		try {
			SwingUtilities.invokeAndWait(startSearch::doClick);

			awaitTheSearchEnding(startSearch);

			final SearchProgressFrame window = menu.mmp.progressWindow();
			assertTrue(window.isVisible());
			assertFalse(menu.isVisible());

			SwingUtilities.invokeAndWait(window::back);

			assertTrue(menu.isVisible());
			assertFalse(window.isDisplayable());
		} finally {
			close(menu);
		}
	}

	private static MainMenuContainerFrame openMenuForASmallSearch() throws Exception {
		GenAlgSettings.setPopulationSize(2);
		GenAlgSettings.setGenerations(1);
		final MainMenuContainerFrame menu = new MainMenuContainerFrame();
		SwingUtilities.invokeAndWait(menu::run);
		return menu;
	}

	private static void awaitTheSearchEnding(final JButton startSearch) throws Exception {
		final long giveUp = System.nanoTime() + TimeUnit.SECONDS.toNanos(120);
		while (!startSearch.isEnabled()) {
			assertTrue(System.nanoTime() < giveUp, "the search didn't finish");
			Thread.sleep(50);
		}
		// what ends a search happens in one go on the Swing thread, so this is after all of it
		SwingUtilities.invokeAndWait(() -> {
		});
	}

	private static void close(final MainMenuContainerFrame menu) throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			if (menu.mmp.progressWindow() != null) {
				menu.mmp.progressWindow().dispose();
			}
			menu.dispose();
		});
	}

	private static List<String> buttonNames(final MainMenuPanel panel) {
		final List<String> names = new ArrayList<>();
		for (final Component component : panel.getComponents()) {
			if (component instanceof JButton && component.isVisible()) {
				names.add(((JButton) component).getText());
			}
		}
		return names;
	}

	private static JButton button(final MainMenuPanel panel, final String name) {
		for (final Component component : panel.getComponents()) {
			if (component instanceof JButton && name.equals(((JButton) component).getText())) {
				return (JButton) component;
			}
		}
		throw new AssertionError("no " + name + " button");
	}
}
