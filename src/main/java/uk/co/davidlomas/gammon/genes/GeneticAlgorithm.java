package uk.co.davidlomas.gammon.genes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.util.RandomSource;

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
	static Individual crossoverIndividuals(final Individual individual1, final Individual individual2) {

		logger.trace("Crossing individuals");

		// new Individual
		final Individual newIndividual = new Individual();

		// loop the individuals attributes
		for (int x = 0; x < individual1.getNumOfAttributes(); x++) {
			// a copy of the parent's gene, not the gene itself, so mutating the child can't change the parent
			final IndividualAttribute parentsGene = RandomSource.nextDouble() <= GenAlgSettings.getUniformRate()
					? individual1.getAttribute(x) : individual2.getAttribute(x);
			newIndividual.setAttribute(x, new IndividualAttribute(parentsGene.getName(), parentsGene.getValue()));
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
			if (RandomSource.nextDouble() <= GenAlgSettings.getMutationRate()) {
				// swap the bit over
				if (individualString[x] == '0') {
					individualString[x] = '1';
				} else {
					individualString[x] = '0';
				}
			}
		}

		// the game plays by the genes, not the chromosome, so they have to be set from the changed bits. Without
		// this, mutation changed nothing about how a player plays, and saving the player put the old bits back
		individual.updateFromBinary();
	}

	/**
	 * Tournament selection.
	 * <p>
	 * Selects the individuals for crossover: picks a few at random from the population, and the fittest of them
	 * wins. The population's fitness has to have been worked out first
	 *
	 * @return the individual
	 */
	public static Individual tournamentSelection(final Population pop) {

		logger.trace("Tournament selection starting");

		// the tournament can't have more players than the population
		final int size = Math.min(GenAlgSettings.getTournamentSize(), pop.size());
		if (size < GenAlgSettings.getTournamentSize()) {
			logger.warn("Tournament size {} is larger than the population size {}, so using {}",
					GenAlgSettings.getTournamentSize(), pop.size(), size);
		}
		final Population tournament = new Population(size, false);
		addTournamentPlayers(pop, tournament);

		// the fittest player wins. Fitness averages many games, so it says far more about the players than the single
		// game each round used to be decided by
		final Individual tournamentVictor = tournament.getFittest();
		logger.trace("Tournament selection ended");
		return tournamentVictor;
	}

	/**
	 * For each place in the tournament get a random individual
	 */
	private static void addTournamentPlayers(final Population initialPopulation,
											 final Population tournamentPopulation) {
		final ArrayList<Integer> list = new ArrayList<>();
		for (int i = 0; i < initialPopulation.size(); i++) {
			list.add(i);
		}
		Collections.shuffle(list, RandomSource.getRandom());
		for (int i = 0; i < tournamentPopulation.size(); i++) {
			tournamentPopulation.saveIndividual(i, initialPopulation.getIndividual(list.get(i)));
		}
	}
}
