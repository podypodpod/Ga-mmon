/*
  GNU General Public License

  This file is part of GA-mmon.

  GA-mmon is free software: you can redistribute it and/or modify it under the
  terms of the GNU General Public License as published by the Free
  Software Foundation, either version 3 of the License, or (at your option) any
  later version.

  GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
  A PARTICULAR PURPOSE. See the GNU General Public License for more
  details.

  You should have received a copy of the GNU General Public License
  along with GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GenAlgSettings {

	private static final String ELITISM = "elitism";
	private static final String UNIFORM_RATE = "uniformRate";
	private static final String TOURNAMENT_SIZE = "tournamentSize";
	private static final String POPULATION_SIZE = "populationSize";
	private static final String MUTATION_RATE = "mutationRate";
	private static final String GENERATIONS = "generations";
	private static final String GAMES_PER_PAIRING = "gamesPerPairing";
	// the settings folder can be moved with -Dgammon.confDir, the tests use this to work on a copy
	private static final String FILE_PATH = System.getProperty(GameSettings.CONF_DIR_PROPERTY, "conf") + "/GenAlgSettings.properties";

	private static Boolean elitism = null;

	private static int generations = -1;
	private static int populationSize = -1;
	private static int attemptCount = 0;
	private static int tournamentSize = -1;
	private static double uniformRate = -1;
	private static double mutationRate = -1;
	private static int gamesPerPairing = -1;

	public static void setElitism(final Boolean elitism) {
		GenAlgSettings.elitism = elitism;
	}

	public static void setUniformRate(final double uniformRate) {
		GenAlgSettings.uniformRate = uniformRate;
	}

	public static void setMutationRate(final double mutationRate) {
		GenAlgSettings.mutationRate = mutationRate;
	}

	public static void setTournamentSize(final int tournamentSize) {
		GenAlgSettings.tournamentSize = tournamentSize;
	}

	/**
	 * The number of this run, which picks the folder players are saved in. StartGa sets it from the folders already
	 * there, rather than it being kept in the settings file, so a run doesn't change a file that's in git
	 */
	public static int getAttemptCount() {
		return attemptCount;
	}

	public static int getGenerations() {
		if (generations == -1) {
			generations = intFromFile(GENERATIONS);
		}
		return generations;
	}

	public static double getMutationRate() {
		if (mutationRate == -1) {
			mutationRate = doubleFromFile(MUTATION_RATE);
		}
		return mutationRate;
	}

	public static int getPopulationSize() {
		if (populationSize == -1) {
			populationSize = intFromFile(POPULATION_SIZE);
		}
		return populationSize;
	}

	public static void setPopulationSize(final int size) {
		populationSize = size;
	}

	public static void setGenerations(final int gens) {
		generations = gens;
	}

	/**
	 * How many times each pair of players meets when fitness is worked out. Each meeting is two games, one with each
	 * of them as player one. More meetings mean less luck in the fitness, and a slower run. 1 if the settings file
	 * doesn't say.
	 */
	public static int getGamesPerPairing() {
		if (gamesPerPairing == -1) {
			gamesPerPairing = gamesPerPairingFrom(getProperties());
		}
		return gamesPerPairing;
	}

	/**
	 * Reads gamesPerPairing from the settings, 1 if they don't have it
	 *
	 * @throws IllegalArgumentException if it's less than 1, as with no games every fitness would be 0
	 */
	static int gamesPerPairingFrom(final Properties properties) {
		final String value = properties.getProperty(GAMES_PER_PAIRING);
		return value == null ? 1 : checkGamesPerPairing(Integer.parseInt(value.trim()));
	}

	public static void setGamesPerPairing(final int games) {
		gamesPerPairing = checkGamesPerPairing(games);
	}

	private static int checkGamesPerPairing(final int games) {
		if (games < 1) {
			throw new IllegalArgumentException(GAMES_PER_PAIRING + " has to be at least 1, so players play each other,"
					+ " but it's " + games);
		}
		return games;
	}

	public static int getTournamentSize() {
		if (tournamentSize == -1) {
			tournamentSize = intFromFile(TOURNAMENT_SIZE);
		}
		return tournamentSize;
	}

	public static double getUniformRate() {
		if (uniformRate == -1) {
			uniformRate = doubleFromFile(UNIFORM_RATE);
		}
		return uniformRate;
	}

	public static boolean isElitism() {
		if (elitism == null) {
			elitism = GameSettings.booleanFrom(ELITISM, getPropertyFromFile(ELITISM));
		}
		return elitism;
	}

	public static void setAttemptCount(final int attemptCount) {
		GenAlgSettings.attemptCount = attemptCount;
	}

	/**
	 * @throws IllegalStateException if the file can't be read
	 */
	private static Properties getProperties() {
		try (InputStream in = new FileInputStream(FILE_PATH)) {
			final Properties props = new Properties();
			props.load(in);
			return props;
		} catch (final IOException e) {
			throw new IllegalStateException("Couldn't read the settings in " + FILE_PATH, e);
		}
	}

	/**
	 * @return the setting's value, without any spaces around it
	 * @throws IllegalStateException if the file can't be read or doesn't have it
	 */
	public static String getPropertyFromFile(final String propVar) {
		final String result = getProperties().getProperty(propVar);
		if (result == null) {
			throw new IllegalStateException(propVar + " is missing from " + FILE_PATH);
		}
		return result.trim();
	}

	private static int intFromFile(final String name) {
		final String value = getPropertyFromFile(name);
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException(name + " has to be a whole number, but it's '" + value + "'", e);
		}
	}

	private static double doubleFromFile(final String name) {
		final String value = getPropertyFromFile(name);
		try {
			return Double.parseDouble(value);
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException(name + " has to be a number, but it's '" + value + "'", e);
		}
	}
}