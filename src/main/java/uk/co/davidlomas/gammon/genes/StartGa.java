package uk.co.davidlomas.gammon.genes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

import java.util.function.Consumer;

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
		run(null);
	}

	/**
	 * Runs the genetic algorithm
	 *
	 * @param progress kept up to date as it goes, null if nothing is watching
	 */
	public static void run(final SearchProgress progress) {

		GenAlgSettings.check();
		GenAlgSettings.setAttemptCount(Individual.lastAttempt() + 1);

		logger.info("Program running!");
		tell(progress, watcher -> watcher.searchStarted(GenAlgSettings.getGenerations()));

		Population pop = createInitialPopulation();
		saveInitialFittestToFile(pop, progress);
		final RunStats stats = new RunStats(pop.getFittest().getFilePathForPlayers() + "/runStats.csv", progress);
		stats.record("initial", pop, true);
		pop = evolve(pop, stats, progress);

		logger.info("------------Finished Evolving-------");
		logger.info("Solution:");
		logger.info(pop.getFittest().toString());
	}

	private static void tell(final SearchProgress progress, final Consumer<SearchProgress> message) {
		if (progress != null) {
			message.accept(progress);
		}
	}

	private static Population createInitialPopulation() {
		logger.info("Creating Initial Population with size={} generations={}", GenAlgSettings.getPopulationSize(),
				GenAlgSettings.getGenerations());

		return new Population(GenAlgSettings.getPopulationSize(), true);
	}

	private static Population evolve(Population pop, final RunStats stats, final SearchProgress progress) {
		final int generations = GenAlgSettings.getGenerations();
		for (int generation = 0; generation < generations; generation++) {
			logger.info("-------------------------");
			logger.info("Started evolving population");
			// numbered from 0, as they are in the stats and the saved players' names
			final String stage = "Generation " + generation + " (" + (generation + 1) + " of " + generations + ")";
			tell(progress, watcher -> watcher.stageStarted(stage));
			pop = GeneticAlgorithm.evolvePopulation(pop, progress);
			final Individual fittest = pop.getFittest();
			fittest.saveToFile(fittest.getFilePathForPlayers() + "/" + SavedPlayers.fileName(String.valueOf(generation)));
			stats.record(String.valueOf(generation), pop,
					generation % RunStats.REFERENCE_EVERY == 0 || generation == generations - 1);
			logger.info("Finished evolving population, evolution {}", generation);
		}
		return pop;
	}

	private static void saveInitialFittestToFile(final Population pop, final SearchProgress progress) {
		// save initial fittest
		logger.info("Calculating fittest of initial pop");
		tell(progress, watcher -> watcher.stageStarted("The initial population"));
		pop.calculateFitness(progress);
		final Individual fittest = pop.getFittest();
		tell(progress, watcher -> watcher.savingIn(fittest.getFilePathForPlayers()));
		fittest.saveToFile(fittest.getFilePathForPlayers() + "/" + SavedPlayers.fileName("initial"));
	}
}
