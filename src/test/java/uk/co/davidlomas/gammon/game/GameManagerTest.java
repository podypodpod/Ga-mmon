package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import static java.lang.Thread.activeCount;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameManagerTest {

	private Individual individual1;
	private Individual individual2;
	private GameStats gameStats;

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
		RandomSource.setSeed(42);
		individual1 = new Individual();
		individual2 = new Individual();
		gameStats = new GameManager().playIndividualsVsEachOther(individual1,
				individual2);
	}

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
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

		assertTrue(victor.equals(individual2) || victor.equals(individual1), "Victor is not one of the players");

		final double player1Score = gameStats.getPlayerOneScore();
		final double player2Score = gameStats.getPlayerTwoScore();

		assertTrue(player1Score >= 0 && player1Score <= 100, "score is out of bounds");
		assertTrue(player2Score >= 0 && player2Score <= 100, "score is out of bounds");
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
			assertTrue(victor.equals(individual1), "Victor didn't have largest score");
		} else {
			assertTrue(victor.equals(individual2), "Victor didn't have largest score");
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

		assertTrue(player1Score != player2Score, "Scores are equal, who wins?");
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
