package uk.co.davidlomas.gammon.test.game;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.IndividualAttribute;
import uk.co.davidlomas.gammon.test.helpers.Settings;

import static org.junit.Assert.assertTrue;

public class GameTest {

    @BeforeClass
    public static void beforeClass() {
        Settings.resetSettings();
    }

    @AfterClass
    public static void afterClass() {
        Settings.resetSettings();
    }

    private Individual individual1;
    private Individual individual2;
    private GameManager gameManager;

    @Before
    public void setup() {
        gameManager = new GameManager();

        individual1 = new Individual();
        individual1.loadFromFile("savedPlayers/Attempt - 2014 - 1/PlayerFromGen0.properties");

        individual2 = new Individual();
        individual2.loadFromFile("savedPlayers/Attempt - 2014 - 1/PlayerFromGen9.properties");
    }

    /**
     * Checking that when one player wins a game its returned as the victor
     * TODO: SLOW AS HELL
     */
    @Test
    public void testingGameStatsComesBackWithWinner() {
        //given
        //individual cant actually finish if it cant bear
        individual1.setAttribute(0, new IndividualAttribute("bearAPiece", 0));

        //when
        final GameStats gameStats = gameManager.playIndividualsVsEachOther(individual1, individual2);

        //then
        assertTrue(gameStats.getVictor().equals(individual2));
        assertTrue("Crap player managed to win (impossible)", gameStats.getPlayerTwoScore() > gameStats.getPlayerOneScore());
    }
}
