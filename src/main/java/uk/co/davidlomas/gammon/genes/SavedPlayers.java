package uk.co.davidlomas.gammon.genes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * The players earlier searches have saved. Fitness only says how a player did against its own population, so it can't
 * compare players from different searches. Their points against the reference player can, as that's the same opponent
 * and the same dice every time, so that's what best goes by. It's only measured every few generations, so only those
 * players can be picked.
 */
public final class SavedPlayers {

	private static final Logger logger = LoggerFactory.getLogger(SavedPlayers.class);
	private static final String INITIAL = "initial";
	private static final String POINTS_COLUMN = "pointsAgainstReference";

	/**
	 * A player from file
	 *
	 * @param name                   where it came from, such as "Attempt 3, generation 40"
	 * @param pointsAgainstReference its average points a game against the reference player
	 */
	public record SavedPlayer(String name, Individual player, double pointsAgainstReference) {
	}

	private record Measured(int attempt, String generation, double points) {
	}

	private SavedPlayers() {
	}

	/**
	 * @return the best players on file, best first, fewer than count if there aren't that many. Players with the same
	 * genes only count once, which is common as the best of one generation is carried over to the next
	 */
	public static List<SavedPlayer> best(final int count) {
		return best(new File(Individual.PLAYERS_DIR), count);
	}

	static List<SavedPlayer> best(final File folder, final int count) {
		final List<Measured> measured = new ArrayList<>();
		final File[] attempts = folder.listFiles(File::isDirectory);
		if (attempts != null) {
			for (final File attempt : attempts) {
				final Matcher name = Individual.ATTEMPT_FOLDER.matcher(attempt.getName());
				if (name.matches()) {
					measured.addAll(measuredIn(attempt, Integer.parseInt(name.group(1))));
				}
			}
		}
		// by attempt and then file order within the same points, so the same files always give the same answer
		measured.sort(Comparator.comparingDouble(Measured::points).reversed().thenComparingInt(Measured::attempt));

		final List<SavedPlayer> best = new ArrayList<>();
		final Set<String> genes = new HashSet<>();
		for (final Measured candidate : measured) {
			if (best.size() == count) {
				break;
			}
			final Individual player = load(new File(folder, "Attempt - " + candidate.attempt()), candidate.generation());
			if (player != null && genes.add(String.valueOf(player.getChromosome()))) {
				best.add(new SavedPlayer(name(candidate), player, candidate.points()));
			}
		}
		return best;
	}

	/**
	 * @return the file a generation's best player is saved in, within its attempt's folder
	 */
	static String fileName(final String generation) {
		return INITIAL.equals(generation) ? "FittestFromInitialPopulation" : "PlayerFromGen" + generation;
	}

	private static String name(final Measured player) {
		return "Attempt " + player.attempt() + ", "
				+ (INITIAL.equals(player.generation()) ? "initial population" : "generation " + player.generation());
	}

	/**
	 * @return the generations measured against the reference player. An attempt whose stats can't be read has none, as
	 * one bad file shouldn't stop the others being used
	 */
	private static List<Measured> measuredIn(final File attempt, final int number) {
		final Path stats = attempt.toPath().resolve("runStats.csv");
		final List<Measured> measured = new ArrayList<>();
		if (!Files.isRegularFile(stats)) {
			return measured;
		}
		try {
			final List<String> lines = Files.readAllLines(stats);
			final int column = lines.isEmpty() ? -1 : Arrays.asList(lines.get(0).split(",")).indexOf(POINTS_COLUMN);
			if (column < 0) {
				return measured;
			}
			for (final String line : lines.subList(1, lines.size())) {
				final String[] cells = line.split(",", -1);
				if (cells.length > column && !cells[column].isBlank()) {
					measured.add(new Measured(number, cells[0], Double.parseDouble(cells[column])));
				}
			}
		} catch (final IOException | NumberFormatException e) {
			logger.warn("Couldn't read {}", stats, e);
		}
		return measured;
	}

	/**
	 * @return the player, null if it can't be read, such as one saved with different genes by an older version
	 */
	private static Individual load(final File attempt, final String generation) {
		final File file = new File(attempt, fileName(generation));
		try {
			final Individual player = new Individual();
			player.loadFromFile(file.getPath());
			return player;
		} catch (final RuntimeException e) {
			logger.warn("Couldn't use {}: {}", file, e.getMessage());
			return null;
		}
	}
}
