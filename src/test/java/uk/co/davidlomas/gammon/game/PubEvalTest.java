package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.test.helpers.TestBoards;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * The scores here were worked out by a separate transliteration of Tesauro's pubeval.c into Python, with his weight
 * files, so they check this one follows the C
 */
public class PubEvalTest {

	private static final double CLOSE = 1e-6;

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
		RandomSource.reset();
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void theStartingPositionScoresAsPubevalDoes(final boolean black) {
		assertEquals(6.840549945831299, PubEval.score(forColour(new Board(), black), black, false), CLOSE);
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void menOnTheBarAndBlotsScoreAsPubevalDoes(final boolean black) {
		final Board board = TestBoards.empty();
		board.setBlackCount(25, 1);
		board.setBlackCount(20, 1);
		board.setBlackCount(13, 4);
		board.setBlackCount(8, 4);
		board.setBlackCount(6, 4);
		board.setBlackCount(4, 1);
		board.setRedCount(0, 2);
		board.setRedCount(1, 1);
		board.setRedCount(12, 4);
		board.setRedCount(17, 3);
		board.setRedCount(19, 4);
		board.setRedCount(22, 1);

		assertEquals(12.616524785757065, PubEval.score(forColour(board, black), black, false), CLOSE);
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aRaceScoresByTheRaceWeights(final boolean black) {
		final Board board = TestBoards.empty();
		board.setBlackCount(6, 4);
		board.setBlackCount(5, 3);
		board.setBlackCount(3, 2);
		board.setBlackBore(6);
		board.setRedCount(24, 5);
		board.setRedCount(20, 3);
		board.setRedCount(19, 2);
		board.setRedBore(5);

		assertEquals(13.118259680271148, PubEval.score(forColour(board, black), black, true), CLOSE);
		assertEquals(9.849745273590088, PubEval.score(forColour(board, black), black, false), CLOSE);
	}

	@Test
	public void theBoardIsReadFromTheMoversPointOfView() {
		final int[] pos = PubEval.position(new Board(), true);
		final int[] expected = new int[28];
		expected[24] = 2;
		expected[13] = 5;
		expected[8] = 3;
		expected[6] = 5;
		expected[1] = -2;
		expected[12] = -5;
		expected[17] = -3;
		expected[19] = -5;
		assertArrayEquals(expected, pos);
	}

	@Test
	public void bearingOffTheLastManIsTheBestMove() {
		final Board board = TestBoards.empty();
		board.setBlackBore(15);
		board.setRedCount(19, 15);

		assertEquals(99999999.0, PubEval.score(board, true, true));
	}

	@Test
	public void itPicksTheHighestScoringBoard() {
		final Board start = new Board();
		final Board better = new Board();
		better.setBlackBore(15);
		for (int point = 0; point < Board.POINTS; point++) {
			better.setBlackCount(point, 0);
		}

		assertSame(better, PubEval.choose(List.of(start, better), true, false));
	}

	@Test
	public void itPlaysWholeGames() {
		RandomSource.setSeed(1);
		final GameManager manager = new GameManager();

		final GameStats stats = manager.playIndividualsVsEachOther(new PubEval(), new Individual());

		// a game that ended, one way or the other
		assertEquals(0, stats.getPlayerOneScore() + stats.getPlayerTwoScore());
	}

	private static Board forColour(final Board blackPosition, final boolean black) {
		return black ? blackPosition : BoardMirror.mirror(blackPosition);
	}
}
