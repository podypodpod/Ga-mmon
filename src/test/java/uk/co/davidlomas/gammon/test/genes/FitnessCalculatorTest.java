package uk.co.davidlomas.gammon.test.genes;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import testingCode.Settings;

public class FitnessCalculatorTest {
	@BeforeClass
	public static void setup() {
		Settings.resettSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resettSettings();
	}
}
