package uk.co.davidlomas.gammon.test.game;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.IndividualAttribute;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

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

	/**
	 * Checking that when one player wins a game its returned as the victor
	 */
	@Test
	public void testingGameStatsComesBackWithWinner() {
		// GIVEN two equal players
		// AND player 1 cannot bear and therefore win
		individual1.setAttribute(0, new IndividualAttribute("bearAPiece", 0));

		// WHEN they play each other
		final GameStats gameStats = gameManager.playIndividualsVsEachOther(individual1, individual2);

		// THEN player 2 must win
		assertTrue(gameStats.getVictor().equals(individual2));
		assertTrue("Crap player managed to win (impossible)", gameStats.getPlayerTwoScore() > gameStats.getPlayerOneScore());
	}
}
