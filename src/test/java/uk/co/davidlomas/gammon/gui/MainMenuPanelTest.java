package uk.co.davidlomas.gammon.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import javax.swing.JButton;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
	public void pressingStartSearchStartsTheSearch() {
		final AtomicInteger searches = new AtomicInteger();
		final MainMenuPanel panel = new MainMenuPanel(searches::incrementAndGet);

		button(panel, "Start Search").doClick();

		assertEquals(1, searches.get());
	}

	@Test
	public void theSearchRunsTheGeneticAlgorithmOnItsOwnThread() throws InterruptedException {
		// SettingsUtil sets attempt 9999, so this run is attempt 10000
		final Thread search = MainMenuPanel.startSearch();

		assertNotSame(Thread.currentThread(), search);
		search.join(120_000);
		assertFalse(search.isAlive(), "the search didn't finish");
		assertEquals(10000, GenAlgSettings.getAttemptCount());
		assertTrue(new File(SettingsUtil.getPlayersDir() + "/Attempt - 10000/PlayerFromGen0").isFile());
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
