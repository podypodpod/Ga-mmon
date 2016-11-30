/**
 * GNU General Public License
 *
 * This file is part of GA-mmon.
 *
 * GA-mmon is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * GameSettings class
 *
 * Only created this to simplify changing the settings of the game
 *
 * Puts all settings for the actual game in one place..e.g. timeDelay for if it
 * actually needs to be seen
 *
 * @author David Lomas
 */
public class GameSettings {

	private static Boolean isP1BlackCache = null;
	private static Boolean displayGUICache = null;
	private static Boolean areBothAIsCache = null;
	private static Boolean multiThreadingCache = null;

	private static int timeDelayCache = -1;

	public static void setIsP1BlackCache(final Boolean isP1BlackCache) {
		GameSettings.isP1BlackCache = isP1BlackCache;
	}

	public static void setDisplayGUICache(final Boolean displayGUICache) {
		GameSettings.displayGUICache = displayGUICache;
	}

	public static void setAreBothAIsCache(final Boolean areBothAIsCache) {
		GameSettings.areBothAIsCache = areBothAIsCache;
	}

	public static void setMultiThreadingCache(final Boolean multiThreadingCache) {
		GameSettings.multiThreadingCache = multiThreadingCache;
	}

	public static void setTimeDelayCache(final int timeDelayCache) {
		GameSettings.timeDelayCache = timeDelayCache;
	}

	public static boolean getAreBothAI() {
		if (areBothAIsCache == null) {
			areBothAIsCache = Boolean.valueOf(getPropertyFromFile("areBothAIs"));
		}
		return areBothAIsCache;
	}

	public static boolean getDisplayGUI() {
		if (displayGUICache == null) {
			displayGUICache = Boolean.valueOf(getPropertyFromFile("displayGUI"));
		}
		return displayGUICache;
	}

	public static boolean getMultiThreading() {
		if (multiThreadingCache == null) {
			multiThreadingCache = Boolean.valueOf(getPropertyFromFile("multiThreading"));
		}
		return multiThreadingCache;
	}

	public static String getPropertyFromFile(final String propVar) {
		final Properties properties = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("conf/GameSettings.properties");
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

	// GETTERS
	public static int getTimeDelay() {
		if (timeDelayCache == -1) {
			timeDelayCache = Integer.valueOf(getPropertyFromFile("timeDelay"));
		}
		return timeDelayCache;
	}

	public static boolean isP1Black() {
		if (isP1BlackCache == null) {
			isP1BlackCache = Boolean.valueOf(getPropertyFromFile("isP1Black"));
		}
		return isP1BlackCache;
	}
}