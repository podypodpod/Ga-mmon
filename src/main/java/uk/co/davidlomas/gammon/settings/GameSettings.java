/*
  GNU General Public License

  This file is part of GA-mmon.

  GA-mmon is free software: you can redistribute it and/or modify it under the
  terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
  A PARTICULAR PURPOSE. See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with
  GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * GameSettings class
 * <p>
 * Only created this to simplify changing the settings of the game
 * <p>
 * Puts all settings for the actual game in one place..e.g. timeDelay for if it
 * actually needs to be seen
 *
 * @author David Lomas
 */
public class GameSettings {

	/**
	 * The settings folder can be moved with -Dgammon.confDir, the tests use this to work on a copy
	 */
	public static final String CONF_DIR_PROPERTY = "gammon.confDir";
	private static final String FILE_PATH = System.getProperty(CONF_DIR_PROPERTY, "conf") + "/GameSettings.properties";

	private static Boolean isP1BlackCache = null;
	private static Boolean displayGUICache = null;
	private static Boolean areBothAIsCache = null;
	private static Boolean multiThreadingCache = null;

	private static int timeDelayCache = -1;
	private static int threadsCache = -1;

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

	public static void setThreadsCache(final int threadsCache) {
		GameSettings.threadsCache = checkThreads(threadsCache);
	}

	/**
	 * Reads every setting now, if it hasn't been read yet. The settings are read from the file the first time
	 * they're asked for; call this before games are played on several threads, so they don't each read the file
	 */
	public static void loadAll() {
		getAreBothAI();
		getDisplayGUI();
		getMultiThreading();
		getThreads();
		getTimeDelay();
		isP1Black();
	}

	/**
	 * How many threads to play the GA's fitness games on when multiThreading is on: 0, or missing from the file,
	 * for one per core
	 */
	public static int getThreads() {
		if (threadsCache == -1) {
			threadsCache = threadsFrom(getPropertyFromFile("threads"));
		}
		return threadsCache;
	}

	/**
	 * The threads setting from the file's value, 0 if it's missing
	 *
	 * @throws IllegalArgumentException if it's below 0
	 */
	static int threadsFrom(final String value) {
		return value == null ? 0 : checkThreads(Integer.parseInt(value.trim()));
	}

	private static int checkThreads(final int threads) {
		if (threads < 0) {
			throw new IllegalArgumentException("threads has to be 0, for one per core, or more, but it's " + threads);
		}
		return threads;
	}

	public static boolean getAreBothAI() {
		if (areBothAIsCache == null) {
			areBothAIsCache = booleanFrom("areBothAIs", getPropertyFromFile("areBothAIs"));
		}
		return areBothAIsCache;
	}

	public static boolean getDisplayGUI() {
		if (displayGUICache == null) {
			displayGUICache = booleanFrom("displayGUI", getPropertyFromFile("displayGUI"));
		}
		return displayGUICache;
	}

	public static boolean getMultiThreading() {
		if (multiThreadingCache == null) {
			multiThreadingCache = booleanFrom("multiThreading", getPropertyFromFile("multiThreading"));
		}
		return multiThreadingCache;
	}

	/**
	 * A true or false setting. Anything else is refused rather than read as false, as that would quietly change how the
	 * program runs: a mistyped areBothAIs would have it waiting for a person to play
	 *
	 * @param name  the setting's name, for the error
	 * @param value its value, null if it's missing
	 *
	 * @return the setting
	 * @throws IllegalArgumentException if it isn't true or false
	 */
	public static boolean booleanFrom(final String name, final String value) {
		if (value != null && value.trim().equalsIgnoreCase("true")) {
			return true;
		}
		if (value != null && value.trim().equalsIgnoreCase("false")) {
			return false;
		}
		throw new IllegalArgumentException(name + " has to be true or false, but it's "
				+ (value == null ? "missing" : "'" + value + "'"));
	}

	/**
	 * The time delay from the file's value
	 *
	 * @throws IllegalArgumentException if it's missing or not a whole number
	 */
	static int timeDelayFrom(final String value) {
		try {
			return Integer.parseInt(String.valueOf(value).trim());
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException("timeDelay has to be a whole number, but it's "
					+ (value == null ? "missing" : "'" + value + "'"), e);
		}
	}

	/**
	 * @return the setting's value, null if the file doesn't have it
	 * @throws IllegalStateException if the file can't be read
	 */
	public static String getPropertyFromFile(final String propVar) {
		final Properties properties = new Properties();
		try (InputStream input = new FileInputStream(FILE_PATH)) {
			properties.load(input);
			return properties.getProperty(propVar);
		} catch (final IOException e) {
			throw new IllegalStateException("Couldn't read the settings in " + FILE_PATH, e);
		}
	}

	// GETTERS
	public static int getTimeDelay() {
		if (timeDelayCache == -1) {
			timeDelayCache = timeDelayFrom(getPropertyFromFile("timeDelay"));
		}
		return timeDelayCache;
	}

	public static boolean isP1Black() {
		if (isP1BlackCache == null) {
			isP1BlackCache = booleanFrom("isP1Black", getPropertyFromFile("isP1Black"));
		}
		return isP1BlackCache;
	}
}