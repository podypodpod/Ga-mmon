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

import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.davidlomas.gammon.settings.GenAlgSettings;

/**
 * The Class GeneticAlgorithm.
 *
 * Evolves populations using cross over and mutation
 *
 * selection by tournament
 *
 * @author David Lomas
 */
public class GeneticAlgorithm {

	final static Logger logger = LoggerFactory.getLogger(GeneticAlgorithm.class);

	/**
	 * Crossover.
	 *
	 * Randomly takes one side or the others attributes according to the uniform
	 * rate
	 *
	 * @param indiv1
	 *            the indiv1
	 * @param indiv2
	 *            the indiv2
	 * @return the individual
	 */
	private static Individual crossoverInidividuals(final Individual indiv1, final Individual indiv2) {

		logger.trace("Crossing individuals");

		// new Individual
		final Individual newIndiv = new Individual();

		// loop the individuals attributes
		for (int x = 0; x < indiv1.getNumOfAttributes(); x++) {
			if (Math.random() <= GenAlgSettings.getUniformRate()) {
				newIndiv.setAttribute(x, indiv1.getAttribute(x));
			} else {
				newIndiv.setAttribute(x, indiv2.getAttribute(x));
			}
		}
		return newIndiv;
	}

	/**
	 * Evolve population.
	 *
	 * advances the population by one generation
	 *
	 * uses crossover and mutation to produce the new population
	 *
	 * @param currentPopulation
	 *            the population to evolve
	 * @return the new population evolved
	 */
	public static Population evolvePopulation(final Population currentPopulation) {
		logger.info("------EVOLVE POPULATION-------------------");

		final Population newPopulation = new Population(currentPopulation.size(), false);
		elitism(currentPopulation, newPopulation);
		final int elitismOffset = crossoverPopulation(currentPopulation, newPopulation);
		mutatePopulation(newPopulation, elitismOffset);
		newPopulation.calculateFitness();

		return newPopulation;
	}

	/**
	 * Keeping the best individual
	 *
	 * @param currentPopulation
	 * @param newPopulation
	 */
	private static void elitism(final Population currentPopulation, final Population newPopulation) {
		if (GenAlgSettings.isElitism()) {
			newPopulation.saveIndividual(0, currentPopulation.getFittest());
		}
	}

	private static int crossoverPopulation(final Population currentPopulation, final Population newPopulation) {
		logger.info("Crossover started");

		int elitismOffset;
		if (GenAlgSettings.isElitism()) {
			elitismOffset = 1;
		} else {
			elitismOffset = 0;
		}

		// Loop over the population size and create new individuals with the
		// crossover function
		// the two individuals that are crossed over are the winners of the
		// tournament selection (should provide some of the best players)
		for (int i = elitismOffset; i < currentPopulation.size(); i++) {

			final Individual indiv1 = tournamentSelection(currentPopulation);
			final Individual indiv2 = tournamentSelection(currentPopulation);

			final Individual newIndiv = crossoverInidividuals(indiv1, indiv2);

			newPopulation.saveIndividual(i, newIndiv);
		}

		logger.info("Crossover finished");
		return elitismOffset;
	}

	private static void mutatePopulation(final Population newPopulation, final int elitismOffset) {
		logger.info("Mutating population");
		for (int i = elitismOffset; i < newPopulation.size(); i++) {
			mutate(newPopulation.getIndividual(i));
		}
	}

	/**
	 * mutate
	 *
	 * Mutate the individual.
	 *
	 * gets a random number, if its less or equal to the mutation rate then
	 * switch the bit
	 *
	 * @param indiv
	 *            the individual
	 */
	public static void mutate(final Individual indiv) {

		// get the individuals string
		final char[] indivStr = indiv.getChromosome();

		// loop through all bits
		for (int x = 0; x < indivStr.length; x++) {
			// random chance
			if (Math.random() <= GenAlgSettings.getMutationRate()) {
				// swap the bit over
				if (indivStr[x] == '0') {
					indivStr[x] = '1';
				} else {
					indivStr[x] = '0';
				}
			}
		}
	}

	/**
	 * Tournament selection.
	 *
	 * Selects the individuals for crossover it does this by selecting a random
	 * population and playing them against each other until there is one victor
	 *
	 * @param pop
	 * @return the individual
	 */
	public static Individual tournamentSelection(final Population pop) {

		logger.trace("Tournament selection starting");

		// Create a tournament population
		final Population tournament = new Population(GenAlgSettings.getTournamentSize(), false);

		if (GenAlgSettings.getTournamentSize() <= pop.size()) {
			addTournamentPlayers(pop, tournament);
		} else {
			logger.error("Tournament Size {} cannot be larger than the population size {}",
					GenAlgSettings.getTournamentSize(), GenAlgSettings.getPopulationSize());
		}

		// Get the fitest via battling against each other
		Individual tournamentVictor = null;

		// until there is a winner
		while (tournamentVictor == null) {

			// create a population for the winner of the round
			final Individual[] winnersOfRound = new Individual[tournament.size() / 2];

			Individual winnerOfRound = null;
			// test the individuals
			int counter = 0;
			for (int i = 0; i < tournament.size(); i += 2) {
				winnerOfRound = FitnessCalculator.getWinnerOf(tournament.getIndividual(i),
						tournament.getIndividual(i + 1));
				winnersOfRound[counter++] = winnerOfRound;
			}

			// if there is only 1 left, then it is the winner!
			if (winnersOfRound.length == 1) {
				tournamentVictor = winnersOfRound[0];
			}
			// setting the tournament array to the winners of the round, so now
			// only has the winners in
			else {
				tournament.setIndividuals(winnersOfRound);
			}
		}
		logger.trace("Tournament selection ended");
		return tournamentVictor;
	}

	/**
	 * For each place in the tournament get a random individual
	 */
	private static void addTournamentPlayers(final Population initialPopulation,
			final Population tournamentPopulation) {
		final ArrayList<Integer> list = new ArrayList<>();
		for (int i = 1; i < initialPopulation.size(); i++) {
			list.add(new Integer(i));
		}
		Collections.shuffle(list);
		for (int i = 0; i < GenAlgSettings.getTournamentSize(); i++) {
			tournamentPopulation.saveIndividual(i, initialPopulation.getIndividual(i));
		}
	}
}
