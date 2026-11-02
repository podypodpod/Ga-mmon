package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link GeneticAlgorithm}
 */
public class GeneticAlgorithmTest {

	private Individual individual1;
	private Individual individual2;

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
		individual1 = new Individual();
		individual2 = new Individual();
	}

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
	}

	@Test
	public void forcingAlwaysMutationChangesChromosome() {
		// force mutation to always happen
		GenAlgSettings.setMutationRate(1.00);

		final char before = individual1.getChromosome()[0];
		GeneticAlgorithm.mutate(individual1);
		final char after = individual1.getChromosome()[0];

		assertNotEquals(before, after);
	}

	@Test
	public void forcingNeverMutateDoesntChangeChromosome() {
		// force mutation to always happen
		GenAlgSettings.setMutationRate(0);

		final char before = individual1.getChromosome()[0];
		GeneticAlgorithm.mutate(individual1);
		final char after = individual1.getChromosome()[0];

		assertEquals(before, after);
	}

	@Test
	public void tournamentSelectionWithTheSameSeedPicksTheSameWinner() {
		GenAlgSettings.setTournamentSize(2);
		GenAlgSettings.setPopulationSize(2);

		final String firstWinner = runSeededTournament(42, 2);
		final String secondWinner = runSeededTournament(42, 2);

		assertEquals(firstWinner, secondWinner);
	}

	/**
	 * The tournament should draw its players at random from the whole population,
	 * not always take the first few
	 */
	@Test
	public void tournamentSelectionDrawsPlayersFromTheWholePopulation() {
		GenAlgSettings.setTournamentSize(2);
		GenAlgSettings.setPopulationSize(4);

		final Set<Integer> winningPositions = new HashSet<>();
		for (int seed = 0; seed < 20 && !winningPositions.contains(2) && !winningPositions.contains(3); seed++) {
			RandomSource.setSeed(seed);
			final Population population = new Population(4, true);
			final Individual winner = GeneticAlgorithm.tournamentSelection(population);
			winningPositions.add(Arrays.asList(population.getPopulation()).indexOf(winner));
		}

		assertTrue(winningPositions.contains(2) || winningPositions.contains(3), "Only the first two individuals ever won: " + winningPositions);
	}

	/**
	 * Runs a tournament on a freshly seeded population
	 *
	 * @return the chromosome of the winner
	 */
	private String runSeededTournament(final long seed, final int populationSize) {
		RandomSource.setSeed(seed);
		final Population population = new Population(populationSize, true);
		return String.valueOf(GeneticAlgorithm.tournamentSelection(population).getChromosome());
	}

	@Test
	public void evolvePopulationWithElitism() {
		GenAlgSettings.setTournamentSize(2);
		GenAlgSettings.setPopulationSize(3);
		GenAlgSettings.setElitism(true);

		final Population initialPopulation = new Population(3, true);
		final Population evolvedPopulation = GeneticAlgorithm.evolvePopulation(initialPopulation);
		assertNotEquals(initialPopulation, evolvedPopulation);
	}
}
