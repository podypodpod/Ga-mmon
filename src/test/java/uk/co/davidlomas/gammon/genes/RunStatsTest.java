package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.game.WinType;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RunStatsTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
		GameSettings.setMultiThreadingCache(false);
	}

	@Test
	public void identicalPlayersHaveNoSpread() {
		assertEquals(0, RunStats.geneSpread(populationWithGenes(40, 40, 40)), 1e-9);
	}

	@Test
	public void theSpreadIsTheStandardDeviationOfEachGeneAveraged() {
		// 40 and 60 are each 10 from their mean of 50, in every gene
		assertEquals(10, RunStats.geneSpread(populationWithGenes(40, 60)), 1e-9);
	}

	@Test
	public void randomGenesSpreadByAbout29() {
		RandomSource.setSeed(1);
		assertEquals(29, RunStats.geneSpread(new Population(200, true)), 1);
	}

	@Test
	public void theTallyAddsUpTheGames() {
		final GameTally tally = new GameTally();
		assertEquals(0, tally.turnsPerGame());
		assertEquals(0, tally.rate(WinType.DRAW));

		tally.add(game(WinType.SINGLE, 40));
		tally.add(game(WinType.SINGLE, 50));
		tally.add(game(WinType.GAMMON, 60));
		tally.add(game(WinType.DRAW, 300));

		assertEquals(4, tally.getGames());
		assertEquals(112.5, tally.turnsPerGame());
		assertEquals(0.5, tally.rate(WinType.SINGLE));
		assertEquals(0.25, tally.rate(WinType.GAMMON));
		assertEquals(0.25, tally.rate(WinType.DRAW));
		assertEquals(0, tally.rate(WinType.BACKGAMMON));
	}

	@Test
	public void workingOutFitnessTalliesEveryGame() {
		RandomSource.setSeed(2);
		final Population population = new Population(4, true);

		population.calculateFitness();

		// six pairs, one meeting each, two games a meeting
		final GameTally tally = population.getGameTally();
		assertEquals(12, tally.getGames());
		assertTrue(tally.turnsPerGame() > 0);
		assertEquals(1, tally.rate(WinType.SINGLE) + tally.rate(WinType.GAMMON) + tally.rate(WinType.BACKGAMMON)
				+ tally.rate(WinType.DRAW), 1e-9);
	}

	@Test
	public void theTallyIsTheSameHoweverManyThreadsPlayTheGames() {
		assertEquals(tallyFromSeed(5, false), tallyFromSeed(5, true));
	}

	@Test
	public void theFileHasAHeaderAndARowForEachGenerationWithTheReferenceOnlyWhenAsked(@TempDir final Path dir)
			throws IOException {
		RandomSource.setSeed(3);
		final Path file = dir.resolve("runStats.csv");
		final Population population = new Population(3, true);
		population.calculateFitness();

		final RunStats stats = new RunStats(file.toString(), null);
		stats.record("initial", population, true);
		stats.record("0", population, false);

		final List<String> lines = Files.readAllLines(file);
		final int columns = RunStats.HEADER.split(",").length;
		assertEquals(RunStats.HEADER, lines.get(0));
		assertEquals(3, lines.size());

		final String[] measured = lines.get(1).split(",", -1);
		assertEquals(columns, measured.length);
		assertEquals("initial", measured[0]);
		assertFalse(measured[columns - 1].isEmpty());

		final String[] notMeasured = lines.get(2).split(",", -1);
		assertEquals(columns, notMeasured.length);
		assertEquals("0", notMeasured[0]);
		assertTrue(notMeasured[columns - 1].isEmpty());
	}

	@Test
	public void measuringAgainstTheReferenceDoesNotChangeTheCallersRandomNumbers(@TempDir final Path dir) {
		RandomSource.setSeed(4);
		final Population population = new Population(3, true);
		population.calculateFitness();
		final RunStats stats = new RunStats(dir.resolve("runStats.csv").toString(), null);

		RandomSource.setSeed(8);
		final long expected = new java.util.Random(8).nextLong();
		stats.record("initial", population, true);

		assertEquals(expected, RandomSource.nextLong());
	}

	private static String tallyFromSeed(final long seed, final boolean multiThreading) {
		GameSettings.setMultiThreadingCache(multiThreading);
		RandomSource.setSeed(seed);
		final Population population = new Population(5, true);
		population.calculateFitness();
		final GameTally tally = population.getGameTally();
		return tally.getGames() + " " + tally.turnsPerGame() + " " + tally.rate(WinType.SINGLE) + " "
				+ tally.rate(WinType.GAMMON) + " " + tally.rate(WinType.BACKGAMMON) + " " + tally.rate(WinType.DRAW);
	}

	private static GameStats game(final WinType ending, final int turns) {
		return new TestGame(ending, turns);
	}

	/**
	 * A game with the given ending and length, without playing one
	 */
	private static final class TestGame extends GameStats {

		private final int turns;

		TestGame(final WinType ending, final int turns) {
			super(null, ending, 0, 0);
			this.turns = turns;
		}

		@Override
		public int getTurns() {
			return turns;
		}
	}

	private static Population populationWithGenes(final int... values) {
		final Population population = new Population(values.length, false);
		for (int i = 0; i < values.length; i++) {
			final Individual individual = new Individual();
			for (int gene = 0; gene < individual.getNumOfAttributes(); gene++) {
				individual.getAttribute(gene).setValue(values[i]);
			}
			population.saveIndividual(i, individual);
		}
		return population;
	}
}
