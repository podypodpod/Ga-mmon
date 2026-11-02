package uk.co.davidlomas.gammon.test.helpers;

import org.apache.commons.io.FileUtils;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * The tests change settings and write them to file, so they must never run against the real conf/ folder.
 * <p>
 * Gradle points them at a copy with -Dgammon.confDir. When something else runs the tests, such as IntelliJ's own
 * test runner, this makes the same copy in build/test-conf and points the settings at it. It runs once before any
 * test class loads, which matters because the settings classes read gammon.confDir when they are first used.
 * <p>
 * Registered in META-INF/services/org.junit.platform.launcher.LauncherSessionListener.
 */
public class TestConfListener implements LauncherSessionListener {

	private static final String CONF_DIR_PROPERTY = "gammon.confDir";

	@Override
	public void launcherSessionOpened(final LauncherSession session) {
		if (System.getProperty(CONF_DIR_PROPERTY) != null) {
			return;
		}
		final File testConf = new File("build/test-conf");
		try {
			FileUtils.deleteDirectory(testConf);
			FileUtils.copyDirectory(new File("conf"), testConf);
		} catch (final IOException e) {
			throw new UncheckedIOException("Couldn't make a copy of conf/ for the tests", e);
		}
		System.setProperty(CONF_DIR_PROPERTY, testConf.getAbsolutePath());
	}
}
