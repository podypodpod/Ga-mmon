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
	
    public static String getPropertyFromFile(String propVar){
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
    
    public static double getUniformrate() {
		return Double.valueOf(getPropertyFromFile("uniformRate"));
	}
	public static double getMutationrate() {
		return Double.valueOf(getPropertyFromFile("mutationRate"));
	}
	public static int getTournamentsize() {
		return Integer.valueOf(getPropertyFromFile("tournamentSize"));
	}
	public static int getPopulationsize() {
		return Integer.valueOf(getPropertyFromFile("populationSize"));
	}
	public static String getSavePath() {
		return getPropertyFromFile("savePath");
	}
    public static double getUniformRate() {
		return Double.valueOf(getPropertyFromFile("uniformRate"));
	}
	public static double getMutationRate() {
		return Double.valueOf(getPropertyFromFile("mutationRate"));
	}
	public static int getTournamentSize() {
		return Integer.valueOf(getPropertyFromFile("tournamentSize"));
	}
	public static boolean isElitism() {
		return Boolean.valueOf(getPropertyFromFile("elitism"));
	}
	public static int getGenerations() {
		return Integer.valueOf(getPropertyFromFile("generations"));
	}
	public static int getPopulationSize() {
		return Integer.valueOf(getPropertyFromFile("populationSize"));
	}
	public static boolean isDisplayconsole() {
		return Boolean.valueOf(getPropertyFromFile("displayConsole"));
	}
}