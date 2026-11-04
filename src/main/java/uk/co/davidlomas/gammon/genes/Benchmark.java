package uk.co.davidlomas.gammon.genes;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.ReferencePlayer;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Measures how strong the GA's players are and how fast games are, so a change can be compared with the commit before
 * it. Run it with ./gradlew benchmark, which adds a row to benchmarks.csv.
 * <p>
 * Strength: the GA is run from a few fixed seeds, with the same settings each time, and each run's fittest player
 * plays the hand-written ReferencePlayer, which never changes. It's their average points a game against it, with the
 * standard error between the seeds' players.
 * <p>
 * Speed: a fixed set of seeded games between two seeded players, on one thread, in games a second. They're played
 * first, as how much of the GA has run changes how the JVM has optimised the code, which moved the speed by far more
 * than anything else. They're timed several times and the fastest kept, as anything else the machine is doing can only
 * slow a pass down. Their results also give a fingerprint, which stays the same while a change doesn't change how games
 * are played.
 * <p>
 * Arguments, all optional: generations (60), seeds (3), meetings with the reference player for each seed (1000), and
 * the csv file to add the row to (benchmarks.csv).
 */
public final class Benchmark {

	private static final int POPULATION = 40;
	private static final int SPEED_MEETINGS = 1000;
	private static final int SPEED_PASSES = 5;
	static final String HEADER = "date,commit,generations,seeds,pointsPerGame,standardError,perSeed,gamesPerSecond,"
			+ "gaSeconds,cores,fingerprint";

	private Benchmark() {
	}

	public static void main(final String[] args) throws Exception {
		LogManager.getRootLogger().setLevel(Level.WARN);
		final int generations = args.length > 0 ? Integer.parseInt(args[0]) : 60;
		final int seeds = args.length > 1 ? Integer.parseInt(args[1]) : 3;
		final int meetings = args.length > 2 ? Integer.parseInt(args[2]) : 1000;
		final Path csv = Path.of(args.length > 3 ? args[3] : "benchmarks.csv");

		final String row = run(generations, seeds, meetings, SPEED_PASSES);
		final String previous = lastRow(csv);
		append(csv, row);

		System.out.println(HEADER);
		if (previous != null) {
			System.out.println(previous + "   <- before");
		}
		System.out.println(row + "   <- now, added to " + csv);
	}

	/**
	 * Runs the benchmark
	 *
	 * @return its row for the csv file
	 */
	static String run(final int generations, final int seeds, final int meetings, final int speedPasses)
			throws Exception {
		// read now, as files edited while it runs aren't what it measured
		final String commit = commit();

		// the same settings every time, whatever conf/ says, so runs can be compared
		GenAlgSettings.setPopulationSize(POPULATION);
		GenAlgSettings.setGamesPerPairing(2);
		GenAlgSettings.setMutationRate(0.02);
		GenAlgSettings.setTournamentSize(8);
		GenAlgSettings.setUniformRate(0.5);
		GenAlgSettings.setElitism(true);
		GameSettings.setAreBothAIsCache(true);
		GameSettings.setIsP1BlackCache(true);
		GameSettings.setDisplayGUICache(false);
		GameSettings.setTimeDelayCache(0);
		GameSettings.setMultiThreadingCache(true);
		GameSettings.setThreadsCache(0);

		final long[] fingerprint = new long[1];
		final double gamesPerSecond = speed(fingerprint, speedPasses);

		final long gaStart = System.nanoTime();
		final double[] perSeed = new double[seeds];
		for (int seed = 0; seed < seeds; seed++) {
			final Individual fittest = evolve(1000 + seed, generations);
			perSeed[seed] = againstReference(fittest, meetings, 2000 + seed);
			System.out.printf(Locale.ROOT, "seed %d: %+.3f points a game against the reference player%n", seed,
					perSeed[seed]);
		}
		final double gaSeconds = (System.nanoTime() - gaStart) / 1e9;

		double mean = 0;
		for (final double points : perSeed) {
			mean += points / seeds;
		}
		double squares = 0;
		for (final double points : perSeed) {
			squares += (points - mean) * (points - mean);
		}
		final double standardError = seeds > 1 ? Math.sqrt(squares / (seeds - 1) / seeds) : 0;

		final StringBuilder seedColumn = new StringBuilder();
		for (final double points : perSeed) {
			seedColumn.append(seedColumn.isEmpty() ? "" : " ").append(String.format(Locale.ROOT, "%+.3f", points));
		}
		return String.join(",", LocalDate.now().toString(), commit, String.valueOf(generations),
				String.valueOf(seeds), String.format(Locale.ROOT, "%+.3f", mean),
				String.format(Locale.ROOT, "%.3f", standardError), seedColumn.toString(),
				String.format(Locale.ROOT, "%.0f", gamesPerSecond), String.format(Locale.ROOT, "%.0f", gaSeconds),
				String.valueOf(Runtime.getRuntime().availableProcessors()), Long.toHexString(fingerprint[0]));
	}

	private static Individual evolve(final long seed, final int generations) {
		RandomSource.setSeed(seed);
		Population pop = new Population(POPULATION, true);
		pop.calculateFitness();
		for (int generation = 0; generation < generations; generation++) {
			pop = GeneticAlgorithm.evolvePopulation(pop);
		}
		return pop.getFittest();
	}

	/**
	 * The player's average points a game against the reference player, over paired meetings on every core
	 */
	private static double againstReference(final Individual player, final int meetings, final long seed) {
		RandomSource.setSeed(seed);
		final ReferencePlayer reference = new ReferencePlayer();
		final long[] meetingSeeds = new long[meetings];
		for (int meeting = 0; meeting < meetings; meeting++) {
			meetingSeeds[meeting] = RandomSource.nextLong();
		}
		return FitnessCalculator.averagePoints(player, reference, meetingSeeds);
	}

	/**
	 * Plays the speed games on this thread several times, after a few to warm up
	 *
	 * @param fingerprint set to a hash of their results
	 *
	 * @param passes      how many times to play them
	 *
	 * @return games a second in the fastest pass
	 * @throws IllegalStateException if the passes' results differ, as the same seeds have to play the same games
	 */
	private static double speed(final long[] fingerprint, final int passes) {
		RandomSource.setSeed(7);
		final Individual first = new Individual();
		final Individual second = new Individual();
		final GameManager gameManager = new GameManager();
		for (int meeting = 0; meeting < SPEED_MEETINGS / 10; meeting++) {
			FitnessCalculator.meet(gameManager, first, second, -meeting);
		}
		double fastest = 0;
		for (int pass = 0; pass < passes; pass++) {
			long hash = 17;
			final long start = System.nanoTime();
			for (int meeting = 0; meeting < SPEED_MEETINGS; meeting++) {
				final double[] scores = FitnessCalculator.meet(gameManager, first, second, meeting);
				hash = 31 * hash + Double.hashCode(scores[0]);
			}
			final double gamesPerSecond = 2 * SPEED_MEETINGS / ((System.nanoTime() - start) / 1e9);
			System.out.printf(Locale.ROOT, "speed pass %d: %.0f games a second%n", pass, gamesPerSecond);
			fastest = Math.max(fastest, gamesPerSecond);
			if (pass > 0 && hash != fingerprint[0]) {
				throw new IllegalStateException("The same seeds played different games in pass " + pass);
			}
			fingerprint[0] = hash;
		}
		return fastest;
	}

	/**
	 * The commit being measured, marked -dirty if there are changes not yet committed other than to benchmarks.csv,
	 * or "unknown" without git
	 */
	private static String commit() {
		try {
			final String hash = git("rev-parse", "--short", "HEAD");
			if (hash.isEmpty()) {
				return "unknown";
			}
			final boolean dirty = !git("status", "--porcelain", "--", ".", ":!benchmarks.csv").isEmpty();
			return dirty ? hash + "-dirty" : hash;
		} catch (final IOException e) {
			return "unknown";
		}
	}

	private static String git(final String... args) throws IOException {
		final List<String> command = new ArrayList<>(List.of("git"));
		command.addAll(List.of(args));
		final Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
		try (InputStream out = process.getInputStream()) {
			final String text = new String(out.readAllBytes(), StandardCharsets.UTF_8).trim();
			return process.waitFor() == 0 ? text : "";
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return "";
		}
	}

	private static String lastRow(final Path csv) throws IOException {
		if (!Files.exists(csv)) {
			return null;
		}
		final List<String> lines = Files.readAllLines(csv);
		return lines.size() > 1 ? lines.get(lines.size() - 1) : null;
	}

	static void append(final Path csv, final String row) throws IOException {
		if (!Files.exists(csv)) {
			Files.writeString(csv, HEADER + "\n");
		}
		Files.writeString(csv, row + "\n", StandardOpenOption.APPEND);
	}
}
