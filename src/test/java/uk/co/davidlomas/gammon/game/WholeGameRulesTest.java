package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.test.helpers.TestBoards;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Checks the moves the game allows against a second, separate working out of backgammon's rules, over the positions
 * real games reach and random ones, for every roll and both players. A bug in the rules would have to be made twice,
 * in two different ways, to get through.
 */
public class WholeGameRulesTest {

	private static final int GAMES = 2;
	private static final int RANDOM_POSITIONS = 100;
	private static final int MAX_TURNS = 300;
	private static final int PIECES = 15;

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
	public void piecesAreNeitherLostNorMadeInWholeGames() {
		for (final Board board : boardsFromGames()) {
			assertPiecesAllThere(board);
		}
	}

	@Test
	public void everyPositionInWholeGamesAllowsExactlyTheMovesTheRulesDo() {
		for (final Board board : boardsFromGames()) {
			assertAllowsWhatTheRulesDo(board);
		}
	}

	@Test
	public void everyRandomPositionAllowsExactlyTheMovesTheRulesDo() {
		final Random random = new Random(3);
		for (int i = 0; i < RANDOM_POSITIONS; i++) {
			assertAllowsWhatTheRulesDo(randomPosition(random, i % 2 == 0));
		}
	}

	private static List<Board> boardsFromGames() {
		final List<Board> boards = new ArrayList<>();
		for (int game = 0; game < GAMES; game++) {
			RandomSource.setSeed(game);
			final AiPlayer black = new AiPlayer(true, new Individual());
			final AiPlayer red = new AiPlayer(false, new Individual());
			Board board = new Board();
			boards.add(board);
			for (int turn = 0; turn < MAX_TURNS && !board.hasPlayerWon(true) && !board.hasPlayerWon(false); turn++) {
				board = (turn % 2 == 0 ? black : red).AiTurn(board);
				boards.add(board);
			}
		}
		return boards;
	}

	private static void assertPiecesAllThere(final Board board) {
		int black = board.getBlackBore();
		int red = board.getRedBore();
		for (int point = 0; point < Board.POINTS; point++) {
			black += board.getBlackCount(point);
			red += board.getRedCount(point);
			assertFalse(board.getBlackCount(point) > 0 && board.getRedCount(point) > 0, "both colours on " + point);
		}
		assertEquals(PIECES, black);
		assertEquals(PIECES, red);
		assertEquals(0, board.getBlackCount(0), "black on red's bar");
		assertEquals(0, board.getRedCount(25), "red on black's bar");
	}

	private static void assertAllowsWhatTheRulesDo(final Board board) {
		for (final boolean black : new boolean[]{true, false}) {
			for (int first = 1; first <= 6; first++) {
				for (int second = 1; second <= 6; second++) {
					final List<Integer> roll = first == second ? List.of(first, first, first, first) : List.of(first, second);
					final Set<Board> expected = Rules.finishingBoards(board, black, roll);
					final Set<Board> actual = new HashSet<>(MoveGenerator.legalMoves(board, black, roll).getBoards());
					assertEquals(expected, actual, () -> describe(board, black, roll));
				}
			}
		}
	}

	private static String describe(final Board board, final boolean black, final List<Integer> roll) {
		final StringBuilder position = new StringBuilder((black ? "black" : "red") + " rolling " + roll + " on");
		for (int point = 0; point < Board.POINTS; point++) {
			if (board.numEither(point) > 0) {
				position.append(' ').append(point).append(board.getCol(point) ? "b" : "r").append(board.numEither(point));
			}
		}
		return position + " off b" + board.getBlackBore() + " r" + board.getRedBore();
	}

	/**
	 * A position that could come up in a game. Near the end, most pieces are home or off, with the odd one left behind
	 * or on the bar, which is where bearing off's rules matter
	 */
	private static Board randomPosition(final Random random, final boolean nearTheEnd) {
		final Board board = TestBoards.empty();
		place(board, true, random, nearTheEnd);
		place(board, false, random, nearTheEnd);
		return board;
	}

	private static void place(final Board board, final boolean black, final Random random, final boolean nearTheEnd) {
		final int off = nearTheEnd ? random.nextInt(PIECES) : random.nextInt(3);
		if (black) {
			board.setBlackBore(off);
		} else {
			board.setRedBore(off);
		}
		for (int piece = off; piece < PIECES; piece++) {
			// in the player's own numbering: home is 1 to 6 and the bar 25
			int point;
			do {
				point = nearTheEnd && random.nextInt(12) != 0 ? 1 + random.nextInt(6) : 1 + random.nextInt(25);
			} while (board.getCount(onBoard(point, black), !black) > 0);
			board.addPiece(onBoard(point, black), black);
		}
	}

	private static int onBoard(final int point, final boolean black) {
		return black ? point : 25 - point;
	}

	/**
	 * Backgammon's rules, written separately from the game's and as plainly as possible, in the player's own numbering:
	 * they move down, their home is 1 to 6, their bar 25, and the opponent's bar 0
	 */
	private static final class Rules {

		private final int[] mine = new int[Board.POINTS];
		private final int[] theirs = new int[Board.POINTS];
		private int off;
		private final int theirOff;
		private final boolean black;

		private Rules(final Board board, final boolean black) {
			this.black = black;
			for (int point = 0; point < Board.POINTS; point++) {
				mine[point] = board.getCount(onBoard(point, black), black);
				theirs[point] = board.getCount(onBoard(point, black), !black);
			}
			off = black ? board.getBlackBore() : board.getRedBore();
			theirOff = black ? board.getRedBore() : board.getBlackBore();
		}

		private Rules(final Rules other) {
			black = other.black;
			System.arraycopy(other.mine, 0, mine, 0, Board.POINTS);
			System.arraycopy(other.theirs, 0, theirs, 0, Board.POINTS);
			off = other.off;
			theirOff = other.theirOff;
		}

		/**
		 * @return the position after moving this far from this point, or null if that isn't allowed
		 */
		private Rules move(final int from, final int die) {
			if (mine[from] == 0 || mine[25] > 0 && from != 25) {
				return null;
			}
			final int to = from - die;
			if (to >= 1 && theirs[to] >= 2) {
				return null;
			}
			if (to <= 0 && !mayBearOff(from, to)) {
				return null;
			}
			final Rules after = new Rules(this);
			after.mine[from]--;
			if (to <= 0) {
				after.off++;
			} else {
				after.mine[to]++;
				if (theirs[to] == 1) {
					after.theirs[to] = 0;
					after.theirs[0]++;
				}
			}
			return after;
		}

		private boolean mayBearOff(final int from, final int to) {
			for (int point = 7; point <= 25; point++) {
				if (mine[point] > 0) {
					return false;
				}
			}
			for (int point = from + 1; to < 0 && point <= 6; point++) {
				if (mine[point] > 0) {
					return false;
				}
			}
			return true;
		}

		private Board toBoard() {
			final Board board = TestBoards.empty();
			for (int point = 0; point < Board.POINTS; point++) {
				if (black) {
					board.setBlackCount(point, mine[point]);
					board.setRedCount(point, theirs[point]);
				} else {
					board.setRedCount(25 - point, mine[point]);
					board.setBlackCount(25 - point, theirs[point]);
				}
			}
			board.setBlackBore(black ? off : theirOff);
			board.setRedBore(black ? theirOff : off);
			return board;
		}

		/**
		 * Every way of playing the dice, in any order, however few of them it uses, as the board and the dice played
		 */
		private void playOn(final List<Integer> left, final List<Integer> played, final List<Finish> finishes) {
			finishes.add(new Finish(toBoard(), played));
			for (int i = 0; i < left.size(); i++) {
				// a double's dice are all the same, so one of them will do
				if (left.subList(0, i).contains(left.get(i))) {
					continue;
				}
				final List<Integer> rest = new ArrayList<>(left);
				final int die = rest.remove(i);
				final List<Integer> playedNow = new ArrayList<>(played);
				playedNow.add(die);
				for (int from = 1; from <= 25; from++) {
					final Rules after = move(from, die);
					if (after != null) {
						after.playOn(rest, playedNow, finishes);
					}
				}
			}
		}

		/**
		 * The boards the rules let the turn end on: as many dice as can be used, and if that's only one of two different
		 * dice, the larger if it can be played
		 */
		static Set<Board> finishingBoards(final Board board, final boolean black, final List<Integer> roll) {
			final List<Finish> finishes = new ArrayList<>();
			new Rules(board, black).playOn(roll, List.of(), finishes);

			int most = 0;
			for (final Finish finish : finishes) {
				most = Math.max(most, finish.played().size());
			}
			final Set<Board> boards = new HashSet<>();
			if (most == 0) {
				return boards;
			}
			int required = 0;
			if (most == 1 && roll.size() == 2 && !roll.get(0).equals(roll.get(1))) {
				for (final Finish finish : finishes) {
					if (finish.played().size() == 1) {
						required = Math.max(required, finish.played().get(0));
					}
				}
			}
			for (final Finish finish : finishes) {
				if (finish.played().size() == most && (required == 0 || finish.played().get(0) == required)) {
					boards.add(finish.board());
				}
			}
			return boards;
		}
	}

	private record Finish(Board board, List<Integer> played) {
	}
}
