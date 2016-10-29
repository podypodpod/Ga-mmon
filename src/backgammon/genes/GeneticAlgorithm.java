/**
 * 	GNU General Public License
 * 
 *  This file is part of GA-mmon.
 *  
 *  GA-mmon is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  GA-mmon is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with GA-mmon.  If not, see <http://www.gnu.org/licenses/>.
*/

package backgammon.genes;

import backgammon.settings.GenAlgSettings;

/**
 * The Class GeneticAlgorithm.
 * 
 * Evolves populations using cross over and mutation
 * 
 * selection by tournament
 * 
 * @author David Lomas - 11035527
 */
public class GeneticAlgorithm {

	/**
	 * Evolve population.
	 *
	 * advances the population by one generation
	 * 
	 * uses crossover and mutation to produce the new population
	 *
	 * @param pop
	 *            the population to evolve
	 * @return the new population evolved
	 */
	public static Population evolvePopulation(Population pop) {

		if (GenAlgSettings.isDisplayconsole()) {
			System.out.println("------EVOLVE POPULATION-------------------");
		}

		Population newPopulation = new Population(pop.size(), false);

		// Keep the best individual
		if (GenAlgSettings.isElitism()) {
			newPopulation.saveIndividual(0, pop.getFittest());
		}

		// Crossover population
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
		for (int i = elitismOffset; i < pop.size(); i++) {

			Individual indiv1 = tournamentSelection(pop);
			Individual indiv2 = tournamentSelection(pop);

			Individual newIndiv = crossover(indiv1, indiv2);

			newPopulation.saveIndividual(i, newIndiv);
		}

		// Mutate population
		if (GenAlgSettings.isDisplayconsole()) {
			System.out.println("------MUTATING-------------------");
		}
		for (int i = elitismOffset; i < newPopulation.size(); i++) {
			mutate(newPopulation.getIndividual(i));
		}

		newPopulation.calculateFitness();

		return newPopulation;
	}

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
	private static Individual crossover(Individual indiv1, Individual indiv2) {

		if (GenAlgSettings.isDisplayconsole()) {
			System.out.println("------CROSSOVER-------------------");
		}

		// new Individual
		Individual newIndiv = new Individual();

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
	 * mutate
	 * 
	 * Mutate the individual.
	 *
	 * gets a random number, if its <= to the mutation rate then switch the bit
	 *
	 * @param indiv
	 *            the individual
	 */
	public static void mutate(Individual indiv) {

		// get the indivs string
		char[] indivStr = indiv.getChromosome();

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
	 *            the pop
	 * @return the individual
	 */
	public static Individual tournamentSelection(Population pop) {

		if (GenAlgSettings.isDisplayconsole()) {
			System.out.println("------TOURNAMENT SELECTION-------------------");
		}

		// Create a tournament population
		Population tournament = new Population(GenAlgSettings.getTournamentSize(), false);

		// For each place in the tournament get a random individual
		for (int i = 0; i < GenAlgSettings.getTournamentSize(); i++) {

			int randomId = (int) (Math.random() * pop.size());
			tournament.saveIndividual(i, pop.getIndividual(randomId));
		}

		// Get the fittest via battling against each other
		Individual tournamentVictor = null;

		// until there is a winner
		while (tournamentVictor == null) {

			// create a population for the winner of the round
			Individual[] winnersOfRound = new Individual[tournament.size() / 2];

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
				tournament.setArray(winnersOfRound);
			}
		}
		return tournamentVictor;
	}
}
