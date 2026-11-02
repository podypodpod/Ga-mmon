package uk.co.davidlomas.gammon.test.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

public class FitnessCalculatorTest {
	@BeforeAll
	public static void setup() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}
}
