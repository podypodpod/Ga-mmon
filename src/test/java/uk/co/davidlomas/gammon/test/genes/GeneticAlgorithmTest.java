package uk.co.davidlomas.gammon.test.genes;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.genes.GeneticAlgorithm;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.IndividualAttribute;
import uk.co.davidlomas.gammon.genes.Population;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test class for {@link GeneticAlgorithm}
 */
public class GeneticAlgorithmTest {

	private Individual individual1;
	private Individual individual2;

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
		individual1 = new Individual();
		individual2 = new Individual();
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
	public void havingATournamentWithAPlayerWith0BearAPieceShouldAlwaysMakeThemLoose() {
		GenAlgSettings.setTournamentSize(2);
		GenAlgSettings.setPopulationSize(2);

		// individual 1 has a HIGH value on bearing pieces
		final IndividualAttribute attr1 = new IndividualAttribute("bearAPiece");
		attr1.setValue(100);
		individual1.setAttribute(0, attr1);

		// individual 1 has a LOW value on bearing pieces
		final IndividualAttribute attr2 = new IndividualAttribute("bearAPiece");
		attr2.setValue(0);
		individual2.setAttribute(0, attr2);

		// make tournament out of the 2
		final Population population = new Population(2, true);
		final Individual[] individuals = population.getPopulation();
		individuals[0] = individual1;
		individuals[1] = individual2;
		population.setIndividuals(individuals);

		// make sure individual 1 wins
		final Individual winner = GeneticAlgorithm.tournamentSelection(population);
		assertEquals(individual1, winner);
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
