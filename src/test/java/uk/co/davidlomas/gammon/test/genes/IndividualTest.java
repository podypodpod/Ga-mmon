package uk.co.davidlomas.gammon.test.genes;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
	public static void afterClass() throws IOException {
		SettingsUtil.resetSettings();
		final File directory = new File("savedPlayers/Attempt - 9999");
		FileUtils.deleteDirectory(directory);
	}

	@BeforeEach
	public void setup() {
		individual1 = new Individual();
		individual2 = new Individual();
		filePath = "savedPlayers/testIndividual - " + UUID.randomUUID().toString();
	}

	@Test
	public void getFilePathForPlayersComparedToActualStringShouldBeTrue() {
		final String actual = individual1.getFilePathForPlayers();
		final String expected = "savedPlayers/Attempt - ";
		assertTrue(actual.startsWith(expected));
	}

	/**
	 * Saving a player to savedPlayers/testIndividual and then loading, checking results match
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

}