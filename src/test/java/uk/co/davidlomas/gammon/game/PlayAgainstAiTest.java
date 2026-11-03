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
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayAgainstAiTest {

	private final InputStream console = System.in;
	private final PrintStream screen = System.out;

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
		System.setIn(console);
		System.setOut(screen);
		GameSettings.setAreBothAIsCache(true);
		RandomSource.reset();
	}

	@Test
	public void aPersonPlaysASavedPlayerOnTheConsole() {
		// a saved player to play against
		new File(SettingsUtil.getPlayersDir()).mkdirs();
		final String saved = SettingsUtil.getPlayersDir() + "/opponent - " + UUID.randomUUID();
		new Individual().saveToFile(saved);

		// the person concedes at their first turn
		System.setIn(new ByteArrayInputStream("2\n".getBytes(StandardCharsets.UTF_8)));
		final ByteArrayOutputStream printed = new ByteArrayOutputStream();
		System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
		RandomSource.setSeed(1);

		PlayAgainstAi.main(new String[]{saved});

		final String said = printed.toString(StandardCharsets.UTF_8);
		assertTrue(said.contains("You conceded."), said);
		assertTrue(said.contains("The AI won"), said);
	}
}
