package uk.co.davidlomas.gammon.test.genes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.Population;

public class PopulationTest {
	private static final int POPULATION_SIZE = 10;
	Population initPopulation;
	Population population;

	@Before
	public void setup() {
		initPopulation = new Population(POPULATION_SIZE, true);
		population = new Population(POPULATION_SIZE, false);

	}

	@Test
	public void checkPopulationSizeIsRight() {
		assertEquals(POPULATION_SIZE, initPopulation.getPopulation().length);
	}

	@Test
	public void checkGetFittestGetsIndiviudalWithHighestFitness() {

		final Individual[] individuals = initPopulation.getPopulation();
		final Individual fitIndividual = new Individual();
		fitIndividual.setFitness(99);
		individuals[5] = fitIndividual;
		initPopulation.setIndividuals(individuals);

		final Individual actual = initPopulation.getFittest();

		assertEquals(fitIndividual, actual);
	}

	// too slow to always run
	@Ignore
	@Test
	public void calculateFitnessChangesOrder() {
		final double before = initPopulation.getFittest().getFitness();
		initPopulation.calculateFitness();
		final double after = initPopulation.getFittest().getFitness();
		assertNotEquals(before, after);
	}

}
