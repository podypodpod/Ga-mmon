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
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GenAlgSettings {

	private static final String FILE_PATH = "conf/GenAlgSettings.properties";

	final static Logger logger = LogManager.getLogger(GenAlgSettings.class);

	private static Boolean elitism = null;
	private static Boolean displayConsole = null;

	private static int generations = -1;
	private static int populationSize = -1;
	private static int attemptCount = -1;
	private static int tournamentSize = -1;

	private static double uniformRate = -1;
	private static double mutationRate = -1;

	public static void setDisplayConsole(final Boolean displayConsole) {
		GenAlgSettings.displayConsole = displayConsole;
	}

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
			attemptCount = Integer.valueOf(getPropertyFromFile("attemptCount"));
		}
		return attemptCount;
	}

	public static int getGenerations() {
		if (generations == -1) {
			generations = Integer.valueOf(getPropertyFromFile("generations"));
		}
		return generations;
	}

	public static double getMutationRate() {
		if (mutationRate == -1) {
			mutationRate = Double.valueOf(getPropertyFromFile("mutationRate"));
		}
		return mutationRate;
	}

	public static int getPopulationSize() {
		if (populationSize == -1) {
			populationSize = Integer.valueOf(getPropertyFromFile("populationSize"));
		}
		return populationSize;
	}

	public static void setPopulationSize(final int size) {
		populationSize = size;
	}

	public static void setGenerations(final int gens) {
		generations = gens;
	}

	public static String getPropertyFromFile(final String propVar) {
		final Properties properties = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(FILE_PATH);
			properties.load(input);
			return properties.getProperty(propVar);

		} catch (final IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static int getTournamentSize() {
		if (tournamentSize == -1) {
			tournamentSize = Integer.valueOf(getPropertyFromFile("tournamentSize"));
		}
		return tournamentSize;
	}

	public static double getUniformRate() {
		if (uniformRate == -1) {
			uniformRate = Double.valueOf(getPropertyFromFile("uniformRate"));
		}
		return uniformRate;
	}

	public static boolean isDisplayConsole() {
		if (displayConsole == null) {
			displayConsole = Boolean.valueOf(getPropertyFromFile("displayConsole"));
		}
		return displayConsole;
	}

	public static boolean isElitism() {
		if (elitism == null) {
			elitism = Boolean.valueOf(getPropertyFromFile("elitism"));
		}
		return elitism;
	}

	public static void setAttemptCount(final int atemptCount) {
		GenAlgSettings.attemptCount = atemptCount;
		updateTofile("attemptCount", attemptCount + "");
	}

	public static void updateTofile(final String propertyName, final String value) {

		try {
			final FileInputStream in = new FileInputStream(FILE_PATH);
			final Properties props = new Properties();
			props.load(in);
			in.close();

			final FileOutputStream out = new FileOutputStream(FILE_PATH);
			props.setProperty(propertyName, value);
			props.store(out, null);
			out.close();

		} catch (final IOException e) {
			e.printStackTrace();
		}

	}
}