package uk.co.davidlomas.gammon.test.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.test.helpers.Settings;

public class GameSettingsTest {

	private final Boolean newIsP1BlackCache = true;
	private final Boolean newDisplayGUICache = false;
	private final Boolean newAreBothAIsCache = false;
	private final Boolean newDisplayConsoleCache = false;
	private final Boolean newMultiThreadingCache = true;

	private final int newTimeDelayCache = 4;

	@BeforeClass
	public static void beforeClass() {
		Settings.resettSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resettSettings();
	}

	@Before
	public void setup() {
		// reset all values to default as its static and the tests can therefore
		// Influence each other

		GameSettings.setAreBothAIsCache(null);
		GameSettings.setDisplayConsoleCache(null);
		GameSettings.setDisplayGUICache(null);
		GameSettings.setIsP1BlackCache(null);
		GameSettings.setMultiThreadingCache(null);

		GameSettings.setTimeDelayCache(-1);
	}

	@Test
	public void settingP1BlackThenGettingReturnsExpected() {
		GameSettings.setIsP1BlackCache(newIsP1BlackCache);
		assertTrue(GameSettings.isP1Black());
	}

	@Test
	public void settingDisplayGuiThenGettingReturnsExpected() {
		GameSettings.setDisplayGUICache(newDisplayGUICache);
		assertFalse(GameSettings.getDisplayGUI());
	}

	@Test
	public void settingAreBothAIsThenGettingReturnsExpected() {
		GameSettings.setAreBothAIsCache(newAreBothAIsCache);
		assertFalse(GameSettings.getAreBothAI());
	}

	@Test
	public void settingDisplayConsoleThenGettingReturnsExpected() {
		GameSettings.setDisplayConsoleCache(newDisplayConsoleCache);
		assertFalse(GameSettings.getDisplayConsole());
	}

	@Test
	public void settingMultiThreadingThenGettingReturnsExpected() {
		GameSettings.setMultiThreadingCache(newMultiThreadingCache);
		assertTrue(GameSettings.getMultiThreading());
	}

	@Test
	public void settingTimeDelayThenGettingReturnsExpected() {
		GameSettings.setTimeDelayCache(newTimeDelayCache);
		assertEquals(newTimeDelayCache, GameSettings.getTimeDelay());
	}

}
