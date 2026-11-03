package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
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
	public void mutationChangesTheGenesTheGamePlaysBy() {
		// flipping every bit changes every gene's value: 7 bits a gene, capped at 100
		GenAlgSettings.setMutationRate(1.00);
		final int[] before = geneValues(individual1);

		GeneticAlgorithm.mutate(individual1);

		final int[] after = geneValues(individual1);
		for (int gene = 0; gene < before.length; gene++) {
			assertNotEquals(before[gene], after[gene], "gene " + gene);
		}
	}

	@Test
	public void aMutatedPlayerIsSavedWithItsMutation() {
		// saving rebuilds the chromosome from the genes, which used to put the old bits back
		GenAlgSettings.setMutationRate(1.00);
		GeneticAlgorithm.mutate(individual1);
		final String mutated = String.valueOf(individual1.getChromosome());
		final String file = SettingsUtil.getPlayersDir() + "/mutated - " + UUID.randomUUID();
		new File(SettingsUtil.getPlayersDir()).mkdirs();

		individual1.saveToFile(file);
		final Individual loaded = new Individual();
		loaded.loadFromFile(file);

		assertArrayEquals(geneValues(individual1), geneValues(loaded));
		assertEquals(mutated, String.valueOf(individual1.getChromosome()));
	}

	@Test
	public void mutatingAChildDoesntChangeItsParents() {
		final int[] firstParent = geneValues(individual1);
		final int[] secondParent = geneValues(individual2);
		final Individual child = GeneticAlgorithm.crossoverIndividuals(individual1, individual2);
		GenAlgSettings.setMutationRate(1.00);

		GeneticAlgorithm.mutate(child);

		assertArrayEquals(firstParent, geneValues(individual1));
		assertArrayEquals(secondParent, geneValues(individual2));
	}

	private static int[] geneValues(final Individual individual) {
		final int[] values = new int[individual.getNumOfAttributes()];
		for (int gene = 0; gene < values.length; gene++) {
			values[gene] = individual.getAttribute(gene).getValue();
		}
		return values;
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

	@Test
	public void aTournamentOfTheWholePopulationPicksTheFittest() {
		final Population population = populationWithFitness(1.5, -2.0, 3.0, 0.5);
		GenAlgSettings.setTournamentSize(4);

		assertSame(population.getIndividual(2), GeneticAlgorithm.tournamentSelection(population));
	}

	@Test
	public void theLeastFitPlayerNeverWinsATournament() {
		// with two in each tournament, the least fit always meets someone fitter
		final Population population = populationWithFitness(1.5, -2.0, 3.0, 0.5);
		GenAlgSettings.setTournamentSize(2);

		for (long seed = 0; seed < 50; seed++) {
			RandomSource.setSeed(seed);
			assertNotSame(population.getIndividual(1), GeneticAlgorithm.tournamentSelection(population), "seed " + seed);
		}
	}

	@Test
	public void aTournamentLargerThanThePopulationUsesTheWholePopulation() {
		final Population population = populationWithFitness(1.5, -2.0, 3.0, 0.5);
		GenAlgSettings.setTournamentSize(8);

		assertSame(population.getIndividual(2), GeneticAlgorithm.tournamentSelection(population));
	}

	private static Population populationWithFitness(final double... fitness) {
		final Population population = new Population(fitness.length, true);
		for (int i = 0; i < fitness.length; i++) {
			population.getIndividual(i).setFitness(fitness[i]);
		}
		return population;
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
