/**
 * GNU General public static License
 *
 * This file is part of GA-mmon.
 *
 * GA-mmon is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General public static License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General public static License for more
 * details.
 *
 * You should have received a copy of the GNU General public static License
 * along with GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GenAlgSettings {

	private static final String ELITISM = "elitism";
	private static final String UNIFORM_RATE = "uniformRate";
	private static final String TOURNAMENT_SIZE = "tournamentSize";
	private static final String POPULATION_SIZE = "populationSize";
	private static final String MUTATION_RATE = "mutationRate";
	private static final String ATTEMPT_COUNT = "attemptCount";
	private static final String GENERATIONS = "generations";
	private static final String FILE_PATH = "conf/GenAlgSettings.properties";

	final static Logger logger = LogManager.getLogger(GenAlgSettings.class);

	private static Boolean elitism = null;

	private static int generations = -1;
	private static int populationSize = -1;
	private static int attemptCount = -1;
	private static int tournamentSize = -1;
	private static double uniformRate = -1;
	private static double mutationRate = -1;

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

	public static int getAttemptCount() {
		if (attemptCount == -1) {
			attemptCount = Integer.valueOf(getPropertyFromFile(ATTEMPT_COUNT));
		}
		return attemptCount;
	}

	public static int getGenerations() {
		if (generations == -1) {
			generations = Integer.valueOf(getPropertyFromFile(GENERATIONS));
		}
		return generations;
	}

	public static double getMutationRate() {
		if (mutationRate == -1) {
			mutationRate = Double.valueOf(getPropertyFromFile(MUTATION_RATE));
		}
		return mutationRate;
	}

	public static int getPopulationSize() {
		if (populationSize == -1) {
			populationSize = Integer.valueOf(getPropertyFromFile(POPULATION_SIZE));
		}
		return populationSize;
	}

	public static void setPopulationSize(final int size) {
		populationSize = size;
	}

	public static void setGenerations(final int gens) {
		generations = gens;
	}

	public static int getTournamentSize() {
		if (tournamentSize == -1) {
			tournamentSize = Integer.valueOf(getPropertyFromFile(TOURNAMENT_SIZE));
		}
		return tournamentSize;
	}

	public static double getUniformRate() {
		if (uniformRate == -1) {
			uniformRate = Double.valueOf(getPropertyFromFile(UNIFORM_RATE));
		}
		return uniformRate;
	}

	public static boolean isElitism() {
		if (elitism == null) {
			elitism = Boolean.valueOf(getPropertyFromFile(ELITISM));
		}
		return elitism;
	}

	public static void setAttemptCount(final int atemptCount) {
		GenAlgSettings.attemptCount = atemptCount;
		updateTofile(ATTEMPT_COUNT, attemptCount + "");
	}

	public static void updateTofile(final String propertyName, final String value) {
		try {
			final Properties properties = getProperties();
			if (properties.containsKey(propertyName)) {
				final FileOutputStream out = new FileOutputStream(FILE_PATH);
				properties.setProperty(propertyName, value);
				properties.store(out, null);
				out.close();
			} else {
				throw new NoSuchFieldError();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static Properties getProperties() {
		FileInputStream in;
		try {
			in = new FileInputStream(FILE_PATH);
			final Properties props = new Properties();
			props.load(in);
			in.close();
			return props;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getPropertyFromFile(final String propVar) {

		final Properties properties = getProperties();
		final String result = properties.getProperty(propVar);
		if (result != null) {
			return result;
		}
		throw new NoSuchFieldError();
	}
}