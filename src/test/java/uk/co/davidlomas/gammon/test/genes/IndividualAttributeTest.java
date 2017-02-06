package uk.co.davidlomas.gammon.test.genes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.co.davidlomas.gammon.genes.IndividualAttribute;
import uk.co.davidlomas.gammon.test.helpers.Settings;

/**
 * Test class for IndividualAttribute
 */
public class IndividualAttributeTest {

  private static final String NAME2 = "attribute2";
  private static final String NAME1 = "attribute1";
  private IndividualAttribute attribute1;
  private IndividualAttribute attribute2;

  @BeforeClass
  public static void beforeClass() {
    Settings.resetSettings();
  }

  @AfterClass
  public static void afterClass() {
    Settings.resetSettings();
  }

  @Before
  public void setup() {
    attribute1 = new IndividualAttribute(NAME1);
    attribute2 = new IndividualAttribute(NAME2);
  }

  @Test
  public void creatingIndividualAttributesShouldBeBetween1And100() {

    assertTrue("randomly created attributes are outside of the range",
        attribute1.getValue() >= 0 && attribute1.getValue() <= 100);

    assertTrue("randomly created attributes are outside of the range",
        attribute2.getValue() >= 0 && attribute2.getValue() <= 100);
  }

  @Test
  public void creatingIndividualAttributesShouldRetainTheirName() {
    assertEquals("Individuals name doesn't match constructor", NAME1, attribute1.getName());
    assertEquals("Individuals name doesn't match constructor", NAME2, attribute2.getName());
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
    assertEquals("setting names on individual doesn't work", newName1, attribute1.getName());
    assertEquals("setting names on individual doesn't work", newName2, attribute2.getName());
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
    assertEquals("setting value doesn't work",newValue1, attribute1.getValue());
    assertEquals("setting value doesn't work",newValue2, attribute2.getValue());
  }

  @Test
  public void overriddenToStringIsNotEmpty() {
    assertFalse("ToString is empty", attribute1.toString().isEmpty());
    assertFalse("ToString is empty",attribute2.toString().isEmpty());
  }

}
