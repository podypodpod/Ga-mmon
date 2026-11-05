package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for IndividualAttribute
 */
public class IndividualAttributeTest {

	private static final String NAME2 = "attribute2";
	private static final String NAME1 = "attribute1";
	private IndividualAttribute attribute1;
	private IndividualAttribute attribute2;

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
		attribute1 = new IndividualAttribute(NAME1);
		attribute2 = new IndividualAttribute(NAME2);
	}

	@Test
	public void creatingIndividualAttributesShouldBeBetween1And100() {

		assertTrue(attribute1.getValue() >= 0 && attribute1.getValue() <= 100, "randomly created attributes are outside of the range");

		assertTrue(attribute2.getValue() >= 0 && attribute2.getValue() <= 100, "randomly created attributes are outside of the range");
	}

	@Test
	public void creatingIndividualAttributesShouldRetainTheirName() {
		assertEquals(NAME1, attribute1.getName(), "Individuals name doesn't match constructor");
		assertEquals(NAME2, attribute2.getName(), "Individuals name doesn't match constructor");
	}

	@Test
	public void changingIndividualAttributesNamesShouldRetainChangeGetNameValue() {
		//given
		final String newName1 = "new name 1";
		final String newName2 = "new name 2";

		//when
		attribute1.setName(newName1);
		attribute2.setName(newName2);

		//then
		assertEquals(newName1, attribute1.getName(), "setting names on individual doesn't work");
		assertEquals(newName2, attribute2.getName(), "setting names on individual doesn't work");
	}

	@Test
	public void changingIndividualAttributesValuesShouldRetainChangeGetValue() {
		//given
		final int newValue1 = 5;
		final int newValue2 = 6;

		//when
		attribute1.setValue(newValue1);
		attribute2.setValue(newValue2);

		//then
		assertEquals(newValue1, attribute1.getValue(), "setting value doesn't work");
		assertEquals(newValue2, attribute2.getValue(), "setting value doesn't work");
	}

	@Test
	public void overriddenToStringIsNotEmpty() {
		assertFalse(attribute1.toString().isEmpty(), "ToString is empty");
		assertFalse(attribute2.toString().isEmpty(), "ToString is empty");
	}


	@Test
	public void aValueOutsideWhatAGeneCanHoldIsRefused() {
		for (final int value : new int[]{-1, 101, 128}) {
			assertThrows(IllegalArgumentException.class, () -> attribute1.setValue(value));
			assertThrows(IllegalArgumentException.class, () -> new IndividualAttribute(NAME1, value));
		}
	}

	@Test
	public void aValueAtEitherEndIsAllowed() {
		attribute1.setValue(0);
		assertEquals(0, attribute1.getValue());
		attribute1.setValue(100);
		assertEquals(100, attribute1.getValue());
	}
}
