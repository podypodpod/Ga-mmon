package uk.co.davidlomas.gammon.game;

import java.util.List;

/**
 * A player who chooses moves by rules of their own rather than by the evolved genes, such as the fixed opponents the
 * evolved players are measured against
 */
interface ChoosesOwnMoves {

	/**
	 * @param boards the boards the legal moves lead to
	 * @param black  the player choosing
	 * @param before the board before the move
	 *
	 * @return the board to play, or null if there are no moves
	 */
	Board chooseMove(List<Board> boards, boolean black, Board before);
}
