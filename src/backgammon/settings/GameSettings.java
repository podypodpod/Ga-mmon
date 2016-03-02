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

/**
 * GameSettings class
 * 
 * Only created this to simplify changing the settings of the game
 * 
 * Puts all settings for the actual game in one place..e.g. timeDelay for if it actually needs to be seen
 * 
 * @author David Lomas - 11035527
 */
public class GameSettings {
	
	private static boolean isP1Black = true;
	private static boolean areBothAIs = true;
	private static int timeDelay = 0;
	private static boolean displayGUI = true;
	private static boolean displayConsole = false;
	
	/** currently not working */
	private static boolean multiThreading = false;
	
	
	public static int getTimeDelay(){
		return timeDelay;
	}
	
	public static boolean getAreBothAI(){
		return areBothAIs;
	}
	
	public static boolean isP1Black(){
		return isP1Black;
	}


	public static boolean getDisplayGUI() {
		return displayGUI;
	}
	
	public static boolean getDisplayConsole(){
		return displayConsole;
	}

	public static boolean getMultiThreading() {
		return multiThreading;
	}
	
}