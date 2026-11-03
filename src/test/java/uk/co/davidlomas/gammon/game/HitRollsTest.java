package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Hit rolls is counted with masks worked out per opponent piece and blot when the opponent has nothing on the bar,
 * as that's quicker, and roll by roll otherwise. These check the two give the same count, for both players, on
 * thousands of positions.
 */
public class HitRollsTest {

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

	@Test
	public void theMasksCountTheSameAsRollByRollInPlayedPositions() {
		// positions from random legal moves from the start, as games reach them
		int compared = 0;
		for (long seed = 0; seed < 200; seed++) {
			RandomSource.setSeed(seed);
			Board board = new Board();
			for (int turn = 0; turn < 20; turn++) {
				final boolean black = turn % 2 == 0;
				compared += compare(board, true) + compare(board, false);
				final List<Board> moves = MoveGenerator.legalMoves(board, black, new DiceList().RollTwoDice()).getBoards();
				if (!moves.isEmpty()) {
					board = moves.get(RandomSource.nextInt(moves.size()));
				}
			}
		}
		assertTrue(compared > 1000, "only " + compared + " positions had the opponent off the bar");
	}

	@Test
	public void theMasksCountTheSameAsRollByRollWithPiecesScatteredAtRandom() {
		// lots of blots and made points in odd places, which played positions rarely have
		final Random random = new Random(11);
		int compared = 0;
		for (int position = 0; position < 3000; position++) {
			compared += compare(scattered(random), true) + compare(scattered(random), false);
		}
		assertTrue(compared > 3000, "only " + compared + " positions had the opponent off the bar");
	}

	/**
	 * Compares the two ways of counting for the player, if the opponent has nothing on the bar
	 *
	 * @return 1 if it compared them, 0 if the opponent was on the bar
	 */
	private static int compare(final Board board, final boolean black) {
		final Feature.Side side = new Feature.Side(board, black);
		if (side.theirs(0) > 0) {
			return 0;
		}
		final int expected = byTheRules(side);
		assertEquals(expected, Feature.HIT_ROLLS.measure(side));
		assertEquals(expected, Feature.countRollByRoll(side));
		return 1;
	}

	/**
	 * The count worked out here from the rules, apart from the game's code: for each of the 36 rolls, whether some
	 * opponent piece reaches some blot with one die, with both when one of the points between is open, or with a
	 * double whose points on the way are open
	 */
	private static int byTheRules(final Feature.Side side) {
		int count = 0;
		for (int die1 = 1; die1 <= 6; die1++) {
			for (int die2 = 1; die2 <= 6; die2++) {
				if (anyBlotHit(side, die1, die2)) {
					count++;
				}
			}
		}
		return count;
	}

	private static boolean anyBlotHit(final Feature.Side side, final int die1, final int die2) {
		for (int blot = 1; blot <= 24; blot++) {
			if (side.mine(blot) != 1) {
				continue;
			}
			for (int from = 1; from < blot; from++) {
				if (side.theirs(from) > 0 && reaches(side, from, blot - from, die1, die2)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean reaches(final Feature.Side side, final int from, final int distance, final int die1,
								   final int die2) {
		if (die1 == die2) {
			for (int step = 1; step <= 4; step++) {
				if (die1 * step == distance) {
					return true;
				}
				if (made(side, from + die1 * step)) {
					return false;
				}
			}
			return false;
		}
		return distance == die1 || distance == die2
				|| distance == die1 + die2 && (!made(side, from + die1) || !made(side, from + die2));
	}

	private static boolean made(final Feature.Side side, final int point) {
		return point >= 1 && point <= 24 && side.mine(point) >= 2;
	}

	/**
	 * 15 pieces each, dropped on random points, one colour to a point
	 */
	private static Board scattered(final Random random) {
		final Board board = new Board();
		for (final Point point : board.Points) {
			point.setBlackCount(0);
			point.setRedCount(0);
		}
		for (int piece = 0; piece < 15; piece++) {
			int point;
			do {
				point = 1 + random.nextInt(24);
			} while (board.Points[point].getRedCount() > 0);
			board.Points[point].addBlackPiece();
			do {
				point = 1 + random.nextInt(24);
			} while (board.Points[point].getBlackCount() > 0);
			board.Points[point].addRedPiece();
		}
		return board;
	}
}
