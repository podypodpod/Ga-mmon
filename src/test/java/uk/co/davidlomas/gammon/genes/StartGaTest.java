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
import java.nio.file.Files;
import java.util.List;
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
		final int before = Individual.lastAttempt();

		StartGa.main(null);

		// each run is a new attempt, with its own folder
		assertEquals(before + 1, GenAlgSettings.getAttemptCount());
		final File attempt = new File(SettingsUtil.getPlayersDir() + "/Attempt - " + (before + 1));
		for (final String saved : new String[]{"FittestFromInitialPopulation", "PlayerFromGen0", "PlayerFromGen1"}) {
			assertIsAPlayer(new File(attempt, saved));
		}
		assertFalse(new File(attempt, "PlayerFromGen2").exists(), "only two generations were asked for");

		// a row for the initial population and for each generation, all measured as the first and last are
		final List<String> stats = Files.readAllLines(new File(attempt, "runStats.csv").toPath());
		assertEquals(RunStats.HEADER, stats.get(0));
		assertEquals(4, stats.size());
		assertThat(stats.get(1)).startsWith("initial,");
		assertThat(stats.get(2)).startsWith("0,");
		assertThat(stats.get(3)).startsWith("1,");
		for (final String row : stats.subList(1, stats.size())) {
			assertFalse(row.endsWith(","), row + " wasn't measured against the reference player");
		}
	}

	@Test
	public void noGenerationsMeansNoneAreMade() {
		GenAlgSettings.setGenerations(0);
		RandomSource.setSeed(1);

		StartGa.main(null);

		final File attempt = new File(SettingsUtil.getPlayersDir() + "/Attempt - " + GenAlgSettings.getAttemptCount());
		assertTrue(new File(attempt, "FittestFromInitialPopulation").isFile());
		assertFalse(new File(attempt, "PlayerFromGen0").exists());
	}

	/**
	 * The file holds a whole chromosome, as long as a new player's, and the fitness it was saved with. A game scores
	 * from -3 for losing a backgammon to 3 for winning one, so the average is between those
	 */
	private static void assertIsAPlayer(final File file) throws IOException {
		assertTrue(file.isFile(), file + " wasn't saved");
		final Properties properties = new Properties();
		try (InputStream input = new FileInputStream(file)) {
			properties.load(input);
		}
		final int length = new Individual().getChromosome().length;
		assertThat(properties.getProperty("chromosome")).as(file.getName()).matches("[01]{" + length + "}");
		assertThat(Double.parseDouble(properties.getProperty("fitness"))).as(file.getName()).isBetween(-3.0, 3.0);
	}
}
