package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.PrimitiveIterator;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

		RandomSource.setSeed(4);
		final double[] scores = FitnessCalculator.meet(gameManager, first, second, 7);

		// the same two games, played by hand
		RandomSource.setSeed(4);
		RandomSource.setDiceSeed(7);
		final GameStats firstGoesFirst = gameManager.playIndividualsVsEachOther(first, second);
		RandomSource.setDiceSeed(7);
		final GameStats secondGoesFirst = gameManager.playIndividualsVsEachOther(second, first);
		assertEquals(firstGoesFirst.getPlayerOneScore() + secondGoesFirst.getPlayerTwoScore(), scores[0], 1e-9);
		assertEquals(firstGoesFirst.getPlayerTwoScore() + secondGoesFirst.getPlayerOneScore(), scores[1], 1e-9);
	}

	@Test
	public void fitnessIsTheAverageScoreOverEveryGameInEveryMeeting() {
		// Given two players who meet twice, with dice from seeds 7 and then 9
		GenAlgSettings.setGamesPerPairing(2);
		RandomSource.setSeed(3);
		final Population population = new Population(2, true);
		final Individual first = population.individuals[0];
		final Individual second = population.individuals[1];
		final PrimitiveIterator.OfLong diceSeeds = LongStream.of(7, 9).iterator();

		// When their fitness is worked out
		RandomSource.setSeed(4);
		FitnessCalculator.calculateFitnessOfPopulation(population, diceSeeds::nextLong);

		// Then it's their scores from both meetings, four games each, averaged
		RandomSource.setSeed(4);
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
}
