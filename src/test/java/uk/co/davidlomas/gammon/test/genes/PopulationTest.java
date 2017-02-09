package uk.co.davidlomas.gammon.test.genes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.Population;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.Settings;

public class PopulationTest {

  private static final int POPULATION_SIZE = 2;

  private Population initPopulation;

  @BeforeClass
  public static void beforeClass() {
    Settings.resetSettings();
  }

  @AfterClass
  public static void afterClass() {
    Settings.resetSettings();
  }

  @Before
  public void setup() {
    initPopulation = new Population(POPULATION_SIZE, true);

    GenAlgSettings.setElitism(true);

    GenAlgSettings.setGenerations(0);
    GenAlgSettings.setPopulationSize(POPULATION_SIZE);
    GenAlgSettings.setAttemptCount(9999);
    GenAlgSettings.setTournamentSize(1);

    GenAlgSettings.setUniformRate(1);
    GenAlgSettings.setMutationRate(1);
  }

  @Test
  public void checkPopulationSizeIsRight() {
    assertEquals(POPULATION_SIZE, initPopulation.getPopulation().length);
  }

  @Test
  public void checkGetFittestGetsIndividualWithHighestFitness() {

    final Individual[] individuals = initPopulation.getPopulation();
    final Individual fitIndividual = new Individual();
    fitIndividual.setFitness(99);
    individuals[1] = fitIndividual;
    initPopulation.setIndividuals(individuals);

    final Individual actual = initPopulation.getFittest();

    assertEquals(fitIndividual, actual);
  }

  @Test
  public void calculateFitnessChangesOrder() {
    final double before = initPopulation.getFittest().getFitness();
    initPopulation.calculateFitness();
    final double after = initPopulation.getFittest().getFitness();
    assertNotEquals(before, after);
  }

}
