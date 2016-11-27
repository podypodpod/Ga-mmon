package uk.co.davidlomas.gammon.test.helpers;

import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

public class Settings {
	public static void resettSettings() {
		// GAME
		GameSettings.setAreBothAIsCache(null);
		GameSettings.setDisplayConsoleCache(null);
		GameSettings.setDisplayGUICache(null);
		GameSettings.setIsP1BlackCache(null);
		GameSettings.setMultiThreadingCache(null);
		GameSettings.setTimeDelayCache(-1);

		// GEN ALG
		GenAlgSettings.setElitism(null);
		GenAlgSettings.setDisplayConsole(null);
		GenAlgSettings.setGenerations(-1);
		GenAlgSettings.setPopulationSize(-1);
		GenAlgSettings.setAttemptCount(-1);
		GenAlgSettings.setTournamentSize(-1);
		GenAlgSettings.setUniformRate(-1);
		GenAlgSettings.setMutationRate(-1);
	}
}
