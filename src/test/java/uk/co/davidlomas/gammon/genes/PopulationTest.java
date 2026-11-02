package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

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
	 * With two players, each one's fitness is its score from the game where it moves first. Replaying those two games
	 * with the same seed gives the scores, so this holds whichever seed is used and whoever wins.
	 */
	@Test
	public void calculatingFitnessGivesEachPlayerItsScoreFromTheGameItMovesFirstIn() {
		final Individual first = initPopulation.getIndividual(0);
		final Individual second = initPopulation.getIndividual(1);

		RandomSource.setSeed(4);
		initPopulation.calculateFitness();

		RandomSource.setSeed(4);
		final GameManager gameManager = new GameManager();
		assertEquals(gameManager.playIndividualsVsEachOther(first, second).getPlayerOneScore(), first.getFitness(), 1e-9);
		assertEquals(gameManager.playIndividualsVsEachOther(second, first).getPlayerOneScore(), second.getFitness(), 1e-9);
	}

}
