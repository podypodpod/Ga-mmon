package uk.co.davidlomas.gammon.test.genes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import testingCode.Settings;
import uk.co.davidlomas.gammon.genes.IndividualAttribute;

public class IndividualAttributeTest {
	private static final String NAME2 = "attribute2";
	private static final String NAME1 = "attribute1";
	private IndividualAttribute attribute1;
	private IndividualAttribute attribute2;

	@BeforeClass
	public static void beforeClass() {
		Settings.resettSettings();
	}

	@AfterClass
	public static void afterClass() {
		Settings.resettSettings();
	}

	@Before
	public void setup() {
		attribute1 = new IndividualAttribute(NAME1);
		attribute2 = new IndividualAttribute(NAME2);
	}

	@Test
	public void creatingIndividualAttributesShouldBeBetween1And100() {
		assertTrue(attribute1.getValue() >= 0 && attribute1.getValue() <= 100);
		assertTrue(attribute2.getValue() >= 0 && attribute2.getValue() <= 100);
	}

	@Test
	public void creatingIndividualAttributesShouldRetainTheirName() {
		assertEquals(NAME1, attribute1.getName());
		assertEquals(NAME2, attribute2.getName());
	}

	@Test
	public void changingIndividualAttributesNamesShouldRetainChangeGetNameValue() {
		final String newName1 = "new name 1";
		final String newName2 = "new name 2";

		attribute1.setName(newName1);
		attribute2.setName(newName2);

		assertEquals(newName1, attribute1.getName());
		assertEquals(newName2, attribute2.getName());
	}

	@Test
	public void changingIndividualAttributesValuesShouldRetainChangeGetValue() {
		final int newValue1 = 5;
		final int newValue2 = 6;

		attribute1.setValue(newValue1);
		attribute2.setValue(newValue2);

		assertEquals(newValue1, attribute1.getValue());
		assertEquals(newValue2, attribute2.getValue());
	}

	@Test
	public void overiddenToStringIsNotEmpty() {
		assertFalse(attribute1.toString().isEmpty());
		assertFalse(attribute2.toString().isEmpty());
	}

}
