package uk.co.davidlomas.gammon.test.gui;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

public class BoardPanelTest {
	@BeforeClass
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterClass
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}
}
