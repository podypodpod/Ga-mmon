package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	}

	@Test
	public void fitnessIsTheAverageScoreFromPlayingEveryoneElseOnce() {
		// Given a population of three
		RandomSource.setSeed(3);
		final Population population = new Population(3, true);
		final Individual[] individuals = population.individuals;

		// When their fitness is worked out
		RandomSource.setSeed(4);
		FitnessCalculator.calculateFitnessOfPopulation(population);

		// Then each one's fitness is the average of its scores, as player one, against the other two.
		// Replaying the same games in the same order with the same seed gives those scores.
		RandomSource.setSeed(4);
		final GameManager gameManager = new GameManager();
		for (final Individual player : individuals) {
			double total = 0;
			for (final Individual opponent : individuals) {
				if (opponent != player) {
					total += gameManager.playIndividualsVsEachOther(player, opponent).getPlayerOneScore();
				}
			}
			assertEquals(total / 2, player.getFitness(), 1e-9);
		}
	}

	@Test
	public void theWinnerOfAGameIsOneOfTheTwoPlayers() {
		RandomSource.setSeed(5);
		final Individual first = new Individual();
		final Individual second = new Individual();

		final Individual winner = FitnessCalculator.getWinnerOf(first, second);

		assertTrue(winner == first || winner == second);
	}
}
