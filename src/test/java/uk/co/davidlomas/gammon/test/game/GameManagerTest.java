package uk.co.davidlomas.gammon.test.game;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static java.lang.Thread.activeCount;
import static org.junit.Assert.assertTrue;

public class GameManagerTest {

	private Individual individual1;
	private Individual individual2;
	private GameStats gameStats;

	@BeforeClass
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterClass
	public static void afterClass() {
		SettingsUtil.resetSettings();
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
	 * <p>
	 * also returns a score from 0 - 100
	 * <p>
	 * * TODO: SLOW AS HELL
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
	 * * TODO: SLOW AS HELL
	 * <p>
	 * shouldn't happen
	 */
	@Test
	public void testingScoresArentEqual() {
		final double player1Score = gameStats.getPlayerOneScore();
		final double player2Score = gameStats.getPlayerTwoScore();

		assertTrue("Scores are equal, who wins?", player1Score != player2Score);
	}

	/**
	 * Normally 2 threads,
	 * gui is 3rd thread
	 * <p>
	 * checking there is 3 threads when using a gui
	 */
	@Test
	public void testingThatPlayingIndividualsAgainstEachOtherWithGuiSettingOnCreatesaThirdGuiThread() {
		GameSettings.setDisplayGUICache(true);
		new GameManager().playIndividualsVsEachOther(individual1, individual2);
		assertTrue(activeCount() > 2);
	}

}
