package uk.co.davidlomas.gammon.test.settings;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenAlgSettingsTest {

	@BeforeEach
	public void setup() {
		// reset all values to default as its static and the tests can therefore
		// Influence each other

		GenAlgSettings.setElitism(null);
		GenAlgSettings.setGenerations(-1);
		GenAlgSettings.setPopulationSize(-1);
		GenAlgSettings.setAttemptCount(-1);
		GenAlgSettings.setTournamentSize(-1);

		GenAlgSettings.setUniformRate(-1);
		GenAlgSettings.setMutationRate(-1);
	}

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void settingUniformRateThenGettingReturnsExpected() {
		GenAlgSettings.setUniformRate(2);
		assertTrue(doublesAreEqual(2, GenAlgSettings.getUniformRate()));
	}

	@Test
	public void settingMutationRateThenGettingReturnsExpected() {
		GenAlgSettings.setMutationRate(3);
		assertTrue(doublesAreEqual(3, GenAlgSettings.getMutationRate()));
	}

	@Test
	public void settingTournamentSizeThenGettingReturnsExpected() {
		GenAlgSettings.setTournamentSize(4);
		assertEquals(4, GenAlgSettings.getTournamentSize());
	}

	@Test
	public void settingGenerationsThenGettingReturnsExpected() {
		GenAlgSettings.setGenerations(5);
		assertEquals(5, GenAlgSettings.getGenerations());
	}

	@Test
	public void settingPopulationSizeThenGettingReturnsExpected() {
		GenAlgSettings.setPopulationSize(6);
		assertEquals(6, GenAlgSettings.getPopulationSize());
	}

	@Test
	public void settingAttemptCountThenGettingReturnsExpected() {
		GenAlgSettings.setAttemptCount(7);
		assertEquals(7, GenAlgSettings.getAttemptCount());
	}

	@Test
	public void settingElitismThenGettingReturnsExpected() {
		GenAlgSettings.setElitism(false);
		assertEquals(false, GenAlgSettings.isElitism());
	}

	@Test
	public void updatingPropFileAttemptCountThenGettingItShouldReturnExpectedValue() {
		final double original = GenAlgSettings.getAttemptCount();

		GenAlgSettings.updateToFile("attemptCount", "151");
		final String actualValue = GenAlgSettings.getPropertyFromFile("attemptCount");
		assertEquals("151", actualValue);

		GenAlgSettings.updateToFile("attemptCount", original + "");
	}

	@Test
	public void leavingAttemptAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final double original = GenAlgSettings.getAttemptCount();

		GenAlgSettings.updateToFile("attemptCount", "999");
		final String actualValue = GenAlgSettings.getAttemptCount() + "";
		assertEquals("999", actualValue);

		GenAlgSettings.updateToFile("attemptCount", original + "");
	}

	@Test
	public void leavingGenerationsAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final double original = GenAlgSettings.getAttemptCount();

		GenAlgSettings.updateToFile("attemptCount", original + "");

		GenAlgSettings.updateToFile("generations", "123");
		final String actualValue = GenAlgSettings.getGenerations() + "";
		assertEquals("123", actualValue);
	}

	@Test
	public void leavingMutationsAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String original = GenAlgSettings.getPropertyFromFile("mutationRate");


		GenAlgSettings.updateToFile("mutationRate", "987");
		final double actualValue = GenAlgSettings.getMutationRate();
		assertTrue(doublesAreEqual(987, actualValue));


		GenAlgSettings.updateToFile("mutationRate", original);
	}

	@Test
	public void leavingPopulationAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String original = GenAlgSettings.getPropertyFromFile("populationSize");

		GenAlgSettings.updateToFile("populationSize", "544");
		final String actualValue = GenAlgSettings.getPopulationSize() + "";
		assertEquals("544", actualValue);

		GenAlgSettings.updateToFile("populationSize", original);
	}

	@Test
	public void leavingTournamentAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String original = GenAlgSettings.getPropertyFromFile("tournamentSize");

		GenAlgSettings.updateToFile("tournamentSize", "355");
		final String actualValue = GenAlgSettings.getTournamentSize() + "";
		assertEquals("355", actualValue);


		GenAlgSettings.updateToFile("tournamentSize", original);
	}

	@Test
	public void leavingUniformRateAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String original = GenAlgSettings.getPropertyFromFile("uniformRate");

		GenAlgSettings.updateToFile("uniformRate", "632");
		final double actualValue = GenAlgSettings.getUniformRate();
		assertTrue(doublesAreEqual(632, actualValue));


		GenAlgSettings.updateToFile("uniformRate", original);
	}

	@Test
	public void leavingElitismConsoleAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String original = GenAlgSettings.getPropertyFromFile("elitism");

		GenAlgSettings.updateToFile("elitism", "false");
		final Boolean actualValue = GenAlgSettings.isElitism();
		assertFalse(actualValue);


		GenAlgSettings.updateToFile("elitism", original);
	}

	@Test
	public void tryingToGetAMissingPropertyShouldThrowError() {
		assertThrows(NoSuchFieldError.class, () -> GenAlgSettings.getPropertyFromFile("missingProperty"));
	}

	@Test
	public void tryingToUpdateAMissingPropertyShouldThrowError() {
		assertThrows(NoSuchFieldError.class, () -> GenAlgSettings.updateToFile("missingProperty", "15"));
	}

	private boolean doublesAreEqual(final double first, final double second) {
		return Math.abs(first - second) == 0;
	}
}
