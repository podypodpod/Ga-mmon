package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.game.ReferencePlayer;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.Arrays;
import java.util.Locale;

/**
 * Runs the GA with given settings for several seeds, measuring the fittest against the reference player at checkpoints.
 * args: name populationSize gamesPerPairing tournamentSize mutationRate generations seeds checkpoint,checkpoint,...
 */
public class Experiment {
	static final int MEETINGS = 1000;

	public static void main(final String[] args) {
		final String name = args[0];
		GenAlgSettings.setPopulationSize(Integer.parseInt(args[1]));
		GenAlgSettings.setGamesPerPairing(Integer.parseInt(args[2]));
		GenAlgSettings.setTournamentSize(Integer.parseInt(args[3]));
		GenAlgSettings.setMutationRate(Double.parseDouble(args[4]));
		GenAlgSettings.setUniformRate(0.5);
		GenAlgSettings.setElitism(true);

		GameSettings.setAreBothAIsCache(true);
		GameSettings.setIsP1BlackCache(true);
		GameSettings.setDisplayGUICache(false);
		GameSettings.setTimeDelayCache(0);
		GameSettings.setMultiThreadingCache(true);
		GameSettings.setThreadsCache(0);
		final int generations = Integer.parseInt(args[5]);
		final int seeds = Integer.parseInt(args[6]);
		final int[] checkpoints = Arrays.stream(args[7].split(",")).mapToInt(Integer::parseInt).toArray();

		final long[] meetingSeeds = new long[MEETINGS];
		for (int i = 0; i < MEETINGS; i++) {
			meetingSeeds[i] = 5_000_000 + i;
		}
		final ReferencePlayer reference = new ReferencePlayer();
		final double[][] points = new double[checkpoints.length][seeds];
		final double[][] versusLast = new double[checkpoints.length][seeds];
		final long start = System.nanoTime();
		for (int seed = 0; seed < seeds; seed++) {
			RandomSource.setSeed(9000 + seed);
			Population pop = new Population(GenAlgSettings.getPopulationSize(), true);
			pop.calculateFitness();
			int next = 0;
			final Individual[] saved = new Individual[checkpoints.length];
			for (int generation = 1; generation <= generations; generation++) {
				pop = GeneticAlgorithm.evolvePopulation(pop);
				if (next < checkpoints.length && generation == checkpoints[next]) {
					final long state = RandomSource.nextLong();
					saved[next] = pop.getFittest();
					points[next][seed] = FitnessCalculator.averagePoints(pop.getFittest(), reference, meetingSeeds, null);
					RandomSource.setSeed(state);
					System.out.printf(Locale.ROOT, "%s seed %d gen %d: %+.3f spread %.1f%n", name, seed, generation,
							points[next][seed], RunStats.geneSpread(pop));
					next++;
				}
			}
			final Individual last = saved[checkpoints.length - 1];
			final String out = System.getProperty("gammon.saveTo");
			if (out != null) {
				last.saveToFile(out + "/" + name + "-" + seed);
			}
			for (int c = 0; c < checkpoints.length - 1; c++) {
				versusLast[c][seed] = FitnessCalculator.averagePoints(last, saved[c], meetingSeeds, null);
			}
		}
		final StringBuilder row = new StringBuilder("SUMMARY " + name + ":");
		final StringBuilder h2h = new StringBuilder("LAST v EARLIER " + name + ":");
		for (int c = 0; c < checkpoints.length - 1; c++) {
			final double mean = Arrays.stream(versusLast[c]).average().orElse(0);
			h2h.append(String.format(Locale.ROOT, "  v gen %d %+.3f", checkpoints[c], mean));
		}
		System.out.println(h2h);
		for (int c = 0; c < checkpoints.length; c++) {
			final double mean = Arrays.stream(points[c]).average().orElse(0);
			row.append(String.format(Locale.ROOT, "  gen %d %+.3f", checkpoints[c], mean));
		}
		row.append(String.format(Locale.ROOT, "  (%.0f s)", (System.nanoTime() - start) / 1e9));
		System.out.println(row);
	}
}
