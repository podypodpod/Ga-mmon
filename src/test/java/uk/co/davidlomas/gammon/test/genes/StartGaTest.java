package uk.co.davidlomas.gammon.test.genes;

import org.junit.Before;
import org.junit.Test;

import uk.co.davidlomas.gammon.genes.StartGa;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

public class StartGaTest {

	@Before
	public void setup() {
		GenAlgSettings.setAttemptCount(9999);
		GenAlgSettings.setPopulationSize(1);
		GenAlgSettings.setGenerations(0);
	}

	@Test
	public void createStartGa() {
		new StartGa();
	}

}
