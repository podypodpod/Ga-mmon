package uk.co.davidlomas.gammon.genes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.game.ReferencePlayer;
import uk.co.davidlomas.gammon.game.WinType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

/**
 * Writes a row of stats for each generation of a run to a csv file as the run goes, so a long run can be watched, and
 * what's left of one that stopped early can still be read. A failure to write is logged, as it's not worth stopping a
 * run for.
 */
final class RunStats {

	/**
	 * How many generations go between measuring the fittest player against the reference player
	 */
	static final int REFERENCE_EVERY = 10;
	static final String HEADER = "generation,bestFitness,averageFitness,geneSpread,turnsPerGame,drawRate,singleRate,"
			+ "gammonRate,backgammonRate,pointsAgainstReference";

	private static final Logger logger = LoggerFactory.getLogger(RunStats.class);
	private static final int REFERENCE_MEETINGS = 200;

	private final Path file;
	private final ReferencePlayer reference = new ReferencePlayer();
	// the same meetings every time, so a change from one measure to the next is down to the player, not the dice
	private final long[] meetingSeeds = new long[REFERENCE_MEETINGS];

	RunStats(final String file) {
		this.file = Path.of(file);
		for (int meeting = 0; meeting < REFERENCE_MEETINGS; meeting++) {
			meetingSeeds[meeting] = meeting;
		}
		write(HEADER, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * Adds a generation's row. Its fitness has to have been worked out
	 *
	 * @param generation     its name in the file
	 * @param pop            the population
	 * @param checkReference whether to play the fittest against the reference player, which takes a few more games
	 */
	void record(final String generation, final Population pop, final boolean checkReference) {
		final String row = row(generation, pop, checkReference
				? FitnessCalculator.averagePoints(pop.getFittest(), reference, meetingSeeds) : null);
		logger.info("Stats: {}", row);
		write(row, StandardOpenOption.APPEND);
	}

	private void write(final String line, final StandardOpenOption... options) {
		try {
			Files.writeString(file, line + "\n", options);
		} catch (final IOException e) {
			logger.error("Failed writing to {}", file, e);
		}
	}

	static String row(final String generation, final Population pop, final Double pointsAgainstReference) {
		final GameTally games = pop.getGameTally();
		return String.format(Locale.ROOT, "%s,%.4f,%.4f,%.2f,%.1f,%.4f,%.4f,%.4f,%.4f,%s", generation,
				pop.getFittest().getFitness(), averageFitness(pop), geneSpread(pop), games.turnsPerGame(),
				games.rate(WinType.DRAW), games.rate(WinType.SINGLE), games.rate(WinType.GAMMON),
				games.rate(WinType.BACKGAMMON), pointsAgainstReference == null ? ""
						: String.format(Locale.ROOT, "%.3f", pointsAgainstReference));
	}

	private static double averageFitness(final Population pop) {
		double total = 0;
		for (final Individual individual : pop.individuals) {
			total += individual.getFitness();
		}
		return total / pop.size();
	}

	/**
	 * How much the population still differs: the standard deviation of each gene's value across it, averaged over
	 * the genes
	 */
	static double geneSpread(final Population pop) {
		final int genes = pop.individuals[0].getNumOfAttributes();
		double total = 0;
		for (int gene = 0; gene < genes; gene++) {
			double sum = 0;
			double squares = 0;
			for (final Individual individual : pop.individuals) {
				final int value = individual.getAttribute(gene).getValue();
				sum += value;
				squares += (double) value * value;
			}
			final double mean = sum / pop.size();
			total += Math.sqrt(Math.max(0, squares / pop.size() - mean * mean));
		}
		return total / genes;
	}
}
