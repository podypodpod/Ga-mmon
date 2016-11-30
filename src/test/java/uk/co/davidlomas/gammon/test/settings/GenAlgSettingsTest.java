package uk.co.davidlomas.gammon.test.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.Settings;

public class GenAlgSettingsTest {

	private final Boolean newDisplayConsole = true;
	private final Boolean newElitism = false;

	private final double newUniformRate = 2;
	private final double newMutationRate = 3;
	private final int newTournamentSize = 4;
	private final int newGenerations = 5;
	private final int newPopulationSize = 6;
	private final int newAttemptCount = 7;

	@Before
	public void setup() {
		// reset all values to default as its static and the tests can therefore
		// Influence each other

		GenAlgSettings.setElitism(null);
		GenAlgSettings.setDisplayConsole(null);

		GenAlgSettings.setGenerations(-1);
		GenAlgSettings.setPopulationSize(-1);
		GenAlgSettings.setAttemptCount(-1);
		GenAlgSettings.setTournamentSize(-1);

		GenAlgSettings.setUniformRate(-1);
		GenAlgSettings.setMutationRate(-1);
	}

	@BeforeClass
	public static void beforeClass() {
		Settings.resetSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resetSettings();
	}

	@Test
	public void settingUniformRateThenGettingReturnsExpected() {
		GenAlgSettings.setUniformRate(newUniformRate);
		assertTrue(doublesAreEqual(newUniformRate, GenAlgSettings.getUniformRate()));
	}

	@Test
	public void settingMutationRateThenGettingReturnsExpected() {
		GenAlgSettings.setMutationRate(newMutationRate);
		assertTrue(doublesAreEqual(newMutationRate, GenAlgSettings.getMutationRate()));
	}

	@Test
	public void settingTournamentSizeThenGettingReturnsExpected() {
		GenAlgSettings.setTournamentSize(newTournamentSize);
		assertEquals(newTournamentSize, GenAlgSettings.getTournamentSize());
	}

	@Test
	public void settingGenerationsThenGettingReturnsExpected() {
		GenAlgSettings.setGenerations(newGenerations);
		assertEquals(newGenerations, GenAlgSettings.getGenerations());
	}

	@Test
	public void settingPopulationSizeThenGettingReturnsExpected() {
		GenAlgSettings.setPopulationSize(newPopulationSize);
		assertEquals(newPopulationSize, GenAlgSettings.getPopulationSize());
	}

	@Test
	public void settingAttemptCountThenGettingReturnsExpected() {
		GenAlgSettings.setAttemptCount(newAttemptCount);
		assertEquals(newAttemptCount, GenAlgSettings.getAttemptCount());
	}

	@Test
	public void settingElitismThenGettingReturnsExpected() {
		GenAlgSettings.setElitism(newElitism);
		assertEquals(newElitism, GenAlgSettings.isElitism());
	}

	@Test
	public void settingDisplayConsoleThenGettingReturnsExpected() {
		GenAlgSettings.setDisplayConsole(newDisplayConsole);
		assertEquals(newDisplayConsole, GenAlgSettings.isDisplayConsole());
	}

	@Test
	public void updatingPropFileAttempotCountThenGettingItShouldReturnExpectedValue() {
		final String expectedValue = "151";

		GenAlgSettings.updateTofile("attemptCount", expectedValue);
		final String actualValue = GenAlgSettings.getPropertyFromFile("attemptCount");

		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void leavingAtemptAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String expectedValue = "999";

		GenAlgSettings.updateTofile("attemptCount", expectedValue);
		final String actualValue = GenAlgSettings.getAttemptCount() + "";

		assertEquals(expectedValue, actualValue);

	}

	@Test
	public void leavingGenerationsAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String expectedValue = "123";

		GenAlgSettings.updateTofile("generations", expectedValue);
		final String actualValue = GenAlgSettings.getGenerations() + "";

		assertEquals(expectedValue, actualValue);

	}

	@Test
	public void leavingMutationsAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final double expectedValue = 987;

		GenAlgSettings.updateTofile("mutationRate", expectedValue + "");
		final double actualValue = GenAlgSettings.getMutationRate();

		assertTrue(doublesAreEqual(expectedValue, actualValue));
	}

	@Test
	public void leavingPopulationAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String expectedValue = "544";

		GenAlgSettings.updateTofile("populationSize", expectedValue);
		final String actualValue = GenAlgSettings.getPopulationSize() + "";

		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void leavingTournamentnAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String expectedValue = "355";

		GenAlgSettings.updateTofile("tournamentSize", expectedValue);
		final String actualValue = GenAlgSettings.getTournamentSize() + "";

		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void leavingUniformRateAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final double expectedValue = 632;

		GenAlgSettings.updateTofile("uniformRate", expectedValue + "");
		final double actualValue = GenAlgSettings.getUniformRate();

		assertTrue(doublesAreEqual(expectedValue, actualValue));
	}

	@Test
	public void leavingDisplayConsoleAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final boolean expectedValue = true;

		GenAlgSettings.updateTofile("displayConsole", expectedValue + "");
		final Boolean actualValue = GenAlgSettings.isDisplayConsole();

		assertTrue(actualValue);
	}

	@Test
	public void leavingElitsmConsoleAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final boolean expectedValue = false;

		GenAlgSettings.updateTofile("elitism", expectedValue + "");
		final Boolean actualValue = GenAlgSettings.isElitism();

		assertFalse(actualValue);
	}

	@Test(expected = NoSuchFieldError.class)
	public void tryingToGetAMissingPropertyShouldThrowError() {
		GenAlgSettings.getPropertyFromFile("missingProperty");
	}

	@Test(expected = NoSuchFieldError.class)
	public void tryingToUpdateAMissingPropertyShouldThrowError() {
		GenAlgSettings.updateTofile("missingProperty", "15");
	}

	private boolean doublesAreEqual(final double first, final double second) {
		return Math.abs(first - second) == 0;
	}
}
