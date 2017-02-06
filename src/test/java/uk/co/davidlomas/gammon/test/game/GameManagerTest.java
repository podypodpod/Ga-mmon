package uk.co.davidlomas.gammon.test.game;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.Settings;

public class GameManagerTest {

  private Individual individual1;
  private Individual individual2;
  private GameStats gameStats;

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
    individual1 = new Individual();
    individual2 = new Individual();
    gameStats = new GameManager().playIndividualsVsEachOther(individual1,
        individual2);
  }

  /**
   * Checking the game stats returns the victor which is one the two players involved,
   *
   * also returns a score from 0 - 100
   */
  @Test
  public void testingPlayingIndividualsAgainstEachOtherReturnsAVictor() {
    final Individual victor = gameStats.getVictor();

    assertTrue("Victor is not one of the players",
        victor.equals(individual2) || victor.equals(individual1));

    final double player1Score = gameStats.getPlayerOneScore();
    final double player2Score = gameStats.getPlayerTwoScore();

    assertTrue("score is out of bounds", player1Score >= 0 && player1Score <= 100);
    assertTrue("score is out of bounds", player2Score >= 0 && player2Score <= 100);
  }

  /**
   * Checking the victor is the player with the highest score
   */
  @Test
  public void testingPlayerWithHighestScoreIsVictor() {

    final Individual victor = gameStats.getVictor();
    final double player1Score = gameStats.getPlayerOneScore();
    final double player2Score = gameStats.getPlayerTwoScore();

    if (player1Score > player2Score) {
      assertTrue("Victor didn't have largest score", victor.equals(individual1));
    } else {
      assertTrue("Victor didn't have largest score", victor.equals(individual2));
    }
  }

  /**
   * Making sure the scores aren't equal as Ive not decided who would win there
   *
   * shouldn't happen
   */
  @Test
  public void testingScoresArentEqual() {
    final double player1Score = gameStats.getPlayerOneScore();
    final double player2Score = gameStats.getPlayerTwoScore();

    assertTrue("Scores are equal, who wins?", player1Score != player2Score);

  }

}
