/**
 * GNU General Public License
 *
 * This file is part of GA-mmon.
 * 
 * GA-mmon is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package backgammon.game;

import backgammon.genes.Individual;

/**
 * The Class MoveGenerator.
 * 
 * generates all possible moves for the player at the current board state
 *
 * adds them all to the board list collection
 *
 * @author David Lomas - 11035527
 */
public class MoveGenerator {

	/** The board list. */
	BoardList boardList;

	Individual individual;

	/**
	 * Instantiates a new move generator.
	 */
	public MoveGenerator(final Individual indiv) {
		individual = indiv;
		boardList = new BoardList(individual);
	}

	/**
	 * Generate all possible moves.
	 *
	 * @param cb
	 *            the current board
	 * @param p
	 *            the player
	 */
	public void generateMoves(final Board cb, final AIPlayer p) {

		// Loop all moves
		for (final int currentMove : p.movesLeft.movesLeft) {

			// check the current move doesn't = 0, this would mean it has been
			// removed and a new board has been created
			if (currentMove != 0) {

				// loop all points
				for (int point = 0; point < cb.Points.length; point++) {

					// if the player is black
					if (p.black) {

						// if the current point can move the current move,
						// create new board of it
						if (p.candidateMovePossible(point, point - currentMove, cb)) {

							// adding the new board to the list
							boardList.addBoard(new Board(cb), point - currentMove, point, new AIPlayer(p), this);

						}
						// if the player is red
					} else if (!p.black) {

						// if the current point can move the current move,
						// create new board of it
						if (p.candidateMovePossible(point, point + currentMove, cb)) {

							// adding the new board to the list
							boardList.addBoard(new Board(cb), point + currentMove, point, new AIPlayer(p), this);
						}
					}
				}
			} else {
				break;
			}
		}
	}

	/**
	 * Gets the next move board.
	 *
	 * @param cb
	 *            the current board
	 * @param p
	 *            the player
	 * @return the next move board
	 */
	public Board getNextMoveBoard(final Board cb, final AIPlayer p) {

		// Clear the list
		boardList.clearList();

		// generate all possible moves
		generateMoves(cb, p);

		// select the best board and return it, if no possible next moves it
		// will return null and carry on using the same board
		return boardList.selectBoard(cb, p);
	}
}