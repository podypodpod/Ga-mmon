package uk.co.davidlomas.gammon.test.genes;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.co.davidlomas.gammon.genes.StartGa;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.Settings;

public class StartGaTest {
	@BeforeClass
	public static void beforeClass() {
		Settings.resetSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resetSettings();
	}

	@Before
	public void setup() {
		GenAlgSettings.setElitism(true);
		GenAlgSettings.setGenerations(2);
		GenAlgSettings.setPopulationSize(1);
		GenAlgSettings.setAttemptCount(9999);
		GenAlgSettings.setTournamentSize(1);

		GenAlgSettings.setUniformRate(1);
		GenAlgSettings.setMutationRate(1);
	}

	@Test
	public void createStartGa() {
		StartGa.main(null);
	}

}
