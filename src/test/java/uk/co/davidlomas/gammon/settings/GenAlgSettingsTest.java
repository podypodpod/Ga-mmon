package uk.co.davidlomas.gammon.settings;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import java.util.Properties;

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
	public void leavingGenerationsAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		SettingsUtil.writeGenAlgSetting("generations", "123");
		final String actualValue = GenAlgSettings.getGenerations() + "";
		assertEquals("123", actualValue);
	}

	@Test
	public void leavingMutationsAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String original = GenAlgSettings.getPropertyFromFile("mutationRate");


		SettingsUtil.writeGenAlgSetting("mutationRate", "987");
		final double actualValue = GenAlgSettings.getMutationRate();
		assertTrue(doublesAreEqual(987, actualValue));


		SettingsUtil.writeGenAlgSetting("mutationRate", original);
	}

	@Test
	public void leavingPopulationAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String original = GenAlgSettings.getPropertyFromFile("populationSize");

		SettingsUtil.writeGenAlgSetting("populationSize", "544");
		final String actualValue = GenAlgSettings.getPopulationSize() + "";
		assertEquals("544", actualValue);

		SettingsUtil.writeGenAlgSetting("populationSize", original);
	}

	@Test
	public void leavingTournamentAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String original = GenAlgSettings.getPropertyFromFile("tournamentSize");

		SettingsUtil.writeGenAlgSetting("tournamentSize", "355");
		final String actualValue = GenAlgSettings.getTournamentSize() + "";
		assertEquals("355", actualValue);


		SettingsUtil.writeGenAlgSetting("tournamentSize", original);
	}

	@Test
	public void leavingUniformRateAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String original = GenAlgSettings.getPropertyFromFile("uniformRate");

		SettingsUtil.writeGenAlgSetting("uniformRate", "632");
		final double actualValue = GenAlgSettings.getUniformRate();
		assertTrue(doublesAreEqual(632, actualValue));


		SettingsUtil.writeGenAlgSetting("uniformRate", original);
	}

	@Test
	public void leavingElitismConsoleAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
		final String original = GenAlgSettings.getPropertyFromFile("elitism");

		SettingsUtil.writeGenAlgSetting("elitism", "false");
		final Boolean actualValue = GenAlgSettings.isElitism();
		assertFalse(actualValue);


		SettingsUtil.writeGenAlgSetting("elitism", original);
	}

	@Test
	public void tryingToGetAMissingPropertySaysWhichOne() {
		final IllegalStateException missing =
				assertThrows(IllegalStateException.class, () -> GenAlgSettings.getPropertyFromFile("missingProperty"));

		assertTrue(missing.getMessage().contains("missingProperty"), missing.getMessage());
	}

	@Test
	public void spacesAroundANumberInTheFileAreIgnored() {
		final String original = GenAlgSettings.getPropertyFromFile("populationSize");

		SettingsUtil.writeGenAlgSetting("populationSize", " 544 ");
		assertEquals(544, GenAlgSettings.getPopulationSize());

		SettingsUtil.writeGenAlgSetting("populationSize", original);
	}

	@Test
	public void aNumberThatIsntOneSaysWhichSettingItIs() {
		final String original = GenAlgSettings.getPropertyFromFile("tournamentSize");

		SettingsUtil.writeGenAlgSetting("tournamentSize", "lots");
		final IllegalArgumentException refused =
				assertThrows(IllegalArgumentException.class, GenAlgSettings::getTournamentSize);
		assertTrue(refused.getMessage().contains("tournamentSize"), refused.getMessage());

		SettingsUtil.writeGenAlgSetting("tournamentSize", original);
	}

	@Test
	public void elitismThatIsntTrueOrFalseIsRefused() {
		final String original = GenAlgSettings.getPropertyFromFile("elitism");

		SettingsUtil.writeGenAlgSetting("elitism", "yes");
		assertThrows(IllegalArgumentException.class, GenAlgSettings::isElitism);

		SettingsUtil.writeGenAlgSetting("elitism", original);
	}

	@Test
	public void theAttemptCountIsntInTheSettingsFile() {
		// a run mustn't change the file, which is in git
		assertThrows(IllegalStateException.class, () -> GenAlgSettings.getPropertyFromFile("attemptCount"));
	}

	private boolean doublesAreEqual(final double first, final double second) {
		return Math.abs(first - second) == 0;
	}

	private static void assertCheckRefuses(final String setting) {
		final IllegalArgumentException refused = assertThrows(IllegalArgumentException.class, GenAlgSettings::check);
		assertTrue(refused.getMessage().contains(setting), refused.getMessage());
		SettingsUtil.resetSettings();
	}

	@Test
	public void settingsThatMakeSensePassTheCheck() {
		SettingsUtil.resetSettings();
		GenAlgSettings.check();
	}

	@Test
	public void theCheckRefusesSettingsThatDontMakeSense() {
		SettingsUtil.resetSettings();
		GenAlgSettings.setPopulationSize(0);
		assertCheckRefuses("populationSize");

		GenAlgSettings.setTournamentSize(0);
		assertCheckRefuses("tournamentSize");

		// -1 means it has not been read yet
		GenAlgSettings.setGenerations(-2);
		assertCheckRefuses("generations");

		GenAlgSettings.setMutationRate(2);
		assertCheckRefuses("mutationRate");

		GenAlgSettings.setUniformRate(-0.5);
		assertCheckRefuses("uniformRate");
	}

	@Test
	public void gamesPerPairingIsOneWhenTheSettingsFileDoesntSay() {
		// as in a settings file from before it was added
		assertEquals(1, GenAlgSettings.gamesPerPairingFrom(new Properties()));
	}

	@Test
	public void gamesPerPairingIsReadFromTheSettingsFile() {
		final Properties properties = new Properties();
		properties.setProperty("gamesPerPairing", " 3 ");

		assertEquals(3, GenAlgSettings.gamesPerPairingFrom(properties));
	}

	@Test
	public void gamesPerPairingBelowOneIsRefused() {
		// with no games, every fitness would be 0 and the GA would choose at random
		final Properties properties = new Properties();
		properties.setProperty("gamesPerPairing", "0");

		assertThrows(IllegalArgumentException.class, () -> GenAlgSettings.gamesPerPairingFrom(properties));
		assertThrows(IllegalArgumentException.class, () -> GenAlgSettings.setGamesPerPairing(-1));
	}
}
