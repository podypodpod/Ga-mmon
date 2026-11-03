package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.gui.BoardContainerFrame;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

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
	 * and that both scores are between -3 and 3
	 * <p>
	 * * TODO: SLOW AS HELL
	 */
	@Test
	public void testingPlayingIndividualsAgainstEachOtherReturnsAVictor() {
		final Individual victor = gameStats.getVictor();

		assertTrue(victor.equals(individual2) || victor.equals(individual1), "Victor is not one of the players");

		final double player1Score = gameStats.getPlayerOneScore();
		final double player2Score = gameStats.getPlayerTwoScore();

		assertTrue(player1Score >= -3 && player1Score <= 3, "score is out of bounds");
		assertTrue(player2Score >= -3 && player2Score <= 3, "score is out of bounds");
	}

	/**
	 * The winner gains the points their kind of win is worth, 1, 2 or 3, and the loser loses them
	 */
	@Test
	public void theWinnerScoresWhatTheirWinIsWorth() {
		final double winnersScore = gameStats.getVictor() == individual1
				? gameStats.getPlayerOneScore() : gameStats.getPlayerTwoScore();
		final double losersScore = gameStats.getVictor() == individual1
				? gameStats.getPlayerTwoScore() : gameStats.getPlayerOneScore();

		assertEquals(gameStats.getWinType().getPoints(), winnersScore);
		assertEquals(-gameStats.getWinType().getPoints(), losersScore);
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
	 * With the GUI on, the first game opens the board window and the games after it draw in the same one, instead of
	 * each opening its own
	 */
	@Test
	public void withTheGuiOnEveryGameDrawsInTheSameWindow() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		GameSettings.setDisplayGUICache(true);
		// closing the window only closes it: the real one ends the program, which would end the test run
		GameManager.windowCloseOperation = WindowConstants.DISPOSE_ON_CLOSE;
		try {
			new GameManager().playIndividualsVsEachOther(individual1, individual2);
			final BoardContainerFrame window = GameManager.boardContainerFrame;
			assertEquals(WindowConstants.DISPOSE_ON_CLOSE, window.getDefaultCloseOperation());

			new GameManager().playIndividualsVsEachOther(individual2, individual1);

			assertTrue(window.isShowing(), "the board window isn't showing");
			assertSame(window, GameManager.boardContainerFrame, "the second game opened another window");
		} finally {
			GameSettings.setDisplayGUICache(false);
			GameManager.windowCloseOperation = WindowConstants.EXIT_ON_CLOSE;
			final BoardContainerFrame window = GameManager.boardContainerFrame;
			GameManager.boardContainerFrame = null;
			if (window != null) {
				SwingUtilities.invokeAndWait(window::dispose);
			}
		}
	}

}
