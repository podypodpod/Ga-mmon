package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.game.NetPlayer;
import uk.co.davidlomas.gammon.game.PubEval;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

/**
 * EXPERIMENT: evolves NetPlayers with real-valued genes, fitness from a round robin within the population, measuring the
 * fittest against pubeval at checkpoints.
 * args: name hidden population generations seeds mutationRate sigma tournament checkpoints [saveDir]
 */
public class NetExperiment {

	static final int MEETINGS = 1000;

	public static void main(final String[] args) throws IOException {
		final String name = args[0];
		final int hidden = Integer.parseInt(args[1]);
		final int size = Integer.parseInt(args[2]);
		final int generations = Integer.parseInt(args[3]);
		final int seeds = Integer.parseInt(args[4]);
		final double mutationRate = Double.parseDouble(args[5]);
		final double sigma = Double.parseDouble(args[6]);
		final int tournament = Integer.parseInt(args[7]);
		final int[] checkpoints = Arrays.stream(args[8].split(",")).mapToInt(Integer::parseInt).toArray();
		final String saveDir = args.length > 9 ? args[9] : null;

		GenAlgSettings.setGamesPerPairing(2);
		GameSettings.setAreBothAIsCache(true);
		GameSettings.setIsP1BlackCache(true);
		GameSettings.setDisplayGUICache(false);
		GameSettings.setTimeDelayCache(0);
		GameSettings.setMultiThreadingCache(true);
		GameSettings.setThreadsCache(0);

		final long[] meetingSeeds = new long[MEETINGS];
		for (int i = 0; i < MEETINGS; i++) {
			meetingSeeds[i] = 9_000_000 + i;
		}
		final int genes = NetPlayer.size(hidden);
		final double[][] results = new double[checkpoints.length][seeds];
		final long start = System.nanoTime();
		for (int seed = 0; seed < seeds; seed++) {
			final Random random = new Random(7000 + seed);
			RandomSource.setSeed(7000 + seed);
			Population pop = new Population(size, false);
			for (int i = 0; i < size; i++) {
				pop.saveIndividual(i, new NetPlayer(hidden, randomGenes(random, genes), randomGenes(random, genes)));
			}
			FitnessCalculator.calculateFitnessOfPopulation(pop);
			int next = 0;
			for (int generation = 1; generation <= generations; generation++) {
				final Population children = new Population(size, false);
				final NetPlayer elite = (NetPlayer) pop.getFittest();
				children.saveIndividual(0, new NetPlayer(hidden, elite.contact.clone(), elite.race.clone()));
				for (int i = 1; i < size; i++) {
					final NetPlayer a = pick(pop, random, tournament);
					final NetPlayer b = pick(pop, random, tournament);
					children.saveIndividual(i, new NetPlayer(hidden,
							child(a.contact, b.contact, random, mutationRate, sigma),
							child(a.race, b.race, random, mutationRate, sigma)));
				}
				pop = children;
				FitnessCalculator.calculateFitnessOfPopulation(pop);
				if (next < checkpoints.length && generation == checkpoints[next]) {
					final NetPlayer best = (NetPlayer) pop.getFittest();
					results[next][seed] = FitnessCalculator.averagePoints(best, new PubEval(), meetingSeeds, null);
					System.out.printf(Locale.ROOT, "%s seed %d gen %d: %+.3f against pubeval (%.0f s)%n", name, seed,
							generation, results[next][seed], (System.nanoTime() - start) / 1e9);
					if (saveDir != null) {
						save(Path.of(saveDir, name + "-" + seed + "-gen" + generation), best);
					}
					next++;
				}
			}
		}
		final StringBuilder row = new StringBuilder("SUMMARY " + name + " against pubeval:");
		for (int c = 0; c < checkpoints.length; c++) {
			row.append(String.format(Locale.ROOT, "  gen %d %+.3f", checkpoints[c],
					Arrays.stream(results[c]).average().orElse(0)));
		}
		row.append(String.format(Locale.ROOT, "  (%.0f s)", (System.nanoTime() - start) / 1e9));
		System.out.println(row);
	}

	private static float[] randomGenes(final Random random, final int genes) {
		final float[] w = new float[genes];
		for (int i = 0; i < genes; i++) {
			w[i] = (float) (random.nextGaussian() * 0.1);
		}
		return w;
	}

	private static NetPlayer pick(final Population pop, final Random random, final int tournament) {
		NetPlayer best = null;
		for (int i = 0; i < tournament; i++) {
			final NetPlayer p = (NetPlayer) pop.individuals[random.nextInt(pop.size())];
			if (best == null || p.getFitness() > best.getFitness()) {
				best = p;
			}
		}
		return best;
	}

	private static float[] child(final float[] a, final float[] b, final Random random, final double rate,
								 final double sigma) {
		final float[] w = new float[a.length];
		for (int i = 0; i < w.length; i++) {
			w[i] = random.nextBoolean() ? a[i] : b[i];
			if (random.nextDouble() < rate) {
				w[i] += (float) (random.nextGaussian() * sigma);
			}
		}
		return w;
	}

	private static void save(final Path file, final NetPlayer player) throws IOException {
		try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
			out.println("hidden " + player.hidden);
			for (final float v : player.contact) {
				out.print(v + " ");
			}
			out.println();
			for (final float v : player.race) {
				out.print(v + " ");
			}
			out.println();
		}
	}
}
