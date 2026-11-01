package uk.co.davidlomas.gammon.test.game;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameTest {

	@BeforeClass
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterClass
	public static void afterClass() {
		SettingsUtil.resetSettings();
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

	@After
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
}
