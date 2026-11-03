package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * How a game was won, from where the loser's pieces are when the winner bears off their last. Each position has red
 * winning against black, and is mirrored to check black winning against red.
 */
public class WinTypeTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@ParameterizedTest(name = "loser black {0}")
	@ValueSource(booleans = {true, false})
	public void aLoserWhoHasBorneOffAPieceLosesASingleGame(final boolean loserBlack) {
		final Board blackLosing = redHasWon();
		blackLosing.Points[3].setBlackCount(14);
		blackLosing.setBlackBore(1);

		assertEquals(WinType.SINGLE, forLoser(blackLosing, loserBlack).winTypeAgainst(loserBlack));
	}

	@ParameterizedTest(name = "loser black {0}")
	@ValueSource(booleans = {true, false})
	public void aLoserWhoHasBorneOffNothingLosesAGammon(final boolean loserBlack) {
		final Board blackLosing = redHasWon();
		blackLosing.Points[3].setBlackCount(10);
		blackLosing.Points[13].setBlackCount(5);

		assertEquals(WinType.GAMMON, forLoser(blackLosing, loserBlack).winTypeAgainst(loserBlack));
	}

	@ParameterizedTest(name = "loser black {0}")
	@ValueSource(booleans = {true, false})
	public void aLoserWithAPieceInTheWinnersHomeLosesABackgammon(final boolean loserBlack) {
		final Board blackLosing = redHasWon();
		blackLosing.Points[3].setBlackCount(14);
		// red's home quarter is 19 to 24
		blackLosing.Points[24].setBlackCount(1);

		assertEquals(WinType.BACKGAMMON, forLoser(blackLosing, loserBlack).winTypeAgainst(loserBlack));
	}

	@ParameterizedTest(name = "loser black {0}")
	@ValueSource(booleans = {true, false})
	public void aLoserWithAPieceOnTheBarLosesABackgammon(final boolean loserBlack) {
		final Board blackLosing = redHasWon();
		blackLosing.Points[3].setBlackCount(14);
		// black's bar
		blackLosing.Points[25].setBlackCount(1);

		assertEquals(WinType.BACKGAMMON, forLoser(blackLosing, loserBlack).winTypeAgainst(loserBlack));
	}

	@Test
	public void eachWinIsWorthItsPoints() {
		assertEquals(1, WinType.SINGLE.getPoints());
		assertEquals(2, WinType.GAMMON.getPoints());
		assertEquals(3, WinType.BACKGAMMON.getPoints());
		assertEquals("a gammon, worth 2 points", WinType.GAMMON.describe());
		assertEquals("a single game, worth 1 point", WinType.SINGLE.describe());
	}

	/**
	 * An empty board where red has borne off all 15 pieces, for a test to place black's
	 */
	private static Board redHasWon() {
		final Board board = new Board();
		for (final Point point : board.Points) {
			point.setBlackCount(0);
			point.setRedCount(0);
		}
		board.setRedBore(15);
		return board;
	}

	private static Board forLoser(final Board blackLosing, final boolean loserBlack) {
		return loserBlack ? blackLosing : BoardMirror.mirror(blackLosing);
	}
}
