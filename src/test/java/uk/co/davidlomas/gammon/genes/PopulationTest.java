package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		RandomSource.setSeed(1);
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
	 * Calculating a population's fitness is FitnessCalculator's job, tested in FitnessCalculatorTest, so this only
	 * checks the population hands it over: the same players and seed give the same fitness either way, whoever wins
	 */
	@Test
	public void calculatingFitnessGivesTheSameResultAsTheFitnessCalculator() {
		RandomSource.setSeed(1);
		final Population samePlayers = new Population(POPULATION_SIZE, true);

		RandomSource.setSeed(4);
		initPopulation.calculateFitness();
		RandomSource.setSeed(4);
		FitnessCalculator.calculateFitnessOfPopulation(samePlayers);

		double total = 0;
		for (int i = 0; i < POPULATION_SIZE; i++) {
			assertArrayEquals(samePlayers.getIndividual(i).getChromosome(), initPopulation.getIndividual(i).getChromosome());
			assertEquals(samePlayers.getIndividual(i).getFitness(), initPopulation.getIndividual(i).getFitness(), 1e-9);
			total += samePlayers.getIndividual(i).getFitness();
		}
		// if every score were 0, a calculateFitness that did nothing would pass too
		assertTrue(total > 0, "seed 4 gave every player a score of 0, pick another");
	}

}
