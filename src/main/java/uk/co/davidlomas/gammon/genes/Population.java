package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.util.RandomSource;

/**
 * The Class Population.
 * <p>
 * collection of individuals for the genetic algorithm to evolve
 *
 * @author David Lomas
 */
public class Population {

	public Individual[] individuals;
	private GameTally gameTally = new GameTally();

	/**
	 * Instantiates a new population.
	 *
	 * @param populationSize the population size
	 * @param initialise     the initialise
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
	 * <p>
	 * Uses round robin to play all against each other
	 */
	public void calculateFitness() {
		FitnessCalculator.calculateFitnessOfPopulation(this);
	}

	/**
	 * As calculateFitness, keeping progress up to date as the games are played
	 *
	 * @param progress null if nothing is watching
	 */
	void calculateFitness(final SearchProgress progress) {
		FitnessCalculator.calculateFitnessOfPopulation(this, RandomSource::nextLong, progress);
	}

	/**
	 * @return what happened in the games played when fitness was last worked out
	 */
	GameTally getGameTally() {
		return gameTally;
	}

	void setGameTally(final GameTally gameTally) {
		this.gameTally = gameTally;
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

	Individual getIndividual(final int i) {
		return individuals[i];
	}

	public Individual[] getPopulation() {
		return individuals;
	}

	void saveIndividual(final int index, final Individual individual) {
		individuals[index] = individual;
	}

	public void setIndividuals(final Individual[] individual) {
		individuals = individual;
	}

	public int size() {
		return individuals.length;
	}

	@Override
	public String toString() {
		return "Population of size: " + size();
	}
}
