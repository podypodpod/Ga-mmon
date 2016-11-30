package uk.co.davidlomas.gammon.test.game;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import uk.co.davidlomas.gammon.test.helpers.Settings;

public class BoardEvaluatorTest {
	@BeforeClass
	public static void beforeClass() {
		Settings.resetSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resetSettings();
	}
}
