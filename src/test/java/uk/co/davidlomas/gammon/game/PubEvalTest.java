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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The scores here were worked out by a separate transliteration of Tesauro's pubeval.c into Python, with his weight
 * files and float arithmetic at every step as in the C, so they check this one follows it exactly
 */
public class PubEvalTest {

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
		assertEquals(6.840550422668457f, PubEval.score(forColour(new Board(), black), black, false));
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

		assertEquals(12.616525650024414f, PubEval.score(forColour(board, black), black, false));
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

		assertEquals(13.118260383605957f, PubEval.score(forColour(board, black), black, true));
		assertEquals(9.849745750427246f, PubEval.score(forColour(board, black), black, false));
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

		// 99999999 in pubeval.c, which is 1e8 as a float
		assertEquals(1e8f, PubEval.score(board, true, true));
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
	public void inAGameItChoosesByTheWeightsForThePositionBeforeTheMove() {
		// contact before the move, as red's piece on 3 hasn't passed black's on 4, but a 2-3 can leave a race. It came
		// up in a game, and the two sets of weights choose differently in it
		final Board before = TestBoards.empty();
		before.setBlackCount(1, 2);
		before.setBlackCount(2, 2);
		before.setBlackCount(4, 2);
		before.setBlackBore(9);
		before.setRedCount(3, 1);
		before.setRedCount(8, 2);
		before.setRedCount(22, 2);
		before.setRedCount(23, 5);
		before.setRedCount(24, 5);
		assertTrue(Feature.isContact(before));
		final List<Integer> roll = List.of(2, 3);
		final List<Board> moves = MoveGenerator.legalMoves(before, true, roll).getBoards();
		final Board byContact = PubEval.choose(moves, true, false);
		final Board byRace = PubEval.choose(moves, true, true);
		// so the test can tell which weights were used
		assertNotEquals(byContact, byRace);

		final PubEval pubEval = new PubEval();
		final AiPlayer player = new AiPlayer(true, pubEval);
		player.movesLeft.setTo(roll);
		final Board played = new MoveGenerator(pubEval).getNextMoveBoard(before, player);

		assertEquals(byContact, played);
	}

	@Test
	public void itPlaysWholeGamesByItsOwnRules() {
		final List<Board> notTheBest = new ArrayList<>();
		final GameManager manager = new GameManager();
		manager.setObserver(new GameObserver() {
			@Override
			public void turnPlayed(final Board before, final Board board, final boolean black,
								   final List<Integer> dice) {
				// pubeval is player one, black, in this game. Its scores are compared rather than its choice, as
				// choosing between equal boards takes the game's random numbers
				if (black) {
					final boolean race = !Feature.isContact(before);
					float best = Float.NEGATIVE_INFINITY;
					for (final Board move : MoveGenerator.legalMoves(before, true, dice).getBoards()) {
						best = Math.max(best, PubEval.score(move, true, race));
					}
					if (best != Float.NEGATIVE_INFINITY && PubEval.score(board, true, race) != best) {
						notTheBest.add(board);
					}
				}
			}
		});
		RandomSource.setSeed(1);

		final GameStats stats = manager.playToWatch(new PubEval(), new Individual());

		assertTrue(stats.getTurns() > 10, "it should have been a whole game");
		assertTrue(notTheBest.isEmpty(), "every move should score highest by pubeval: " + notTheBest.size() + " didn't");
	}

	private static Board forColour(final Board blackPosition, final boolean black) {
		return black ? blackPosition : BoardMirror.mirror(blackPosition);
	}
}
