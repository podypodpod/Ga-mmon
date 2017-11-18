package uk.co.davidlomas.gammon.test.game;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

public class GameStatsTest {
	@BeforeClass
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterClass
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

}
