package uk.co.davidlomas.gammon.test.genes;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import junit.framework.Assert;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.IndividualAttribute;

public class IndividualSaveTest {
	final Logger logger = LogManager.getLogger(IndividualAttribute.class);

	@Test
	public void savingIndividualToFile() {
		final Individual individual = new Individual();
		final IndividualAttribute bearAPeice = individual.getAttribute(0);
		individual.saveToFile("savingIndividualToFile");
		Assert.assertTrue(false);
	}
}
