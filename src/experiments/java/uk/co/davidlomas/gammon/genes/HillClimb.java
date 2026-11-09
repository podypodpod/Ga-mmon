package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.game.NetPlayer;
import uk.co.davidlomas.gammon.game.PubEval;
import uk.co.davidlomas.gammon.settings.GameSettings;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

/**
 * EXPERIMENT: Pollack and Blair's evolution for backgammon. A champion plays a mutated copy of itself, and if the
 * challenger wins, the champion moves a small step towards it rather than being replaced, which averages out the luck
 * of the dice. Measures the champion against pubeval at checkpoints.
 * args: name hidden generations meetingsPerGeneration sigma step checkpoints seed [saveDir]
 */
public class HillClimb {

	static final int MEASURE_MEETINGS = 1000;

	public static void main(final String[] args) throws IOException {
		final String name = args[0];
		final int hidden = Integer.parseInt(args[1]);
		final int generations = Integer.parseInt(args[2]);
		final int meetings = Integer.parseInt(args[3]);
		final double sigma = Double.parseDouble(args[4]);
		final double step = Double.parseDouble(args[5]);
		final int[] checkpoints = Arrays.stream(args[6].split(",")).mapToInt(Integer::parseInt).toArray();
		final long seed = Long.parseLong(args[7]);
		final String saveDir = args.length > 8 ? args[8] : null;
		// a saved player to start from, rather than all-zero weights
		final String startFrom = args.length > 9 ? args[9] : null;

		GameSettings.setAreBothAIsCache(true);
		GameSettings.setIsP1BlackCache(true);
		GameSettings.setDisplayGUICache(false);
		GameSettings.setTimeDelayCache(0);
		GameSettings.setMultiThreadingCache(true);
		GameSettings.setThreadsCache(0);

		final long[] measureSeeds = new long[MEASURE_MEETINGS];
		for (int i = 0; i < MEASURE_MEETINGS; i++) {
			measureSeeds[i] = 9_000_000 + i;
		}
		final PubEval pubEval = new PubEval();
		final Random random = new Random(seed);
		final int genes = NetPlayer.size(hidden);
		float[] contact = new float[genes];
		float[] race = new float[genes];
		if (startFrom != null) {
			final java.util.List<String> lines = Files.readAllLines(Path.of(startFrom));
			contact = parse(lines.get(1), genes);
			race = parse(lines.get(2), genes);
			System.out.printf(Locale.ROOT, "%s gen 0: %+.3f against pubeval, the player it starts from%n", name,
					FitnessCalculator.averagePoints(new NetPlayer(hidden, contact, race), pubEval, measureSeeds, null));
		}
		int accepted = 0;
		int next = 0;
		final long start = System.nanoTime();
		final long[] meetingSeeds = new long[meetings];
		for (int generation = 1; generation <= generations; generation++) {
			final float[] challengerContact = mutated(contact, random, sigma);
			final float[] challengerRace = mutated(race, random, sigma);
			for (int i = 0; i < meetings; i++) {
				meetingSeeds[i] = random.nextLong();
			}
			final double challengerPoints = FitnessCalculator.averagePoints(
					new NetPlayer(hidden, challengerContact, challengerRace),
					new NetPlayer(hidden, contact, race), meetingSeeds, null);
			if (challengerPoints > 0) {
				contact = towards(contact, challengerContact, step);
				race = towards(race, challengerRace, step);
				accepted++;
			}
			if (next < checkpoints.length && generation == checkpoints[next]) {
				final NetPlayer champion = new NetPlayer(hidden, contact, race);
				final double points = FitnessCalculator.averagePoints(champion, pubEval, measureSeeds, null);
				System.out.printf(Locale.ROOT, "%s gen %d: %+.3f against pubeval, %d challengers accepted (%.0f s)%n",
						name, generation, points, accepted, (System.nanoTime() - start) / 1e9);
				if (saveDir != null) {
					save(Path.of(saveDir, name + "-gen" + generation), champion);
				}
				next++;
			}
		}
	}

	private static float[] parse(final String line, final int genes) {
		final String[] numbers = line.trim().split("\\s+");
		final float[] w = new float[genes];
		for (int i = 0; i < genes; i++) {
			w[i] = Float.parseFloat(numbers[i]);
		}
		return w;
	}

	private static float[] mutated(final float[] w, final Random random, final double sigma) {
		final float[] m = new float[w.length];
		for (int i = 0; i < w.length; i++) {
			m[i] = (float) (w[i] + random.nextGaussian() * sigma);
		}
		return m;
	}

	private static float[] towards(final float[] from, final float[] to, final double step) {
		final float[] w = new float[from.length];
		for (int i = 0; i < w.length; i++) {
			w[i] = (float) (from[i] + step * (to[i] - from[i]));
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
