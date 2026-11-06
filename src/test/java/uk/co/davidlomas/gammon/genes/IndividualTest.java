package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IndividualTest {

	private Individual individual1;
	private Individual individual2;
	private String filePath;

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@BeforeEach
	public void setup() {
		individual1 = new Individual();
		individual2 = new Individual();
		new File(SettingsUtil.getPlayersDir()).mkdirs();
		filePath = SettingsUtil.getPlayersDir() + "/testIndividual - " + UUID.randomUUID().toString();
	}

	@Test
	public void getFilePathForPlayersComparedToActualStringShouldBeTrue() {
		final String actual = individual1.getFilePathForPlayers();
		final String expected = SettingsUtil.getPlayersDir() + "/Attempt - ";
		assertTrue(actual.startsWith(expected));
	}

	/**
	 * Saving a player to the players folder and then loading, checking results match
	 */
	@Test
	public void savingAndLoadingIndividualCheckingPropertiesMatch() {

		// given
		individual1 = new Individual();
		individual1.saveToFile(filePath);
		assertTrue(new File(filePath).exists());

		// when
		individual2 = new Individual();
		individual2.loadFromFile(filePath);

		//then
		// check num of attributes is same
		assertEquals(individual1.getNumOfAttributes(), individual2.getNumOfAttributes());

		// check all attributes
		for (int i = 0; i < individual1.getNumOfAttributes(); i++) {

			assertEquals(individual1.getAttribute(i).getName(), individual2.getAttribute(i).getName(), "Individuals attributes  don't match after loading");

			assertEquals(individual1.getAttribute(i).getValue(), individual2.getAttribute(i).getValue(), "Individuals attributes  don't match after loading");
		}
	}

	@Test
	public void aFileThatCantBeReadIsAnErrorRatherThanLeavingTheGenesAsTheyWere() {
		final UncheckedIOException failed =
				assertThrows(UncheckedIOException.class, () -> individual1.loadFromFile(filePath + " missing"));

		assertTrue(failed.getMessage().contains(filePath), failed.getMessage());
	}

	@Test
	public void aFileThatCantBeWrittenIsAnError() {
		final String nowhere = filePath + " no such folder/player";

		assertThrows(UncheckedIOException.class, () -> individual1.saveToFile(nowhere));
	}

	@Test
	public void theLastAttemptIsTheHighestAttemptFolder() {
		final int highest = Individual.lastAttempt() + 2;
		assertTrue(new File(SettingsUtil.getPlayersDir() + "/Attempt - " + highest).mkdirs());
		// neither of these is an attempt
		assertTrue(new File(SettingsUtil.getPlayersDir() + "/Attempt - " + (highest + 1) + " old").mkdirs());
		assertTrue(new File(SettingsUtil.getPlayersDir() + "/Attempts").mkdirs() || new File(SettingsUtil.getPlayersDir() + "/Attempts").isDirectory());

		assertEquals(highest, Individual.lastAttempt());
	}

	@Test
	public void savingLeavesOnlyTheFinishedFileAndReplacesAnOldOne() {
		individual1.saveToFile(filePath);
		individual2.saveToFile(filePath);

		final Individual loaded = new Individual();
		loaded.loadFromFile(filePath);

		assertEquals(String.valueOf(individual2.getChromosome()), String.valueOf(loaded.getChromosome()));
		assertFalse(new File(filePath + ".partial").exists());
	}

	@Test
	public void aPlayerSavedWithOtherGenesIsRefused() throws IOException {
		// as the old rule-based player saved them, with 32 genes of 7 bits rather than these
		final String oldPlayer = filePath + " old";
		final Properties saved = new Properties();
		saved.setProperty("chromosome", "0".repeat(32 * 7));
		try (OutputStream out = new FileOutputStream(oldPlayer)) {
			saved.store(out, null);
		}

		final IllegalArgumentException refused =
				assertThrows(IllegalArgumentException.class, () -> individual1.loadFromFile(oldPlayer));

		assertTrue(refused.getMessage().contains("wasn't saved with these genes"), refused.getMessage());
	}
}
