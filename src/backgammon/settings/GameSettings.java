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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
	
    public static String getPropertyFromFile(String propVar){
		Properties properties = new Properties();
		InputStream input = null;
	 
		try {
			input = new FileInputStream("resources/settings/GameSettings.properties");
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

	//GETTERS
	public static int getTimeDelay(){
		return Integer.valueOf(getPropertyFromFile("timeDelay"));
	}
	public static boolean getAreBothAI(){
		return Boolean.valueOf(getPropertyFromFile("areBothAIs"));
	}
	public static boolean isP1Black(){
		return Boolean.valueOf(getPropertyFromFile("isP1Black"));
	}
	public static boolean getDisplayGUI() {
		return Boolean.valueOf(getPropertyFromFile("displayGUI"));
	}
	public static boolean getDisplayConsole(){
		return Boolean.valueOf(getPropertyFromFile("displayConsole"));
	}
	public static boolean getMultiThreading() {
		return Boolean.valueOf(getPropertyFromFile("multiThreading"));
	}
}