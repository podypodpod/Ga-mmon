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

/**
 * The Class Population.
 *
 * collection of individuals for the genetic algorithm to evolve
 *
 * @author David Lomas
 */
public class Population {

  public Individual[] individuals;

  /**
   * Instantiates a new population.
   *
   * @param populationSize the population size
   * @param initialise the initialise
   */
  public Population(final int populationSize, final boolean initialise) {
    individuals = new Individual[populationSize];

    if (initialise) {
      for (int x = 0; x < populationSize; x++) {
        final Individual newIndividual = new Individual();

        individuals[x] = newIndividual;
      }
    }
  }

  /**
   * calculates fitness of whole population
   *
   * Uses round robin to play all against each other
   */
  public void calculateFitness() {
    FitnessCalculator.calculateFitnessOfPopulation(this);
  }

  /**
   * Gets the fittest.
   *
   * @return the fittest
   */
  public Individual getFittest() {

    Individual fittest = individuals[0];

    for (int x = 1; x < individuals.length; x++) {
      if (fittest.getFitness() <= individuals[x].getFitness()) {
        fittest = individuals[x];
      }
    }
    return fittest;
  }

  public Individual getIndividual(final int i) {
    return individuals[i];
  }

  public Individual[] getPopulation() {
    return individuals;
  }

  public void saveIndividual(final int index, final Individual indiv) {
    individuals[index] = indiv;
  }

  public void setIndividuals(final Individual[] indivs) {
    individuals = indivs;
  }

  public int size() {
    return individuals.length;
  }

  @Override
  public String toString() {
    return "Population of size: " + size();
  }
}
