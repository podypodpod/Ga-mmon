package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

public class UtilTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}
}
