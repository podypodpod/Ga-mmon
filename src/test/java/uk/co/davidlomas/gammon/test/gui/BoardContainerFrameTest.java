package uk.co.davidlomas.gammon.test.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

public class BoardContainerFrameTest {
	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}
}
