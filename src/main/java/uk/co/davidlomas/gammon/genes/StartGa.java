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

import uk.co.davidlomas.gammon.settings.GenAlgSettings;

/**
 * Main
 *
 * The main class to run for the genetic algorithm to run
 *
 * will run for the size and generations set in
 * backgammon.settings/genalgsettings
 *
 * @author David Lomas - 11035527
 */
public class StartGa {

	public StartGa() {
		GenAlgSettings.setAtemptCount(GenAlgSettings.getAtemptCount() + 1);

		System.out.println("Program running!");

		Population pop = new Population(GenAlgSettings.getPopulationSize(), true);
		System.out.println("Initial Population created, size: " + GenAlgSettings.getPopulationSize() + ", generations: "
				+ GenAlgSettings.getGenerations());

		// save initial fitest
		System.out.println("Calculating fittest of initial pop");
		pop.calculateFitness();
		Individual fittest = pop.getFittest();
		fittest.saveToFile(fittest.getFilePathForPlayers() + "FittestFromInitialPopulation");

		// evolve once
		System.out.println("-------------------------");
		System.out.println("Evolving population");
		pop = GeneticAlgorithm.evolvePopulation(pop);
		fittest = pop.getFittest();
		fittest.saveToFile(fittest.getFilePathForPlayers() + "PlayerFromGen" + 0);
		System.out.println("--------Evolved!--------- this was population 0");

		for (int x = 0; x < GenAlgSettings.getGenerations() - 1; x++) {
			System.out.println("-------------------------");
			System.out.println("Evolving population");
			pop = GeneticAlgorithm.evolvePopulation(pop);
			System.out.println("Calculating fitness");
			fittest = pop.getFittest();
			fittest.saveToFile(fittest.getFilePathForPlayers() + "PlayerFromGen" + (x + 1));
			System.out.println("--------Evolved!--------- this was population " + (x + 1));
		}

		System.out.println("------------Finished-------");
		System.out.println("Solution:");
		System.out.println(pop.getFittest().toString());
	}
}
