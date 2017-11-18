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
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The Class GeneticAlgorithm.
 * <p>
 * Evolves populations using cross over and mutation
 * <p>
 * selection by tournament
 *
 * @author David Lomas
 */
public class GeneticAlgorithm {

	private final static Logger logger = LoggerFactory.getLogger(GeneticAlgorithm.class);

	/**
	 * Crossover.
	 * <p>
	 * Randomly takes one side or the others attributes according to the uniform
	 * rate
	 *
	 * @param individual1 the individual1
	 * @param individual2 the individual2
	 *
	 * @return the individual1
	 */
	private static Individual crossoverIndividuals(final Individual individual1, final Individual individual2) {

		logger.trace("Crossing individuals");

		// new Individual
		final Individual newIndividual = new Individual();

		// loop the individuals attributes
		for (int x = 0; x < individual1.getNumOfAttributes(); x++) {
			if (Math.random() <= GenAlgSettings.getUniformRate()) {
				newIndividual.setAttribute(x, individual1.getAttribute(x));
			} else {
				newIndividual.setAttribute(x, individual2.getAttribute(x));
			}
		}
		return newIndividual;
	}

	/**
	 * Evolve population.
	 * <p>
	 * advances the population by one generation
	 * <p>
	 * uses crossover and mutation to produce the new population
	 *
	 * @param currentPopulation the population to evolve
	 *
	 * @return the new population evolved
	 */
	public static Population evolvePopulation(final Population currentPopulation) {
		logger.info("------EVOLVE POPULATION-------------------");

		final Population newPopulation = new Population(currentPopulation.size(), false);
		carryOverBestPlayer(currentPopulation, newPopulation);
		final int elitismOffset = crossoverPopulation(currentPopulation, newPopulation);
		mutatePopulation(newPopulation, elitismOffset);
		newPopulation.calculateFitness();

		return newPopulation;
	}

	/**
	 * Keeping the best individual
	 * <p>
	 * Elitism
	 */
	private static void carryOverBestPlayer(final Population currentPopulation, final Population newPopulation) {
		if (GenAlgSettings.isElitism()) {
			newPopulation.saveIndividual(0, currentPopulation.getFittest());
		}
	}

	private static int crossoverPopulation(final Population currentPopulation, final Population newPopulation) {
		logger.info("Crossover started");

		final int elitismOffset;
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

			final Individual individual1 = tournamentSelection(currentPopulation);
			final Individual individual2 = tournamentSelection(currentPopulation);

			final Individual newIndividual = crossoverIndividuals(individual1, individual2);

			newPopulation.saveIndividual(i, newIndividual);
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
	 * <p>
	 * Mutate the individual.
	 * <p>
	 * gets a random number, if its less or equal to the mutation rate then
	 * switch the bit
	 *
	 * @param individual the individual
	 */
	public static void mutate(final Individual individual) {

		// get the individuals string
		final char[] individualString = individual.getChromosome();

		// loop through all bits
		for (int x = 0; x < individualString.length; x++) {
			// random chance
			if (Math.random() <= GenAlgSettings.getMutationRate()) {
				// swap the bit over
				if (individualString[x] == '0') {
					individualString[x] = '1';
				} else {
					individualString[x] = '0';
				}
			}
		}
	}

	/**
	 * Tournament selection.
	 * <p>
	 * Selects the individuals for crossover it does this by selecting a random
	 * population and playing them against each other until there is one victor
	 *
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

		// Get the fittest via battling against each other
		Individual tournamentVictor = null;

		// until there is a winner
		while (tournamentVictor == null) {

			// create a population for the winner of the round
			final Individual[] winnersOfRound = new Individual[tournament.size() / 2];

			Individual winnerOfRound;
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
			list.add(i);
		}
		Collections.shuffle(list);
		for (int i = 0; i < GenAlgSettings.getTournamentSize(); i++) {
			tournamentPopulation.saveIndividual(i, initialPopulation.getIndividual(i));
		}
	}
}
