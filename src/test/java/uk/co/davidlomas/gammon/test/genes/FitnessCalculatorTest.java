package uk.co.davidlomas.gammon.test.genes;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import uk.co.davidlomas.gammon.test.helpers.Settings;

public class FitnessCalculatorTest {
	@BeforeClass
	public static void setup() {
		Settings.resetSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resetSettings();
	}
}
