/*
  GNU General Public License

  This file is part of GA-mmon.

  GA-mmon is free software: you can redistribute it and/or modify it under the
  terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
  A PARTICULAR PURPOSE. See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with
  GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.genes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.function.LongSupplier;

/**
 * FitnessCalculator
 * <p>
 * Allows the calculation of fitness of an individual or population
 *
 * @author David Lomas
 */
class FitnessCalculator {

	private final static Logger logger = LoggerFactory.getLogger(FitnessCalculator.class);

	/**
	 * calculateFitnessOfPopulation
	 * <p>
	 * calculates the fitness of every individual in the population: the average score from every game it plays.
	 * <p>
	 * Every pair of players meets GenAlgSettings.getGamesPerPairing() times. Each meeting is two games with the same
	 * dice, one with each of them as player one, so they both get the luckier seat, and both players' scores count.
	 * That takes out much of the luck backgammon's dice bring, which would otherwise decide more of the fitness than
	 * the players' skill.
	 *
	 * @param pop - population we are measuring
	 */
	static void calculateFitnessOfPopulation(final Population pop) {
		calculateFitnessOfPopulation(pop, RandomSource::nextLong);
	}

	/**
	 * As calculateFitnessOfPopulation, with the seed for each meeting's dice taken from diceSeeds, so tests can
	 * choose them
	 */
	static void calculateFitnessOfPopulation(final Population pop, final LongSupplier diceSeeds) {

		final GameManager gameManager = new GameManager();
		final int size = pop.individuals.length;
		final int meetings = GenAlgSettings.getGamesPerPairing();
		final double[] totals = new double[size];

		logger.trace("Round robin started");

		for (int first = 0; first < size; first++) {
			for (int second = first + 1; second < size; second++) {
				for (int meeting = 0; meeting < meetings; meeting++) {
					final double[] scores = meet(gameManager, pop.individuals[first], pop.individuals[second],
							diceSeeds.getAsLong());
					totals[first] += scores[0];
					totals[second] += scores[1];
				}
			}
		}

		// each player plays two games a meeting against each of the others
		final int gamesEach = 2 * meetings * (size - 1);
		for (int i = 0; i < size; i++) {
			pop.individuals[i].setFitness(gamesEach == 0 ? 0 : totals[i] / gamesEach);
		}
	}

	/**
	 * One meeting: two games with the same dice, one with each player as player one
	 *
	 * @return each player's total score from the two games, first then second
	 */
	static double[] meet(final GameManager gameManager, final Individual first, final Individual second,
						 final long diceSeed) {
		RandomSource.setDiceSeed(diceSeed);
		final GameStats firstAsPlayerOne = gameManager.playIndividualsVsEachOther(first, second);

		RandomSource.setDiceSeed(diceSeed);
		final GameStats secondAsPlayerOne = gameManager.playIndividualsVsEachOther(second, first);

		return new double[]{firstAsPlayerOne.getPlayerOneScore() + secondAsPlayerOne.getPlayerTwoScore(),
				firstAsPlayerOne.getPlayerTwoScore() + secondAsPlayerOne.getPlayerOneScore()};
	}
}
