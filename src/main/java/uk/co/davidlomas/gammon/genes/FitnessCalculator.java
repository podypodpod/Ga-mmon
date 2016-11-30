/**
 * GNU General Public License
 *
 * This file is part of GA-mmon.
 *
 * GA-mmon is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

/**
 * FitnessCalculator
 *
 * Allows the calculation of fitness of an indiviudal or population
 *
 * @author David Lomas - 11035527
 */
public class FitnessCalculator {

	private static GameManager gameManager;

	/**
	 * calculateFitnessOfPopulation
	 *
	 * calculates the fitness of every individual in the population.
	 *
	 * requires playing a large number of games ((population-1)^2)
	 *
	 * @param pop
	 *            - population we are measuring
	 */
	public static void calculateFitnessOfPopulation(final Population pop) {

		Individual testSubject;
		Individual oponent;
		double tempFitness;

		gameManager = new GameManager();

		if (GenAlgSettings.isDisplayConsole()) {
			System.out.println("Round robin started");
		}

		// looping the whole population, x is the one we are measuring
		for (int x = 0; x < pop.individuals.length; x++) {

			if (GenAlgSettings.isDisplayConsole()) {
				System.out.println("Testing Player: " + x);
			}

			// the one we are generating the fitness of
			testSubject = pop.individuals[x];

			// will be added to over the course of the games
			tempFitness = 0;

			// looping the whole population, y is the one currently playing
			// against x
			for (int y = 0; y < pop.individuals.length; y++) {
				// make sure its not playing itself
				if (y != x) {
					if (GenAlgSettings.isDisplayConsole()) {
						System.out.println("\tagainst player: " + (y + 1) + "/" + pop.individuals.length);
					}

					// Opponent individual
					oponent = pop.individuals[y];

					// adding the result of the game to the temp fitness, so
					// this will add all the games score together
					final GameStats gs = gameManager.playIndividualsVsEachOther(testSubject, oponent);

					tempFitness += gs.getPlayerOneScore();
				}
			}
			if (tempFitness != 0) {
				// divides by the number of games and therefore gets an average
				tempFitness = tempFitness / (pop.size() - 1);
			}
			testSubject.setFitness(tempFitness);
		}
	}

	/**
	 * getWinnerOf
	 *
	 * Makes the 2 players play against each other and returns the winner
	 *
	 * @param i1
	 *            player 1
	 * @param i2
	 *            player 2
	 * @return the winner
	 */
	public static Individual getWinnerOf(final Individual i1, final Individual i2) {
		gameManager = new GameManager();
		final GameStats gs = gameManager.playIndividualsVsEachOther(i1, i2);
		return gs.getVictor();
	}
}