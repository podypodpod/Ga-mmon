package uk.co.davidlomas.gammon.test.genes;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.Population;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link Population}
 */
public class PopulationTest {

	private static final int POPULATION_SIZE = 2;

	private Population initPopulation;

	@BeforeClass
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterClass
	public static void afterClass() {
		SettingsUtil.resetSettings();
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

	/**
	 * Checking that calculate fitness does change order of a popualtion
	 */
	@Test
	public void calculateFitnessChangesOrder() {
		// GIVEN a population which hasn't had it's fitness calculated
		// AND its currently fittest player
		final double before = initPopulation.getFittest().getFitness();

		// WHEN we calculate the fitness
		initPopulation.calculateFitness();

		// THEN the fittest has changed
		assertThat(initPopulation.getFittest().getFitness()).as("fittest").isNotEqualTo(before);
	}

}
