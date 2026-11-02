package uk.co.davidlomas.gammon.test.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

public class DiceListTest {
	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}
}
