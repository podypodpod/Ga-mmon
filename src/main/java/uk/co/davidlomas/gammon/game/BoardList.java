/*
  GNU General Public License

  This file is part of GA-mmon.

  GA-mmon is free software: you can redistribute it and/or modify it under the
  terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
  A PARTICULAR PURPOSE. See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with
  GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.game;

import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The Class BoardList.
 * <p>
 * Holds a collection of board states, the boards a player's legal moves lead to, for MoveChooser to pick from
 *
 * @author David Lomas
 */
public class BoardList {

	/**
	 * The all possible moves.
	 */
	private final List<Board> boardList;

	/**
	 * The same boards as boardList, so checking for a duplicate is a quick lookup instead of a scan of the list,
	 * with the dice each one used to get there. A board can be reached part way through a turn, after only some of
	 * the dice, and keepOnlyLegalMoves uses these to drop those.
	 */
	private final Map<Board, List<Integer>> boardsInList;

	// how many dice the boards use, once keepOnlyLegalMoves has run
	private int mostDiceUsed;

	// the boards, with the dice left, that the moves on from them have already been worked out for
	private final Set<Position> triedFrom = new HashSet<>();

	/**
	 * A board part way through a turn and the dice still to play, in any order
	 */
	private static final class Position {
		private final Board board;
		private final List<Integer> diceLeft;

		Position(final Board board, final List<Integer> diceLeft) {
			this.board = board;
			this.diceLeft = new ArrayList<>(diceLeft);
			this.diceLeft.removeIf(die -> die == 0);
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
	 * BoardList
	 * <p>
	 * Instantiates a new board list.
	 */
	public BoardList() {
		boardList = new ArrayList<>();
		boardsInList = new HashMap<>();
	}

	/**
	 * addBoardWithNextMove Adds the board passed in, for tests that build the list by hand. It isn't known which dice
	 * reached it, so it's treated as using none, and keepOnlyLegalMoves would drop it next to generated boards.
	 *
	 * @param b the board
	 */
	void addBoardWithNextMove(final Board b) {
		addBoard(b, Collections.emptyList());
	}

	private void addBoard(final Board b, final List<Integer> diceUsed) {
		boardList.add(b);
		boardsInList.put(b, diceUsed);
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

		if (player.movesLeft.moves.size() < 1) {
			throw new MalformedParametersException("Player has no moves left");

		} else {
			// move the piece on this new board
			player.movePiece(from, to, board);
			player.diceUsed.add(player.distanceBetween(from, to));

			if (player.movesLeft.size() > 1) {
				player.movesLeft.moves.remove(Integer.valueOf(player.distanceBetween(from, to)));
			} else {
				player.movesLeft.moves.remove(0);
				player.movesLeft.moves.add(0);
			}

			// check if any further moves can be made. The same board with the same dice left leads on to the same
			// boards as the last time it was reached, often in a different order of moves, so it's only tried once
			if (!(player.movesLeft.size() == 1 && player.movesLeft.getNext() == 0 || player.movesLeft.size() == 0)
					&& triedFrom.add(new Position(board, player.movesLeft.moves))) {
				moveGenerator.generateMoves(board, player);
			}

			// add the board to the list if there is not a duplicate. If there is, keep whichever way of reaching it
			// used more dice. The dice are only copied when they're kept, as many boards reached are duplicates
			if (!thereIsADuplicate(board)) {
				addBoard(board, new ArrayList<>(player.diceUsed));
			} else if (boardsInList.get(board).size() < player.diceUsed.size()) {
				boardsInList.put(board, new ArrayList<>(player.diceUsed));
			}

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
		for (final Board board : boardList) {
			most = Math.max(most, boardsInList.get(board).size());
		}
		mostDiceUsed = most;
		removeBoardsWhere(board -> boardsInList.get(board).size() < mostDiceUsed);

		if (mostDiceUsed == 1 && roll.size() == 2 && !roll.get(0).equals(roll.get(1))) {
			final int larger = Math.max(roll.get(0), roll.get(1));
			final boolean largerCanBePlayed =
					boardList.stream().anyMatch(board -> boardsInList.get(board).get(0) == larger);
			if (largerCanBePlayed) {
				removeBoardsWhere(board -> boardsInList.get(board).get(0) != larger);
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
	 * @return the boards, for quick lookups, read only
	 */
	Set<Board> boardSet() {
		return Collections.unmodifiableSet(boardsInList.keySet());
	}

	private void removeBoardsWhere(final Predicate<Board> unwanted) {
		boardList.removeIf(board -> {
			if (unwanted.test(board)) {
				boardsInList.remove(board);
				return true;
			}
			return false;
		});
	}

	/**
	 * clearList
	 * <p>
	 * Empties the list.
	 */

	void clearList() {
		boardList.clear();
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
		return boardList.size() > 0;
	}

	/**
	 * Gets the boards, so tests can see which moves were on offer.
	 * <p>
	 * Nothing in the game calls this, but DecisionGenesTest does, so don't remove it as unused.
	 *
	 * @return the boards, read only
	 */
	List<Board> getBoards() {
		return Collections.unmodifiableList(boardList);
	}

	/**
	 * There is a duplicate. checks if there is already a duplicate of the board
	 * passed in already present in the list
	 *
	 * @param boardInQuestion the board it is checking against
	 *
	 * @return true, if there is a duplicate
	 */
	private boolean thereIsADuplicate(final Board boardInQuestion) {
		return boardsInList.containsKey(boardInQuestion);
	}
}