package uk.co.davidlomas.gammon.test.helpers;

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public class SettingsUtil {
	public static void resetSettings() {
		// GAME
		GameSettings.setAreBothAIsCache(true);
		GameSettings.setDisplayGUICache(false);
		GameSettings.setIsP1BlackCache(true);
		GameSettings.setMultiThreadingCache(false);
		GameSettings.setThreadsCache(0);
		GameSettings.setTimeDelayCache(0);

		// GEN ALG
		GenAlgSettings.setElitism(true);
		GenAlgSettings.setGenerations(0);
		GenAlgSettings.setPopulationSize(2);
		GenAlgSettings.setAttemptCount(9999);
		GenAlgSettings.setTournamentSize(2);
		GenAlgSettings.setUniformRate(632.0);
		GenAlgSettings.setMutationRate(0.1);
		GenAlgSettings.setGamesPerPairing(1);
	}

	/**
	 * Changes a setting in the tests' copy of GenAlgSettings.properties, so a test can see it read from the file
	 */
	public static void writeGenAlgSetting(final String name, final String value) {
		final String file = System.getProperty(GameSettings.CONF_DIR_PROPERTY) + "/GenAlgSettings.properties";
		final Properties properties = new Properties();
		try (InputStream in = new FileInputStream(file)) {
			properties.load(in);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
		properties.setProperty(name, value);
		try (OutputStream out = new FileOutputStream(file)) {
			properties.store(out, null);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * The folder the tests save players in, set by TestConfListener or Gradle
	 */
	public static String getPlayersDir() {
		return System.getProperty(Individual.PLAYERS_DIR_PROPERTY);
	}
}
