package uk.co.davidlomas.gammon.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Class BoardList.
 * <p>
 * Holds a collection of board states, the boards a player's legal moves lead to, for MoveChooser to pick from
 *
 * @author David Lomas
 */
public class BoardList {

	/**
	 * The boards, in the order they were found, so checking for a duplicate is a quick lookup, with the dice each one
	 * used to get there. A board can be reached part way through a turn, after only some of the dice, and
	 * keepOnlyLegalMoves uses these to drop those.
	 */
	private final Map<HashedBoard, List<Integer>> boardsInList = new LinkedHashMap<>();

	// how many dice the boards use, once keepOnlyLegalMoves has run
	private int mostDiceUsed;

	// the boards, with the dice left, that the moves on from them have already been worked out for
	private final Set<Position> triedFrom = new HashSet<>();

	/**
	 * A board and its hash. A board is looked up three times as moves are tried, and hashing it means going through
	 * every point, so the hash is worked out once. The board must not change while it's in the list. Using the boards
	 * as keys directly made the benchmark's games about 8% slower.
	 */
	private static final class HashedBoard {
		private final Board board;
		private final int hash;

		HashedBoard(final Board board) {
			this.board = board;
			hash = board.hashCode();
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof HashedBoard && hash == ((HashedBoard) o).hash && board.equals(((HashedBoard) o).board);
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	/**
	 * A board part way through a turn and the dice still to play, in any order
	 */
	private static final class Position {
		private final HashedBoard board;
		private final List<Integer> diceLeft;

		Position(final HashedBoard board, final List<Integer> diceLeft) {
			this.board = board;
			this.diceLeft = new ArrayList<>(diceLeft);
			Collections.sort(this.diceLeft);
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof Position && board.equals(((Position) o).board)
					&& diceLeft.equals(((Position) o).diceLeft);
		}

		@Override
		public int hashCode() {
			return 31 * board.hashCode() + diceLeft.hashCode();
		}
	}

	/**
	 * addBoardWithNextMove Adds the board passed in, for tests that build the list by hand. It isn't known which dice
	 * reached it, so it's treated as using none, and keepOnlyLegalMoves would drop it next to generated boards.
	 *
	 * @param b the board
	 */
	void addBoardWithNextMove(final Board b) {
		boardsInList.put(new HashedBoard(b), Collections.emptyList());
	}

	/**
	 * addBoardWithNextMove adds the board passed in with the move also passed in applied to
	 * it first. It then checks if any more moves can be made
	 *
	 * @param board         the board passed in
	 * @param to            the destination of move
	 * @param from          the start of the move
	 * @param player        the player object
	 * @param moveGenerator the move generator
	 */
	public void addBoardWithNextMove(final Board board, final int to, final int from, final AiPlayer player, final MoveGenerator moveGenerator) {

		if (player.movesLeft.size() == 0) {
			throw new IllegalStateException("Player has no moves left");
		}

		// move the piece on this new board
		player.movePiece(from, to, board);
		final int die = player.distanceBetween(from, to);
		player.diceUsed.add(die);
		player.movesLeft.remove(die);

		// check if any further moves can be made. The same board with the same dice left leads on to the same
		// boards as the last time it was reached, often in a different order of moves, so it's only tried once
		final HashedBoard key = new HashedBoard(board);
		if (player.movesLeft.size() > 0 && triedFrom.add(new Position(key, player.movesLeft.moves))) {
			moveGenerator.generateMoves(board, player);
		}

		// add the board to the list if there is not a duplicate. If there is, keep whichever way of reaching it
		// used more dice. The dice are only copied when they're kept, as many boards reached are duplicates
		final List<Integer> diceBefore = boardsInList.get(key);
		if (diceBefore == null || diceBefore.size() < player.diceUsed.size()) {
			boardsInList.put(key, new ArrayList<>(player.diceUsed));
		}
	}

	/**
	 * Drops the boards the rules don't allow. A player has to use as many of the dice as they can, and when only one
	 * of two different dice can be used, it has to be the larger one if that can be played.
	 * <p>
	 * The list holds every board reached part way through a turn too, so without this the AI could stop after one
	 * die when both could be played.
	 *
	 * @param roll the dice rolled this turn
	 */
	void keepOnlyLegalMoves(final List<Integer> roll) {
		int most = 0;
		for (final List<Integer> diceUsed : boardsInList.values()) {
			most = Math.max(most, diceUsed.size());
		}
		mostDiceUsed = most;
		boardsInList.values().removeIf(diceUsed -> diceUsed.size() < mostDiceUsed);

		if (mostDiceUsed == 1 && roll.size() == 2 && !roll.get(0).equals(roll.get(1))) {
			final int larger = Math.max(roll.get(0), roll.get(1));
			final boolean largerCanBePlayed =
					boardsInList.values().stream().anyMatch(diceUsed -> diceUsed.get(0) == larger);
			if (largerCanBePlayed) {
				boardsInList.values().removeIf(diceUsed -> diceUsed.get(0) != larger);
			}
		}
	}

	/**
	 * @return how many dice the boards left by keepOnlyLegalMoves use
	 */
	int mostDiceUsed() {
		return mostDiceUsed;
	}

	/**
	 * @return the boards, for quick lookups
	 */
	Set<Board> boardSet() {
		return new HashSet<>(getBoards());
	}

	/**
	 * clearList
	 * <p>
	 * Empties the list.
	 */
	void clearList() {
		boardsInList.clear();
		triedFrom.clear();
		mostDiceUsed = 0;
	}

	/**
	 * Checks for contents.
	 *
	 * @return true, if successful
	 */
	public boolean hasContents() {
		return !boardsInList.isEmpty();
	}

	/**
	 * Gets the boards, for MoveChooser to pick from and for tests to see which moves were on offer.
	 * <p>
	 * The boards themselves mustn't be changed: the list keeps the hash each one had when it was added, so a changed
	 * board would be looked up in the wrong place. Copy one to play on from it, as Game does with the board chosen.
	 *
	 * @return the boards, in the order they were found, read only
	 */
	List<Board> getBoards() {
		final List<Board> boards = new ArrayList<>(boardsInList.size());
		for (final HashedBoard key : boardsInList.keySet()) {
			boards.add(key.board);
		}
		return Collections.unmodifiableList(boards);
	}
}