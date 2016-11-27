package uk.co.davidlomas.gammon.test.genes;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import uk.co.davidlomas.gammon.test.helpers.Settings;

public class GeneticAlgorithmTest {
	@BeforeClass
	public static void setup() {
		Settings.resettSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resettSettings();
	}
}
