package uk.co.davidlomas.gammon.test.game;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.IndividualAttribute;
import uk.co.davidlomas.gammon.test.helpers.Settings;

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

	final static Logger logger = LoggerFactory.getLogger(GameStatsTest.class);

	@Test
	public void testingGameStatsComesBackWithWinner() {
		individual1.setAttribute(0, new IndividualAttribute("bearAPiece", 0));
		final GameStats gameStats = gameManager.playIndividualsVsEachOther(individual1, individual2);
		assertTrue(gameStats.getVictor().equals(individual2));
		assertTrue(gameStats.getPlayerTwoScore() > gameStats.getPlayerOneScore());
	}
}
