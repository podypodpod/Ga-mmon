package uk.co.davidlomas.gammon.settings;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameSettingsTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@BeforeEach
	public void setup() {
		// reset all values to default as its static and the tests can therefore
		// Influence each other

		GameSettings.setAreBothAIsCache(null);
		GameSettings.setDisplayGUICache(null);
		GameSettings.setIsP1BlackCache(null);
		GameSettings.setMultiThreadingCache(null);

		GameSettings.setTimeDelayCache(-1);
	}

	@Test
	public void settingP1BlackThenGettingReturnsExpected() {
		GameSettings.setIsP1BlackCache(true);
		assertTrue(GameSettings.isP1Black());
	}

	@Test
	public void settingDisplayGuiThenGettingReturnsExpected() {
		GameSettings.setDisplayGUICache(false);
		assertFalse(GameSettings.getDisplayGUI());
	}

	@Test
	public void settingAreBothAIsThenGettingReturnsExpected() {
		GameSettings.setAreBothAIsCache(false);
		assertFalse(GameSettings.getAreBothAI());
	}

	@Test
	public void settingMultiThreadingThenGettingReturnsExpected() {
		GameSettings.setMultiThreadingCache(true);
		assertTrue(GameSettings.getMultiThreading());
	}

	@Test
	public void settingTimeDelayThenGettingReturnsExpected() {
		final int newTimeDelayCache = 4;
		GameSettings.setTimeDelayCache(newTimeDelayCache);
		assertEquals(newTimeDelayCache, GameSettings.getTimeDelay());
	}

	@Test
	public void threadsIsZeroForOnePerCoreWhenTheFileDoesNotSay() {
		assertEquals(0, GameSettings.threadsFrom(null));
	}

	@Test
	public void threadsIsReadFromTheFile() {
		assertEquals(4, GameSettings.threadsFrom(" 4 "));
	}

	@Test
	public void threadsBelowZeroIsRefused() {
		assertThrows(IllegalArgumentException.class, () -> GameSettings.threadsFrom("-1"));
		assertThrows(IllegalArgumentException.class, () -> GameSettings.setThreadsCache(-2));
	}
}
