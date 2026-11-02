package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartGaTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@BeforeEach
	public void setup() {
		// a population of three, so every player has games to play, for two generations
		GenAlgSettings.setElitism(true);
		GenAlgSettings.setGenerations(2);
		GenAlgSettings.setPopulationSize(3);
		GenAlgSettings.setAttemptCount(10000);
		GenAlgSettings.setTournamentSize(2);
		GenAlgSettings.setUniformRate(0.5);
		GenAlgSettings.setMutationRate(0.1);
	}

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
	}

	@Test
	public void aRunSavesTheFittestPlayerFromTheStartAndFromEachGeneration() {
		RandomSource.setSeed(1);

		StartGa.main(null);

		// each run is a new attempt, with its own folder
		assertEquals(10001, GenAlgSettings.getAttemptCount());
		final File attempt = new File(SettingsUtil.getPlayersDir() + "/Attempt - 10001");
		assertTrue(new File(attempt, "FittestFromInitialPopulation").isFile());
		assertTrue(new File(attempt, "PlayerFromGen0").isFile());
		assertTrue(new File(attempt, "PlayerFromGen1").isFile());
		assertFalse(new File(attempt, "PlayerFromGen2").exists(), "only two generations were asked for");
	}
}
