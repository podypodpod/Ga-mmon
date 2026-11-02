package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for {@link Population}
 */
public class PopulationTest {

	private static final int POPULATION_SIZE = 2;

	private Population initPopulation;

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
		initPopulation = new Population(POPULATION_SIZE, true);

		GenAlgSettings.setElitism(true);

		GenAlgSettings.setGenerations(0);
		GenAlgSettings.setPopulationSize(POPULATION_SIZE);
		GenAlgSettings.setAttemptCount(9999);
		GenAlgSettings.setTournamentSize(1);

		GenAlgSettings.setUniformRate(1);
		GenAlgSettings.setMutationRate(1);
	}

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
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
		// Seeded, because each player's fitness is its score from the one game where it moves first, and if both
		// of them lose that game every fitness stays 0. Unseeded, that happened often enough to fail CI.
		RandomSource.setSeed(1);
		initPopulation = new Population(POPULATION_SIZE, true);

		// GIVEN a population which hasn't had it's fitness calculated
		// AND its currently fittest player
		final double before = initPopulation.getFittest().getFitness();

		// WHEN we calculate the fitness
		initPopulation.calculateFitness();

		// THEN the fittest has changed
		assertThat(initPopulation.getFittest().getFitness()).as("fittest").isNotEqualTo(before);
	}

}
