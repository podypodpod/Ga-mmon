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

import java.util.Scanner;

import backgammon.settings.GameSettings;

/**
 * The Class Player. the base class for players and contains code that only
 * covers human players.
 * 
 * two players will always be made, the AI will extend this to use AIPlayers
 * though
 *
 * @author David Lomas - 11035527
 */
public class Player {

	/** The moves left. */
	public MovesLeft movesLeft;

	/** The players color */
	Boolean black;

	Die die1, die2;
	DiceList dice;

	boolean turnOver;

	/**
	 * Instantiates a new player.
	 *
	 * @param b
	 *            the b
	 */
	public Player(final boolean b) {

		black = b;

		movesLeft = new MovesLeft();

		dice = new DiceList();

		die1 = new Die();
		die2 = new Die();
	}

	/**
	 * Returns true if the move suggested is a legal move
	 */
	public boolean candidateMovePossible(final int from, final int to, final Board liveBoard) {

		if (!passesBasicChecks(to, from, liveBoard)) {
			return false;
		}

		// TO piece, -1 or 26 = bear
		if (!movingToZeroPeices(to, from)) {

			// looping through the moves Left array to check against
			// what they have asked for
			boolean validLength = false;
			for (final int x : movesLeft.movesLeft) {
				if (x == distanceBetween(from, to)) {
					validLength = true;
					break;
				}
			}

			if (validLength) {
				if (validDestination(to, from, liveBoard)) {
					// DONE CHECKING
					return true;
				}
			}

			// BEARING, bearing is counted as point -1 or 26
		} else if (tryingAndCanBear(to, from, liveBoard)) {

			if (theyHaveThatMoveLeft(to, from)) {
				return true;
			}
		}
		// move is not possible
		return false;
	}

	/**
	 * returns the distance between the two points
	 */
	public int distanceBetween(final int a, final int b) {
		if (a > b) {
			return a - b;
		} else {
			return b - a;
		}
	}

	/**
	 * returns true if start position has a piece on it
	 */
	public boolean hasAPeiceAtStart(final int from, final Board liveBoard) {
		if (liveBoard.Points[from].numEither() > 0 && liveBoard.Points[from].getCol() == black) {
			return true;
		}
		return false;
	}

	/**
	 * Move piece.
	 */
	public void movePiece(final int from, final int to, final Board liveBoard) {

		// if there is an opposing piece there
		if (to != -1 && to != 26) {
			if (liveBoard.Points[to].getCol() != black && liveBoard.Points[to].numEither() == 1) {

				liveBoard.Points[from].removePiece(black);
				liveBoard.Points[to].removePiece(!black);
				liveBoard.Points[to].addPiece(black);

				if (black) {
					liveBoard.Points[0].addRedPiece();
				} else {
					liveBoard.Points[25].addBlackPiece();
				}

				// if its empty
			} else {

				liveBoard.Points[from].removePiece(black);
				liveBoard.Points[to].addPiece(black);
			}
			// BEARING THE PEICE OFF
		} else if (to == -1 || to == 26) {
			liveBoard.Points[from].removePiece(black);
			liveBoard.addToBear(black);
		}
	}

	/**
	 * returns true if the piece is moving in the right direction
	 */
	public boolean movingInTheRightDirection(final int to, final int from) {
		if (black && to < from || !black && to > from) {
			return true;
		}
		return false;
	}

	/**
	 * returns true if not moving to a zero space
	 */
	public boolean movingToZeroPeices(final int to, final int from) {
		if (to != 0 && to != 25 && to != -1 && to != 26) {
			return false;
		}
		return true;
	}

	/**
	 * returns true if they are not moving the zero piece which needs to be
	 * moved (only legal move)
	 */
	public boolean notMovingTheZero(final int from) {
		if (black && from != 25 || !black && from != 0) {
			return true;
		}
		return false;
	}

	/**
	 * returns true if it passes basic checks of backgammon rules
	 */
	public boolean passesBasicChecks(final int to, final int from, final Board liveBoard) {

		// FROM piece
		if (!withinArrayBounds(to, from)) {
			return false;
		}

		if (!hasAPeiceAtStart(from, liveBoard)) {
			return false;
		}

		if (!movingInTheRightDirection(to, from)) {
			return false;
		}

		if (liveBoard.isthereZero(black)) {
			if (notMovingTheZero(from)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * returns true if the distance between the two points matches the moves
	 * left they have
	 */
	public boolean theyHaveThatMoveLeft(final int to, final int from) {
		int y;

		if (to == -1) {
			y = 1;
		} else {
			y = -1;
		}

		// looping through the moves Left array to check against what they have
		// asked for
		for (final int x : movesLeft.movesLeft) {
			if (x >= distanceBetween(from, to + y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * returns true if the player is trying to bear and he can bear at this time
	 */
	public boolean tryingAndCanBear(final int to, final int from, final Board liveBoard) {
		if ((to == -1 || to == 26) && liveBoard.canPlayerBear(liveBoard.Points[from].getCol())) {
			return true;
		}
		return false;
	}

	/**
	 * The players turn.
	 *
	 * @param liveBoard
	 *            the live board
	 */
	public void turn(final Board liveBoard) {

		turnOver = false;

		if (GameSettings.getDisplayConsole()) {
			System.out.println("------------Your Turn!-----------------");
		}

		// Rolling the dice
		movesLeft.setTo(dice.RollDice());

		System.out.println("Player has : " + movesLeft.toString());

		// ASKING WHAT TO DO
		while (!turnOver) {

			if (GameSettings.getDisplayConsole()) {
				System.out.println("What do you want to do?, " + movesLeft.size() + " moves left");

				System.out.println("1) Move a piece");
				System.out.println("2) Bear off a piece");
				System.out.println("3) Skip or Finish go");
				System.out.println("4) Concede");
			}

			@SuppressWarnings("resource")
			final Scanner Scanner = new Scanner(System.in);

			final int option = Scanner.nextInt();

			// MOVE A piece
			if (option == 1) {
				System.out.println("from which point (1,2,3 etc): ");
				final int from = Scanner.nextInt();
				System.out.println("to which point (1,2,3,bear=-1 etc)");
				final int to = Scanner.nextInt();

				// IF ITS POSSIBLE
				if (candidateMovePossible(from, to, liveBoard)) {
					// MOVE THE PIECE
					movePiece(from, to, liveBoard);
					// REMOVE THE "MOVE"
					movesLeft.remove(Integer.valueOf(distanceBetween(from, to)));

					if (movesLeft.size() == 0) {
						turnOver = true;
					}
				} else {
					System.out.println("invalid move");
				}
				// SKIP GO
			} else if (option == 2) {
				if (liveBoard.canPlayerBear(black)) {

					// CHECK ITS A VALID MOVE/LENGTH

					System.out.println("Bear which piece?");

					final int bearPeice = Scanner.nextInt();
					if (candidateMovePossible(bearPeice, -1, liveBoard)) {
						liveBoard.bearPiece(bearPeice, black);
					}
				} else {
					System.out.println("Can not bear pieces yet");
				}
			} else if (option == 3) {
				turnOver = true;
				// CONCEEDING
			} else if (option == 4) {
				turnOver = true;

			}
		}
	}

	/**
	 * returns true if destination isnt filled or no more than 1 chip
	 */
	public boolean validDestination(final int to, final int from, final Board liveBoard) {
		if (liveBoard.Points[to].getCol() == black
				|| liveBoard.Points[to].getCol() != black && liveBoard.Points[to].numEither() <= 1) {
			return true;
		}
		return false;
	}

	/**
	 * returns if its within set array bounds
	 */
	public boolean withinArrayBounds(final int to, final int from) {
		if (from >= 0 && from <= 25 && to <= 26 && to >= -1) {
			return true;
		}
		return false;
	}
}
