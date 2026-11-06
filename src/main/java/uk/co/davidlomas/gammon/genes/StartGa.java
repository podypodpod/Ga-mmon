package uk.co.davidlomas.gammon.genes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

/**
 * Main
 * <p>
 * The main class to run for the genetic algorithm to run
 * <p>
 * will run for the size and generations set in
 * conf/GenAlgSettings.properties
 *
 * @author David Lomas
 */
public class StartGa {

	private final static Logger logger = LoggerFactory.getLogger(StartGa.class);

	/**
	 * The main for starting the genetic algorithm
	 *
	 * @param args command line arguments
	 */
	public static void main(final String args[]) {

		GenAlgSettings.check();
		GenAlgSettings.setAttemptCount(Individual.lastAttempt() + 1);

		logger.info("Program running!");

		Population pop = createInitialPopulation();
		saveInitialFittestToFile(pop);
		final RunStats stats = new RunStats(pop.getFittest().getFilePathForPlayers() + "/runStats.csv");
		stats.record("initial", pop, true);
		pop = evolve(pop, stats);

		logger.info("------------Finished Evolving-------");
		logger.info("Solution:");
		logger.info(pop.getFittest().toString());
	}

	private static Population createInitialPopulation() {
		logger.info("Creating Initial Population with size={} generations={}", GenAlgSettings.getPopulationSize(),
				GenAlgSettings.getGenerations());

		return new Population(GenAlgSettings.getPopulationSize(), true);
	}

	private static Population evolve(Population pop, final RunStats stats) {
		final int generations = GenAlgSettings.getGenerations();
		for (int generation = 0; generation < generations; generation++) {
			logger.info("-------------------------");
			logger.info("Started evolving population");
			pop = GeneticAlgorithm.evolvePopulation(pop);
			final Individual fittest = pop.getFittest();
			fittest.saveToFile(fittest.getFilePathForPlayers() + "/PlayerFromGen" + generation);
			stats.record(String.valueOf(generation), pop,
					generation % RunStats.REFERENCE_EVERY == 0 || generation == generations - 1);
			logger.info("Finished evolving population, evolution {}", generation);
		}
		return pop;
	}

	private static void saveInitialFittestToFile(final Population pop) {
		// save initial fittest
		logger.info("Calculating fittest of initial pop");
		pop.calculateFitness();
		final Individual fittest = pop.getFittest();
		fittest.saveToFile(fittest.getFilePathForPlayers() + "/FittestFromInitialPopulation");
	}
}
