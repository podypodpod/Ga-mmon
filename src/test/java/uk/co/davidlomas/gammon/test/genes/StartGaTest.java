package uk.co.davidlomas.gammon.test.genes;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.genes.StartGa;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import java.io.File;
import java.io.IOException;

public class StartGaTest {

    @BeforeClass
    public static void beforeClass() {
        SettingsUtil.resetSettings();
    }

    @AfterClass
    public static void afterClass() throws IOException {
        SettingsUtil.resetSettings();
        final File directory = new File("savedPlayers/Attempt - 10000");
        FileUtils.deleteDirectory(directory);
    }


    @Before
    public void setup() {
        GenAlgSettings.setElitism(true);
        GenAlgSettings.setGenerations(2);
        GenAlgSettings.setPopulationSize(1);
        GenAlgSettings.setAttemptCount(9999);
        GenAlgSettings.setTournamentSize(1);

        GenAlgSettings.setUniformRate(1);
        GenAlgSettings.setMutationRate(1);
    }

    @Test
    public void createStartGa() {
        StartGa.main(null);
    }

}
