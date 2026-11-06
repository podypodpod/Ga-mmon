package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.PrimitiveIterator;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FitnessCalculatorTest {
	@BeforeAll
	public static void setup() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
		GenAlgSettings.setGamesPerPairing(1);
	}

	@Test
	public void aMeetingIsTwoGamesWithTheSameDiceOneWithEachPlayerFirst() {
		RandomSource.setSeed(3);
		final Individual first = new Individual();
		final Individual second = new Individual();
		final GameManager gameManager = new GameManager();

		final double[] scores = FitnessCalculator.meet(gameManager, first, second, 7);

		// the same two games, played by hand: the meeting's seed sets the players' choices, and gives the dice seed
		RandomSource.setSeed(7);
		final long dice = RandomSource.nextLong();
		RandomSource.setDiceSeed(dice);
		final GameStats firstGoesFirst = gameManager.playIndividualsVsEachOther(first, second);
		RandomSource.setDiceSeed(dice);
		final GameStats secondGoesFirst = gameManager.playIndividualsVsEachOther(second, first);
		assertEquals(firstGoesFirst.getPlayerOneScore() + secondGoesFirst.getPlayerTwoScore(), scores[0], 1e-9);
		assertEquals(firstGoesFirst.getPlayerTwoScore() + secondGoesFirst.getPlayerOneScore(), scores[1], 1e-9);
	}

	@Test
	public void fitnessIsTheAverageScoreOverEveryGameInEveryMeeting() {
		// Given two players who meet twice, with meetings seeded 7 and then 9
		GenAlgSettings.setGamesPerPairing(2);
		RandomSource.setSeed(3);
		final Population population = new Population(2, true);
		final Individual first = population.individuals[0];
		final Individual second = population.individuals[1];
		final PrimitiveIterator.OfLong meetingSeeds = LongStream.of(7, 9).iterator();

		// When their fitness is worked out
		FitnessCalculator.calculateFitnessOfPopulation(population, meetingSeeds::nextLong);

		// Then it's their scores from both meetings, four games each, averaged
		final GameManager gameManager = new GameManager();
		final double[] firstMeeting = FitnessCalculator.meet(gameManager, first, second, 7);
		final double[] secondMeeting = FitnessCalculator.meet(gameManager, first, second, 9);
		assertEquals((firstMeeting[0] + secondMeeting[0]) / 4, first.getFitness(), 1e-9);
		assertEquals((firstMeeting[1] + secondMeeting[1]) / 4, second.getFitness(), 1e-9);
	}

	@Test
	public void whatOnePlayerWinsAnotherLosesSoTheFitnessesAddUpToNothing() {
		// every game is zero-sum and everyone plays as many games, so the population's fitness adds up to 0
		RandomSource.setSeed(5);
		final Population population = new Population(4, true);

		FitnessCalculator.calculateFitnessOfPopulation(population);

		double total = 0;
		for (final Individual individual : population.individuals) {
			total += individual.getFitness();
		}
		assertEquals(0, total, 1e-9);
	}

	@Test
	public void playingTheMeetingsAtTheSameTimeGivesTheSameFitness() {
		// one at a time, then on every core, then on three threads, from the same seed
		final double[] oneAtATime = fitnessFromSeed(6, false, 0);

		assertArrayEquals(oneAtATime, fitnessFromSeed(6, true, 0));
		assertArrayEquals(oneAtATime, fitnessFromSeed(6, true, 3));
	}

	@Test
	public void watchingTheGamesDoesNotChangeHowTheyPlayOut() {
		RandomSource.setSeed(11);
		final Population pop = new Population(4, true);
		final double[] unwatched = fitnessesFromFixedSeeds(pop, null);

		final SearchProgress progress = new SearchProgress();
		final double[] watched = fitnessesFromFixedSeeds(pop, progress);

		assertArrayEquals(unwatched, watched, 1e-12);
		// 6 pairs, a meeting each, and two games a meeting
		assertEquals(12, progress.getGames());
	}

	private static double[] fitnessesFromFixedSeeds(final Population pop, final SearchProgress progress) {
		final long[] nextSeed = {100};
		FitnessCalculator.calculateFitnessOfPopulation(pop, () -> nextSeed[0]++, progress);
		final double[] fitnesses = new double[pop.size()];
		for (int i = 0; i < pop.size(); i++) {
			fitnesses[i] = pop.getIndividual(i).getFitness();
		}
		return fitnesses;
	}

	@Test
	public void workingOutFitnessDoesNotChangeTheCallersRandomNumbers() {
		// the GA goes on to pick parents and mutate from the same random numbers, so the games mustn't use them up
		final Population population = new Population(3, true);
		RandomSource.setSeed(8);

		FitnessCalculator.calculateFitnessOfPopulation(population, () -> 1L);

		// the next number is the first one seed 8 gives, as if no games had been played
		assertEquals(new java.util.Random(8).nextLong(), RandomSource.nextLong());
	}

	@Test
	public void gamesAreOnlyPlayedInParallelWhenNoOneIsWatchingOrPlaying() {
		GameSettings.setMultiThreadingCache(true);
		try {
			assertTrue(FitnessCalculator.playsInParallel());

			GameSettings.setAreBothAIsCache(false);
			assertFalse(FitnessCalculator.playsInParallel(), "a person can only play one game at a time");
			GameSettings.setAreBothAIsCache(true);

			GameSettings.setDisplayGUICache(true);
			assertFalse(FitnessCalculator.playsInParallel(), "the window only shows one game");
		} finally {
			SettingsUtil.resetSettings();
		}
	}

	private static double[] fitnessFromSeed(final long seed, final boolean multiThreading, final int threads) {
		GameSettings.setMultiThreadingCache(multiThreading);
		GameSettings.setThreadsCache(threads);
		try {
			RandomSource.setSeed(seed);
			final Population population = new Population(5, true);
			FitnessCalculator.calculateFitnessOfPopulation(population);
			final double[] fitness = new double[population.size()];
			for (int i = 0; i < fitness.length; i++) {
				fitness[i] = population.individuals[i].getFitness();
			}
			return fitness;
		} finally {
			GameSettings.setMultiThreadingCache(false);
			GameSettings.setThreadsCache(0);
		}
	}
}
