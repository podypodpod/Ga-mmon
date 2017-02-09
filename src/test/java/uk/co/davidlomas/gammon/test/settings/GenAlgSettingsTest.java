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

  @Before
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
    GenAlgSettings.updateToFile("attemptCount", "151");
    final String actualValue = GenAlgSettings.getPropertyFromFile("attemptCount");
    assertEquals("151", actualValue);
  }

  @Test
  public void leavingAttemptAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
    GenAlgSettings.updateToFile("attemptCount", "999");
    final String actualValue = GenAlgSettings.getAttemptCount() + "";
    assertEquals("999", actualValue);
  }

  @Test
  public void leavingGenerationsAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
    GenAlgSettings.updateToFile("generations", "123");
    final String actualValue = GenAlgSettings.getGenerations() + "";
    assertEquals("123", actualValue);
  }

  @Test
  public void leavingMutationsAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
    GenAlgSettings.updateToFile("mutationRate", "987");
    final double actualValue = GenAlgSettings.getMutationRate();
    assertTrue(doublesAreEqual(987, actualValue));
  }

  @Test
  public void leavingPopulationAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
    GenAlgSettings.updateToFile("populationSize", "544");
    final String actualValue = GenAlgSettings.getPopulationSize() + "";
    assertEquals("544", actualValue);
  }

  @Test
  public void leavingTournamentAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
    GenAlgSettings.updateToFile("tournamentSize", "355");
    final String actualValue = GenAlgSettings.getTournamentSize() + "";
    assertEquals("355", actualValue);
  }

  @Test
  public void leavingUniformRateAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
    GenAlgSettings.updateToFile("uniformRate", "632");
    final double actualValue = GenAlgSettings.getUniformRate();
    assertTrue(doublesAreEqual(632, actualValue));
  }

  @Test
  public void leavingElitismConsoleAsDefaultValueAndGettingItShouldReturnValueFromPropertiesFile() {
    GenAlgSettings.updateToFile("elitism", "false");
    final Boolean actualValue = GenAlgSettings.isElitism();
    assertFalse(actualValue);
  }

  @Test(expected = NoSuchFieldError.class)
  public void tryingToGetAMissingPropertyShouldThrowError() {
    GenAlgSettings.getPropertyFromFile("missingProperty");
  }

  @Test(expected = NoSuchFieldError.class)
  public void tryingToUpdateAMissingPropertyShouldThrowError() {
    GenAlgSettings.updateToFile("missingProperty", "15");
  }

  private boolean doublesAreEqual(final double first, final double second) {
    return Math.abs(first - second) == 0;
  }
}
