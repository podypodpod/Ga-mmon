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
import java.awt.Component;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		final MainMenuPanel panel = new MainMenuPanel(done -> {
			searches.incrementAndGet();
			whenDone.set(done);
		});
		final JButton startSearch = button(panel, "Start Search");

		// pressing it twice while the first search is running starts only one
		startSearch.doClick();
		startSearch.doClick();
		assertEquals(1, searches.get());
		assertFalse(startSearch.isEnabled());
		assertTrue(panel.status().contains("window"), "it should say where the progress is");

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

	private static List<String> buttonNames(final MainMenuPanel panel) {
		final List<String> names = new ArrayList<>();
		for (final Component component : panel.getComponents()) {
			if (component instanceof JButton) {
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
