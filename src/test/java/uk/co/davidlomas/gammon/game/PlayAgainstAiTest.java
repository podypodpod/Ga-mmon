package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayAgainstAiTest {

	private final ByteArrayOutputStream printed = new ByteArrayOutputStream();

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@AfterEach
	public void tearDown() {
		GameSettings.setAreBothAIsCache(true);
		RandomSource.reset();
	}

	@Test
	public void aPersonPlaysASavedPlayerOnTheConsole() {
		final String saved = playersFile("opponent");
		new Individual().saveToFile(saved);
		RandomSource.setSeed(1);

		// the person concedes at their first turn
		play(saved, "2");

		assertTrue(said().contains("You conceded."), said());
		assertTrue(said().contains("The AI won"), said());
	}

	@Test
	public void aMistypedFileIsSaidPlainly() {
		play(playersFile("nobody"));

		assertTrue(said().startsWith("There's no saved player at "), said());
		assertFalse(said().contains("Your turn"), said());
	}

	@Test
	public void aPlayerSavedWithOtherGenesIsSaidPlainly() throws IOException {
		final String old = playersFile("old");
		final Properties properties = new Properties();
		properties.setProperty("chromosome", "0".repeat(32 * 7));
		try (OutputStream out = new FileOutputStream(old)) {
			properties.store(out, null);
		}

		play(old);

		assertTrue(said().startsWith("That player can't be played: "), said());
	}

	private void play(final String file, final String... lines) {
		final ByteArrayInputStream in =
				new ByteArrayInputStream((String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8));
		PlayAgainstAi.play(new String[]{file}, in, new PrintStream(printed, true, StandardCharsets.UTF_8));
	}

	private String said() {
		return printed.toString(StandardCharsets.UTF_8);
	}

	private static String playersFile(final String name) {
		new File(SettingsUtil.getPlayersDir()).mkdirs();
		return SettingsUtil.getPlayersDir() + "/" + name + " - " + UUID.randomUUID();
	}
}
