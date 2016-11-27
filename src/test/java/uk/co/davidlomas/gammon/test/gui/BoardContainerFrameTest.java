package uk.co.davidlomas.gammon.test.gui;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import testingCode.Settings;

public class BoardContainerFrameTest {
	@BeforeClass
	public static void beforeClass() {
		Settings.resettSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resettSettings();
	}
}
