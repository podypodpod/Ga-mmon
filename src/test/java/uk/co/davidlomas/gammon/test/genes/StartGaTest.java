package uk.co.davidlomas.gammon.test.genes;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import testingCode.Settings;
import uk.co.davidlomas.gammon.genes.StartGa;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

public class StartGaTest {
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
		GenAlgSettings.setElitism(true);
		GenAlgSettings.setDisplayConsole(true);

		GenAlgSettings.setGenerations(0);
		GenAlgSettings.setPopulationSize(1);
		GenAlgSettings.setAttemptCount(9999);
		GenAlgSettings.setTournamentSize(1);

		GenAlgSettings.setUniformRate(1);
		GenAlgSettings.setMutationRate(1);
	}

	@Test
	public void createStartGa() {
		new StartGa();
	}

}
