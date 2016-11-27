package uk.co.davidlomas.gammon.test.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import testingCode.Settings;
import uk.co.davidlomas.gammon.main.MainNoMenu;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

public class MainNoMenuTest {

	@BeforeClass
	public static void setup() {
		Settings.resettSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resettSettings();
	}

	@Test
	public void runMain() {

		// GAME
		GameSettings.setAreBothAIsCache(true);
		GameSettings.setDisplayConsoleCache(false);
		GameSettings.setDisplayGUICache(false);
		GameSettings.setIsP1BlackCache(true);
		GameSettings.setMultiThreadingCache(false);
		GameSettings.setTimeDelayCache(0);

		// GEN ALG
		GenAlgSettings.setElitism(true);
		GenAlgSettings.setDisplayConsole(false);
		GenAlgSettings.setGenerations(1);
		GenAlgSettings.setPopulationSize(2);
		GenAlgSettings.setAttemptCount(9998);
		GenAlgSettings.setTournamentSize(1);
		GenAlgSettings.setUniformRate(1);
		GenAlgSettings.setMutationRate(1);

		MainNoMenu.main(null);
	}

}
