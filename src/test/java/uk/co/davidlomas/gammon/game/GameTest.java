package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	private Individual individual1;
	private Individual individual2;
	private GameManager gameManager;

	@BeforeEach
	public void setup() {
		gameManager = new GameManager();

		// two players with genes of their own, seeded so they're the same each run
		RandomSource.setSeed(9);
		individual1 = new Individual();
		individual2 = new Individual();
	}

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
	}

	/**
	 * Checking that when one player wins a game its returned as the victor, with the higher score
	 */
	@Test
	public void testingGameStatsComesBackWithWinner() {
		RandomSource.setSeed(42);

		final GameStats gameStats = gameManager.playIndividualsVsEachOther(individual1, individual2);

		assertFalse(gameStats.isDraw(), "the seeded game was cut short as a draw, so it has no winner to check");
		if (gameStats.getVictor().equals(individual1)) {
			assertTrue(gameStats.getPlayerOneScore() > gameStats.getPlayerTwoScore());
		} else {
			assertEquals(individual2, gameStats.getVictor());
			assertTrue(gameStats.getPlayerTwoScore() > gameStats.getPlayerOneScore());
		}
	}

	/**
	 * Checking that two games played with the same seed play out exactly the same
	 */
	@Test
	public void testingGamesWithTheSameSeedHaveTheSameResult() {
		RandomSource.setSeed(42);
		final GameStats firstGame = gameManager.playIndividualsVsEachOther(individual1, individual2);

		RandomSource.setSeed(42);
		final GameStats secondGame = gameManager.playIndividualsVsEachOther(individual1, individual2);

		assertEquals(firstGame.getVictor(), secondGame.getVictor());
		assertEquals(firstGame.getPlayerOneScore(), secondGame.getPlayerOneScore(), 0);
		assertEquals(firstGame.getPlayerTwoScore(), secondGame.getPlayerTwoScore(), 0);
	}

	@Test
	public void aGameThatGoesOnTooLongIsADraw() {
		RandomSource.setSeed(3);
		final Game game = new Game(individual1, individual2, 6);

		game.run();

		final GameStats stats = game.getGameStats();
		assertTrue(stats.isDraw());
		assertEquals(WinType.DRAW, stats.getWinType());
		assertEquals(0.0, stats.getPlayerOneScore());
		assertEquals(0.0, stats.getPlayerTwoScore());
		assertNull(stats.getVictor());
	}

	@Test
	public void aGameOnAnInterruptedThreadStopsAsADraw() {
		// as when the GA gives up and interrupts the games it's still playing
		final Game game = new Game(individual1, individual2);
		Thread.currentThread().interrupt();
		try {
			game.run();
		} finally {
			// clears the interrupt, so it doesn't reach the next test
			Thread.interrupted();
		}

		assertTrue(game.getGameStats().isDraw());
	}

	@Test
	public void aGameCutShortCountsOnlyTheTurnsPlayed() {
		final Game game = new Game(individual1, individual2, 6);

		game.run();

		assertEquals(6, game.getGameStats().getTurns());
	}

	@Test
	public void aFinishedGameCountsItsTurns() {
		final Game game = new Game(individual1, individual2);

		game.run();

		assertThat(game.getGameStats().getTurns()).isBetween(1, 300);
	}
}
