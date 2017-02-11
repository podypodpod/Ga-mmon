package uk.co.davidlomas.gammon.test.genes;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

public class FitnessCalculatorTest {
    @BeforeClass
    public static void setup() {
        SettingsUtil.resetSettings();
    }

    @AfterClass
    public static void afterClass() {
        SettingsUtil.resetSettings();
    }
}
