package uk.co.davidlomas.gammon.genes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.genes.SearchProgress.GenerationResult;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * What the searches so far have saved: for each attempt, the GA settings it ran with, its stats for each generation,
 * and the players. Anything that can't be read is left out, as one bad file shouldn't stop the rest being shown.
 */
public final class SavedAttempts {

	static final String SETTINGS_FILE = "gaSettings.properties";
	static final String STATS_FILE = "runStats.csv";
	private static final String STARTED = "started";

	private static final Logger logger = LoggerFactory.getLogger(SavedAttempts.class);

	/**
	 * One search's results
	 *
	 * @param settings    the GA settings it ran with, in the order they were saved, empty for one from before they were
	 * @param started     when it started, null if that can't be told
	 * @param generations its stats for each generation, in the order they ran
	 */
	public record Attempt(int number, File folder, Map<String, String> settings, Instant started,
						  List<GenerationResult> generations) {

		/**
		 * @return the generation's best player, null if it wasn't saved or can't be read
		 */
		public Individual player(final String generation) {
			return load(folder, generation);
		}

		/**
		 * @return its best points a game against the reference player, null if that was never measured
		 */
		public Double bestPointsAgainstReference() {
			return generations.stream().map(GenerationResult::pointsAgainstReference).filter(p -> p != null)
					.max(Double::compare).orElse(null);
		}
	}

	private SavedAttempts() {
	}

	/**
	 * @return every attempt saved, newest first
	 */
	public static List<Attempt> all() {
		return all(new File(Individual.PLAYERS_DIR));
	}

	static List<Attempt> all(final File folder) {
		final List<Attempt> attempts = new ArrayList<>();
		final File[] folders = folder.listFiles(File::isDirectory);
		if (folders != null) {
			for (final File attempt : folders) {
				final Matcher name = Individual.ATTEMPT_FOLDER.matcher(attempt.getName());
				if (name.matches()) {
					attempts.add(read(attempt, Integer.parseInt(name.group(1))));
				}
			}
		}
		attempts.sort(Comparator.comparingInt(Attempt::number).reversed());
		return attempts;
	}

	private static Attempt read(final File folder, final int number) {
		final Map<String, String> settings = settings(folder);
		final String started = settings.remove(STARTED);
		Instant when = null;
		if (started != null) {
			try {
				when = Instant.parse(started);
			} catch (final RuntimeException e) {
				logger.warn("Couldn't read when {} started: {}", folder, started);
			}
		}
		if (when == null) {
			when = created(folder);
		}
		return new Attempt(number, folder, settings, when, generations(folder));
	}

	/**
	 * @return when the folder was made, which is when the attempt started, for one from before that was saved. Null if
	 * the system can't say
	 */
	private static Instant created(final File folder) {
		try {
			return Files.readAttributes(folder.toPath(), BasicFileAttributes.class).creationTime().toInstant();
		} catch (final IOException | UnsupportedOperationException e) {
			return null;
		}
	}

	/**
	 * Saves the GA settings a search is running with in its folder, as the settings file can change between searches
	 *
	 * @throws UncheckedIOException if they can't be saved
	 */
	static void saveSettings(final String folder, final Instant started) {
		final Map<String, String> settings = new LinkedHashMap<>();
		settings.put(STARTED, started.toString());
		settings.put("populationSize", String.valueOf(GenAlgSettings.getPopulationSize()));
		settings.put("generations", String.valueOf(GenAlgSettings.getGenerations()));
		settings.put("gamesPerPairing", String.valueOf(GenAlgSettings.getGamesPerPairing()));
		settings.put("tournamentSize", String.valueOf(GenAlgSettings.getTournamentSize()));
		settings.put("uniformRate", String.valueOf(GenAlgSettings.getUniformRate()));
		settings.put("mutationRate", String.valueOf(GenAlgSettings.getMutationRate()));
		settings.put("elitism", String.valueOf(GenAlgSettings.isElitism()));
		// written line by line, as Properties would save them in no particular order. None of them need escaping
		final StringBuilder file = new StringBuilder("# The GA settings this attempt ran with\n");
		settings.forEach((key, value) -> file.append(key).append('=').append(value).append('\n'));
		try {
			Files.writeString(Path.of(folder, SETTINGS_FILE), file);
		} catch (final IOException e) {
			throw new UncheckedIOException("Couldn't save the settings in " + folder, e);
		}
	}

	/**
	 * @return the settings, in the order they were saved, empty if there aren't any to read
	 */
	private static Map<String, String> settings(final File folder) {
		final Path file = folder.toPath().resolve(SETTINGS_FILE);
		final Map<String, String> settings = new LinkedHashMap<>();
		if (!Files.isRegularFile(file)) {
			return settings;
		}
		try (InputStream in = Files.newInputStream(file)) {
			final Properties saved = new Properties();
			saved.load(in);
			// Properties forgets the order, so it's taken from the file's lines
			for (final String line : Files.readAllLines(file)) {
				final int equals = line.indexOf('=');
				if (!line.startsWith("#") && equals > 0) {
					final String key = line.substring(0, equals).trim();
					if (saved.containsKey(key)) {
						settings.put(key, saved.getProperty(key));
					}
				}
			}
		} catch (final IOException | IllegalArgumentException e) {
			logger.warn("Couldn't read {}", file, e);
			settings.clear();
		}
		return settings;
	}

	/**
	 * @return its stats, by the names in the file's first line, so a column it doesn't have is left as NaN, or null for
	 * the points against the reference player. A file that can't be read gives as many rows as could be
	 */
	private static List<GenerationResult> generations(final File folder) {
		final Path stats = folder.toPath().resolve(STATS_FILE);
		final List<GenerationResult> generations = new ArrayList<>();
		if (!Files.isRegularFile(stats)) {
			return generations;
		}
		try {
			final List<String> lines = Files.readAllLines(stats);
			if (lines.isEmpty()) {
				return generations;
			}
			final List<String> columns = Arrays.asList(lines.get(0).split(","));
			if (!columns.contains("generation")) {
				return generations;
			}
			for (final String line : lines.subList(1, lines.size())) {
				if (line.isBlank()) {
					continue;
				}
				final String[] cells = line.split(",", -1);
				final String points = cell(cells, columns, "pointsAgainstReference");
				generations.add(new GenerationResult(cell(cells, columns, "generation"),
						number(cells, columns, "bestFitness"), number(cells, columns, "geneSpread"),
						number(cells, columns, "turnsPerGame"), number(cells, columns, "drawRate"),
						number(cells, columns, "singleRate"), number(cells, columns, "gammonRate"),
						number(cells, columns, "backgammonRate"),
						points == null || points.isBlank() ? null : Double.parseDouble(points)));
			}
		} catch (final IOException | NumberFormatException e) {
			logger.warn("Couldn't read all of {}", stats, e);
		}
		return generations;
	}

	private static String cell(final String[] cells, final List<String> columns, final String column) {
		final int index = columns.indexOf(column);
		return index < 0 || index >= cells.length ? null : cells[index];
	}

	private static double number(final String[] cells, final List<String> columns, final String column) {
		final String value = cell(cells, columns, column);
		return value == null || value.isBlank() ? Double.NaN : Double.parseDouble(value);
	}

	/**
	 * @return the file a generation's best player is saved in, within its attempt's folder
	 */
	static String fileName(final String generation) {
		return SavedPlayers.INITIAL.equals(generation) ? "FittestFromInitialPopulation" : "PlayerFromGen" + generation;
	}

	/**
	 * @return the player, null if it can't be read, such as one saved with different genes by an older version
	 */
	static Individual load(final File attempt, final String generation) {
		final File file = new File(attempt, fileName(generation));
		if (!file.isFile()) {
			return null;
		}
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
