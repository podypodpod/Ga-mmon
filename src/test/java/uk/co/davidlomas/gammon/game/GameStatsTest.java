package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class GameStatsTest {
	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void keepsTheVictorHowTheyWonAndBothScores() {
		final Individual victor = new Individual();

		final GameStats gameStats = new GameStats(victor, WinType.GAMMON, 2.0, -2.0);

		assertSame(victor, gameStats.getVictor());
		assertSame(WinType.GAMMON, gameStats.getWinType());
		assertEquals(2.0, gameStats.getPlayerOneScore());
		assertEquals(-2.0, gameStats.getPlayerTwoScore());
	}
}
