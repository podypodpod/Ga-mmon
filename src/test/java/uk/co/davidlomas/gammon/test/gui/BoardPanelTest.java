package uk.co.davidlomas.gammon.test.gui;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import uk.co.davidlomas.gammon.test.helpers.Settings;

public class BoardPanelTest {
	@BeforeClass
	public static void beforeClass() {
		Settings.resetSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resetSettings();
	}
}
