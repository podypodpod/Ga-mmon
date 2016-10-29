/**
 * 	GNU General public static License 
 * 
 *  This file is part of GA-mmon.
 *  
 *  GA-mmon is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General public static License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  GA-mmon is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General public static License for more details.
 *  
 *  You should have received a copy of the GNU General public static License
 *  along with GA-mmon.  If not, see <http://www.gnu.org/licenses/>.
*/

package backgammon.settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GenAlgSettings {

	private static Boolean displayConsoleCache = null;
	private static double uniformRateCache = -1;
	private static double mutationRateCache = -1;
	private static int tournamentSizeCache = -1;
	private static Boolean elitismCache = null;
	private static int generationsCache = -1;
	private static int populationSizeCache = -1;
	private static String savePathCache = null;

	public static String getPropertyFromFile(String propVar) {
		Properties properties = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("resources/settings/GenAlgSettings.properties");
			properties.load(input);
			return properties.getProperty(propVar);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static double getUniformRate() {
		if (uniformRateCache == -1) {
			uniformRateCache = Double.valueOf(getPropertyFromFile("uniformRate"));
		}
		return uniformRateCache;
	}

	public static double getMutationRate() {
		if (mutationRateCache == -1) {
			mutationRateCache = Double.valueOf(getPropertyFromFile("mutationRate"));
		}
		return mutationRateCache;
	}

	public static int getTournamentSize() {
		if (tournamentSizeCache == -1) {
			tournamentSizeCache = Integer.valueOf(getPropertyFromFile("tournamentSize"));
		}
		return tournamentSizeCache;
	}

	public static int getPopulationSize() {
		if (populationSizeCache == -1) {
			populationSizeCache = Integer.valueOf(getPropertyFromFile("populationSize"));
		}
		return populationSizeCache;
	}

	public static String getSavePath() {
		if (savePathCache == null) {
			savePathCache = getPropertyFromFile("savePath");
		}
		return savePathCache;
	}

	public static boolean isElitism() {
		if (elitismCache == null) {
			elitismCache = Boolean.valueOf(getPropertyFromFile("elitism"));
		}
		return elitismCache;
	}

	public static int getGenerations() {
		if (generationsCache == -1) {
			generationsCache = Integer.valueOf(getPropertyFromFile("generations"));
		}
		return generationsCache;
	}

	public static boolean isDisplayconsole() {
		if (displayConsoleCache == null) {
			displayConsoleCache = Boolean.valueOf(getPropertyFromFile("displayConsole"));
		}
		return displayConsoleCache;
	}
}