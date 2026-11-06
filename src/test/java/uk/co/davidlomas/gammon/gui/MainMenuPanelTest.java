package uk.co.davidlomas.gammon.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.SearchProgress;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MainMenuPanelTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void theMenuHasItsThreeButtons() {
		assertEquals(List.of("Start Search", "Play Best Two", "Change Settings"), buttonNames(new MainMenuPanel()));
	}

	@Test
	public void buttonsThatDontDoAnythingYetAreGreyedOut() {
		final MainMenuPanel panel = new MainMenuPanel();

		assertTrue(button(panel, "Start Search").isEnabled());
		assertFalse(button(panel, "Play Best Two").isEnabled());
		assertFalse(button(panel, "Change Settings").isEnabled());
	}

	@Test
	public void startSearchRunsOneSearchAtATime() {
		final AtomicInteger searches = new AtomicInteger();
		final AtomicReference<Runnable> whenDone = new AtomicReference<>();
		final MainMenuPanel panel = new MainMenuPanel((menu, done) -> {
			searches.incrementAndGet();
			whenDone.set(done);
		});
		final JButton startSearch = button(panel, "Start Search");

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
	public void aFailureBeforeTheSearchStartsCountsAsTheSearchFailing() {
		final SearchProgress progress = new SearchProgress();

		assertThrows(IllegalStateException.class, () -> MainMenuPanel.markingFailure(progress, () -> {
			throw new IllegalStateException("a mistake in the settings");
		}));

		assertEquals(SearchProgress.State.FAILED, progress.getState());
	}

	@Test
	public void leavingTheSearchsWindowHiddenUntilItEndsStillLetsTheProgramEnd() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final MainMenuContainerFrame menu = openMenuForASmallSearch();
		final JButton startSearch = button(menu.mmp, "Start Search");
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
		final JButton startSearch = button(menu.mmp, "Start Search");
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
