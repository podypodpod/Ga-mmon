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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
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
	public void aRunSavesTheFittestPlayerFromTheStartAndFromEachGeneration() throws IOException {
		RandomSource.setSeed(1);

		StartGa.main(null);

		// each run is a new attempt, with its own folder
		assertEquals(10001, GenAlgSettings.getAttemptCount());
		final File attempt = new File(SettingsUtil.getPlayersDir() + "/Attempt - 10001");
		for (final String saved : new String[]{"FittestFromInitialPopulation", "PlayerFromGen0", "PlayerFromGen1"}) {
			assertIsAPlayer(new File(attempt, saved));
		}
		assertFalse(new File(attempt, "PlayerFromGen2").exists(), "only two generations were asked for");
	}

	/**
	 * The file holds a whole chromosome, 7 binary digits for each gene, and the fitness the player was saved with
	 */
	private static void assertIsAPlayer(final File file) throws IOException {
		assertTrue(file.isFile(), file + " wasn't saved");
		final Properties properties = new Properties();
		try (InputStream input = new FileInputStream(file)) {
			properties.load(input);
		}
		final int genes = new Individual().getNumOfAttributes();
		assertThat(properties.getProperty("chromosome")).as(file.getName()).matches("[01]{" + (genes * 7) + "}");
		assertThat(Double.parseDouble(properties.getProperty("fitness"))).as(file.getName()).isGreaterThanOrEqualTo(0);
	}
}
