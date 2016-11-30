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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.davidlomas.gammon.settings.GenAlgSettings;

/**
 * Main
 *
 * The main class to run for the genetic algorithm to run
 *
 * will run for the size and generations set in
 * backgammon.settings/GenAlgSettings
 *
 * @author David Lomas
 */
public class StartGa {

	final static Logger logger = LoggerFactory.getLogger(StartGa.class);

	public static void main(final String args[]) {

		GenAlgSettings.setAttemptCount(GenAlgSettings.getAttemptCount() + 1);

		logger.info("Program running!");

		Population pop = createInitialPopulation();
		saveInitialFitest(pop);
		pop = initialEvolution(pop);
		pop = restOfEvolutions(pop);

		logger.info("------------Finished Evolving-------");
		logger.info("Solution:");
		logger.info(pop.getFittest().toString());
	}

	private static Population createInitialPopulation() {
		final Population pop = new Population(GenAlgSettings.getPopulationSize(), true);

		logger.info("Initial Population created size={} generations={}", GenAlgSettings.getPopulationSize(),
				GenAlgSettings.getGenerations());
		return pop;
	}

	private static Population restOfEvolutions(Population pop) {
		Individual fittest;
		for (int x = 0; x < GenAlgSettings.getGenerations() - 1; x++) {
			logger.info("-------------------------");
			logger.info("Started evolving population");
			pop = GeneticAlgorithm.evolvePopulation(pop);
			System.out.println("Calculating fitness");
			fittest = pop.getFittest();
			fittest.saveToFile(fittest.getFilePathForPlayers() + "/PlayerFromGen" + (x + 1));
			logger.info("Finished evolving population, evolution {}", x + 1);
		}
		return pop;
	}

	private static Population initialEvolution(Population pop) {
		Individual fittest;
		logger.info("-------------------------");
		logger.info("Started evolving population");
		pop = GeneticAlgorithm.evolvePopulation(pop);
		fittest = pop.getFittest();
		fittest.saveToFile(fittest.getFilePathForPlayers() + "/PlayerFromGen" + 0);
		logger.info("Finished evolving population, evolution 0");
		return pop;
	}

	private static void saveInitialFitest(final Population pop) {
		// save initial fitest
		logger.info("Calculating fittest of initial pop");
		pop.calculateFitness();
		final Individual fittest = pop.getFittest();
		fittest.saveToFile(fittest.getFilePathForPlayers() + "/FittestFromInitialPopulation");
	}
}