package uk.co.davidlomas.gammon.test.helpers;

import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

public class SettingsUtil {
    public static void resetSettings() {
        // GAME
        GameSettings.setAreBothAIsCache(true);
        GameSettings.setDisplayGUICache(false);
        GameSettings.setIsP1BlackCache(true);
        GameSettings.setMultiThreadingCache(false);
        GameSettings.setTimeDelayCache(0);

        // GEN ALG
        GenAlgSettings.setElitism(true);
        GenAlgSettings.setGenerations(0);
        GenAlgSettings.setPopulationSize(2);
        GenAlgSettings.setAttemptCount(9999);
        GenAlgSettings.setTournamentSize(2);
        GenAlgSettings.setUniformRate(632.0);
        GenAlgSettings.setMutationRate(0.1);
    }
}
