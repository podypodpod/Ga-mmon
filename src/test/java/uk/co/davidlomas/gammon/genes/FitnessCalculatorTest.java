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
	public void eachPlayersFitnessIsTheAverageOfBothGamesInEveryMeeting() {
		// Given two players, who meet twice
		GenAlgSettings.setGamesPerPairing(2);
		RandomSource.setSeed(3);
		final Population population = new Population(2, true);
		final Individual first = population.individuals[0];
		final Individual second = population.individuals[1];

		// When their fitness is worked out
		RandomSource.setSeed(4);
		FitnessCalculator.calculateFitnessOfPopulation(population);

		// Then it's the average of all four games, as player one and as player two. Replaying them with the same
		// seed, each meeting's two games rolling the same dice, gives the scores
		RandomSource.setSeed(4);
		final GameManager gameManager = new GameManager();
		double firstTotal = 0;
		double secondTotal = 0;
		for (int meeting = 0; meeting < 2; meeting++) {
			final long dice = RandomSource.nextLong();
			RandomSource.setDiceSeed(dice);
			final GameStats firstGoesFirst = gameManager.playIndividualsVsEachOther(first, second);
			RandomSource.setDiceSeed(dice);
			final GameStats secondGoesFirst = gameManager.playIndividualsVsEachOther(second, first);
			firstTotal += firstGoesFirst.getPlayerOneScore() + secondGoesFirst.getPlayerTwoScore();
			secondTotal += firstGoesFirst.getPlayerTwoScore() + secondGoesFirst.getPlayerOneScore();
		}
		assertEquals(firstTotal / 4, first.getFitness(), 1e-9);
		assertEquals(secondTotal / 4, second.getFitness(), 1e-9);
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
