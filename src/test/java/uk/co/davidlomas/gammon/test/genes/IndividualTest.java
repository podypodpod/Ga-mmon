package uk.co.davidlomas.gammon.test.genes;

import java.io.File;
import java.util.UUID;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.IndividualAttribute;

public class IndividualTest {
	final Logger logger = LogManager.getLogger(IndividualAttribute.class);

	private Individual individual1;
	private Individual individual2;
	UUID uuid;
	String filePath;

	@Before
	public void setup() {
		individual1 = new Individual();
		individual2 = new Individual();
		uuid = UUID.randomUUID();
		filePath = "savedPlayers/test/testIndividual - " + uuid.toString();
	}

	@Test
	public void savingAndLoadingIndividualCheckingPropertiesMatch() {

		// save
		individual1 = new Individual();
		individual1.saveToFile(filePath);
		Assert.assertTrue(new File(filePath).exists());

		// load
		individual2 = new Individual();
		individual2.loadFromFile(filePath);

		// check num of attributes is same
		Assert.assertEquals(individual1.getNumOfAttributes(), individual2.getNumOfAttributes());

		// check all attr
		for (int i = 0; i < individual1.getNumOfAttributes(); i++) {
			Assert.assertEquals(individual1.getAttribute(i).getName(), individual2.getAttribute(i).getName());
			Assert.assertEquals(individual1.getAttribute(i).getValue(), individual2.getAttribute(i).getValue());
		}
	}

	@Test
	public void savingIndividualToFile() {
		final UUID uuid = UUID.randomUUID();
		final String filePath = "savedPlayers/test/testIndividual - " + uuid.toString();
		individual1.saveToFile(filePath);
		Assert.assertTrue(new File(filePath).exists());
	}

}