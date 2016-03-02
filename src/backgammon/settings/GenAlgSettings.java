/**
 * 	GNU General Public License
 * 
 *  This file is part of GA-mmon.
 *  
 *  GA-mmon is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  GA-mmon is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with GA-mmon.  If not, see <http://www.gnu.org/licenses/>.
*/


package backgammon.settings;

public class GenAlgSettings {
	
	private static final boolean displayConsole = true;

    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.3;
    
    /** tournamentSize. Has to be a multiple of 4 for the tournament method to work correctly */
    private static final int tournamentSize = 8;
    
    private static final boolean elitism = true;
    private static final int generations = 50;
    private static final int populationSize = 10;
    private static final String savePath = "savedPlayers/Attempt6/";
    
    public static double getUniformrate() {
		return uniformRate;
	}

	public static double getMutationrate() {
		return mutationRate;
	}

	public static int getTournamentsize() {
		return tournamentSize;
	}

	public static int getPopulationsize() {
		return populationSize;
	}

	public static String getSavePath() {
		return savePath;
	}

    public static double getUniformRate() {
		return uniformRate;
	}

	public static double getMutationRate() {
		return mutationRate;
	}

	public static int getTournamentSize() {
		return tournamentSize;
	}

	public static boolean isElitism() {
		return elitism;
	}

	public static int getGenerations() {
		return generations;
	}

	public static int getPopulationSize() {
		return populationSize;
	}

	public static boolean isDisplayconsole() {
		return displayConsole;
	}
}