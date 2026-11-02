package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class AiPlayerTest {
	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aTurnFromTheStartMovesOnlyThePlayersOwnPieces(final boolean black) {
		for (long seed = 0; seed < 10; seed++) {
			RandomSource.setSeed(seed);
			final Board start = new Board();

			final Board after = new AiPlayer(black, new Individual()).AiTurn(start);

			assertNotEquals(start, after, "seed " + seed);
			for (int point = 0; point < start.Points.length; point++) {
				assertEquals(countFor(!black, start.Points[point]), countFor(!black, after.Points[point]),
						"opponent's pieces moved on point " + point + ", seed " + seed);
			}
			assertEquals(15, piecesOnBoard(after, black), "seed " + seed);
		}
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPlayerWhoCantComeInOffTheBarKeepsTheSameBoard(final boolean black) {
		// Black has a piece on the bar and red holds every point in black's way in, so no roll lets it move
		final Board blackPosition = new Board();
		for (final Point point : blackPosition.Points) {
			point.setBlackCount(0);
			point.setRedCount(0);
		}
		blackPosition.Points[25].setBlackCount(1);
		blackPosition.Points[6].setBlackCount(14);
		for (int point = 19; point <= 24; point++) {
			blackPosition.Points[point].setRedCount(2);
		}
		blackPosition.Points[1].setRedCount(3);
		final Board start = black ? blackPosition : BoardMirror.mirror(blackPosition);

		for (long seed = 0; seed < 10; seed++) {
			RandomSource.setSeed(seed);

			assertSame(start, new AiPlayer(black, new Individual()).AiTurn(start), "seed " + seed);
		}
	}

	private static int countFor(final boolean black, final Point point) {
		return black ? point.getBlackCount() : point.getRedCount();
	}

	private static int piecesOnBoard(final Board board, final boolean black) {
		int total = 0;
		for (final Point point : board.Points) {
			total += countFor(black, point);
		}
		return total;
	}
}
