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

package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.settings.GameSettings;

/**
 * The Class BoardEvaluator.
 *
 * Useful methods for picking boards from the board list e.g. pick a board where
 * it will take a piece
 *
 * Allows the AI to loop through all the possible moves and pick a certain one
 *
 * @author David Lomas - 11035527
 */
public class BoardEvaluator {

	/** The live board. */
	private Board liveBoard;

	/** The current player. */
	public AIPlayer currentPlayer;

	/** The opponent zero positions. */
	private int myZero, oponentZero;

	/**
	 * getMyZero
	 *
	 * @return the value of myZero
	 */
	public int getMyZero() {
		return myZero;
	}

	/**
	 * hasABlotBeenDoubled
	 *
	 * Checks to see if the board passed in has had a single piece on its own (a
	 * blot) doubled and therefore safe from being taken
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if it has been doubled
	 */
	public boolean hasABlotBeenDoubled(final Board newBoard) {
		if (liveBoard.getNumOfBlots(currentPlayer.black) > newBoard.getNumOfBlots(currentPlayer.black)) {
			if (GameSettings.getDisplayConsole()) {
				System.out.println("Blot doubled");
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks for a piece that has been bore.
	 *
	 * @param newBoard
	 *            the new board
	 * @return true, if successful
	 */
	public boolean hasAPeiceBeenBore(final Board newBoard) {
		if (liveBoard.howManyHasPlayerBore(currentPlayer.black) < newBoard.howManyHasPlayerBore(currentPlayer.black)) {
			if (GameSettings.getDisplayConsole()) {
				System.out.println("Peice has been BORE");
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks for a piece being taken.
	 *
	 * @param newBoard
	 *            the new board
	 * @return true, if successful
	 */
	public boolean hasAPeiceBeenTaken(final Board newBoard) {
		if (liveBoard.Points[oponentZero].numEither() < newBoard.Points[oponentZero].numEither()) {
			if (GameSettings.getDisplayConsole()) {
				System.out.println("Peice has been taken.");
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * hasAPieceBeenMovedSolo
	 *
	 * Checks to see if the board passed in has had a single piece on its own (a
	 * blot) created
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if it has been doubled
	 */
	public boolean hasAPieceBeenMovedSolo(final Board newBoard) {
		if (liveBoard.getNumOfBlots(currentPlayer.black) < newBoard.getNumOfBlots(currentPlayer.black)) {
			if (GameSettings.getDisplayConsole()) {
				System.out.println("Piece moved solo");
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * hasAPieceBeenSpread
	 *
	 * checks to see if the board passed in has moved a piece to another spot on
	 * the home board when bearing is applicable
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if it has been spread
	 */
	public boolean hasAPieceBeenSpread(final Board newBoard) {
		if (liveBoard.getNumOfHomePointsCovered(currentPlayer.black) < newBoard
				.getNumOfHomePointsCovered(currentPlayer.black)) {
			if (GameSettings.getDisplayConsole()) {
				System.out.println("piece spread");
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * hasAStackBeenAddedTo
	 *
	 * checks to see if the board passed in has moved a piece on top of an
	 * existing stack
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if it has been blocked
	 */
	public boolean hasAStackBeenAddedTo(final Board newBoard) {
		if (liveBoard.getNumberOfCheckersOnStacks(currentPlayer.black) < newBoard
				.getNumberOfCheckersOnStacks(currentPlayer.black)) {
			if (GameSettings.getDisplayConsole()) {
				System.out.println("added to stack");
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * hasTheOponentBeenBlocked
	 *
	 * checks to see if the board passed in has moved a piece to block the
	 * oponent
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if it has been blocked
	 */
	public boolean hasTheOponentBeenBlocked(final Board newBoard) {
		if (liveBoard.getNumberOfPiecesSurroundOpponent(currentPlayer.black) < newBoard
				.getNumberOfPiecesSurroundOpponent(currentPlayer.black)) {
			if (GameSettings.getDisplayConsole()) {
				System.out.println("blocking oponent");
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean ifPointHasPeices(final Board newBoard, final int point, final int num) {
		if (newBoard.Points[point].numEither() == num) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * isFiveFourInitialMoveAgr
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveFourInitialMoveAgr(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 13, 3) && ifPointHasPeices(newBoard, 9, 1)
					&& ifPointHasPeices(newBoard, 8, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 12, 3) && ifPointHasPeices(newBoard, 16, 1)
					&& ifPointHasPeices(newBoard, 17, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFiveFourInitialMoveBal
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveFourInitialMoveBal(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 20, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 8, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 5, 1)
					&& ifPointHasPeices(newBoard, 12, 4) && ifPointHasPeices(newBoard, 17, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFiveOneInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveOneInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 23, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 8, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMove");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 2, 1)
					&& ifPointHasPeices(newBoard, 12, 4) && ifPointHasPeices(newBoard, 17, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMove");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFiveOneInitialMoveAlt
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveOneInitialMoveAlt(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 6, 4) && ifPointHasPeices(newBoard, 5, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 8, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 19, 4) && ifPointHasPeices(newBoard, 20, 1)
					&& ifPointHasPeices(newBoard, 12, 4) && ifPointHasPeices(newBoard, 17, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFiveThreeInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveThreeInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 8, 2) && ifPointHasPeices(newBoard, 6, 4)
					&& ifPointHasPeices(newBoard, 3, 2)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 17, 2) && ifPointHasPeices(newBoard, 19, 4)
					&& ifPointHasPeices(newBoard, 22, 2)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFiveTwoInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveTwoInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 11, 1) && ifPointHasPeices(newBoard, 13, 3)
					&& ifPointHasPeices(newBoard, 8, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 14, 1) && ifPointHasPeices(newBoard, 12, 3)
					&& ifPointHasPeices(newBoard, 17, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFiveTwoInitialMoveRisk
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveTwoInitialMoveRisk(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 22, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 8, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red player
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 3, 1)
					&& ifPointHasPeices(newBoard, 12, 4) && ifPointHasPeices(newBoard, 17, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFourTwoInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFourOneInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 23, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 9, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFourOneInitialMove");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 2, 1)
					&& ifPointHasPeices(newBoard, 12, 4) && ifPointHasPeices(newBoard, 16, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFourOneInitialMove");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFourTwoInitialMoveAlt
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFourOneInitialMoveAlt(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 6, 4) && ifPointHasPeices(newBoard, 5, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 9, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFourOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 19, 4) && ifPointHasPeices(newBoard, 20, 1)
					&& ifPointHasPeices(newBoard, 12, 4) && ifPointHasPeices(newBoard, 16, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFourOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFourThreeInitialMoveBlock
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFourThreeInitialMoveBlock(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 13, 3) && ifPointHasPeices(newBoard, 10, 1)
					&& ifPointHasPeices(newBoard, 9, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFourThreeInitialMoveBlock");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 12, 3) && ifPointHasPeices(newBoard, 15, 1)
					&& ifPointHasPeices(newBoard, 16, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFourThreeInitialMoveBlock");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFourThreeInitialMoveSplit
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFourThreeInitialMoveSplit(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 21, 1)
					&& ifPointHasPeices(newBoard, 6, 4) && ifPointHasPeices(newBoard, 9, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFourThreeInitialMoveSplit");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 4, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 16, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFourThreeInitialMoveSplit");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFourTwoInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFourTwoInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 8, 2) && ifPointHasPeices(newBoard, 4, 2)
					&& ifPointHasPeices(newBoard, 6, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFourTwoInitialMove");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 17, 2) && ifPointHasPeices(newBoard, 21, 2)
					&& ifPointHasPeices(newBoard, 19, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFourTwoInitialMove");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isSixFiveInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isSixFiveInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 13, 6)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 12, 6)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isSixFourInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isSixFourInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 14, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 11, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isSixFourInitialMoveSplit
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isSixFourInitialMoveSplit(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 18, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 9, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 7, 1)
					&& ifPointHasPeices(newBoard, 12, 4) && ifPointHasPeices(newBoard, 16, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isSixOneInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isSixOneInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 7, 2) && ifPointHasPeices(newBoard, 8, 2)
					&& ifPointHasPeices(newBoard, 13, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 17, 2) && ifPointHasPeices(newBoard, 18, 2)
					&& ifPointHasPeices(newBoard, 12, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isSixTwoInitialMoveAgr
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isSixThreeInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 15, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 10, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isSixThreeInitialMoveSplit
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isSixThreeInitialMoveSplit(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 18, 1)
					&& ifPointHasPeices(newBoard, 10, 1) && ifPointHasPeices(newBoard, 13, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 7, 1)
					&& ifPointHasPeices(newBoard, 15, 1) && ifPointHasPeices(newBoard, 12, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isSixTwoInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isSixTwoInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 18, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 1, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 1, 1) && ifPointHasPeices(newBoard, 7, 1)
					&& ifPointHasPeices(newBoard, 12, 4) && ifPointHasPeices(newBoard, 14, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isSixTwoInitialMoveAgr
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isSixTwoInitialMoveAgr(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 5, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 12, 4) && ifPointHasPeices(newBoard, 20, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isFiveOneInitialMoveAlt");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isThreeOneInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isThreeOneInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 6, 4) && ifPointHasPeices(newBoard, 5, 2)
					&& ifPointHasPeices(newBoard, 8, 2)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isThreeOneInitialMove");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 19, 4) && ifPointHasPeices(newBoard, 20, 2)
					&& ifPointHasPeices(newBoard, 17, 2)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isThreeOneInitialMove");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isThreeTwoOffenceInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isThreeTwoOffenceInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 13, 3) && ifPointHasPeices(newBoard, 11, 1)
					&& ifPointHasPeices(newBoard, 10, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isThreeTwoOffenceInitialMove");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 12, 3) && ifPointHasPeices(newBoard, 14, 1)
					&& ifPointHasPeices(newBoard, 15, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isThreeTwoOffenceInitialMove");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isThreeTwoSplitInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isThreeTwoSplitInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 4, 1) && ifPointHasPeices(newBoard, 1, 1)
					&& ifPointHasPeices(newBoard, 12, 4) && ifPointHasPeices(newBoard, 14, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isThreeTwoSplitInitialMove");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 21, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 11, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isThreeTwoSplitInitialMove");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isTwoOneSlotPlayInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isTwoOneSlotPlayInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 6, 4) && ifPointHasPeices(newBoard, 5, 1)
					&& ifPointHasPeices(newBoard, 14, 1) && ifPointHasPeices(newBoard, 12, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isTwoOneSlotPlayInitialMove");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 19, 4) && ifPointHasPeices(newBoard, 20, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 11, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isTwoOneSlotPlayInitialMove");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * isTwoOneSplitPlayInitialMove
	 *
	 * checks to see if the board passed in has used this particular initial
	 * move (move taken from http://www.bkgm.com/openings.html#opening21)
	 *
	 * @param newBoard
	 *            the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isTwoOneSplitPlayInitialMove(final Board newBoard) {
		// black
		if (currentPlayer.black) {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 2, 1) && ifPointHasPeices(newBoard, 1, 1)
					&& ifPointHasPeices(newBoard, 14, 1) && ifPointHasPeices(newBoard, 12, 4)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isTwoOneSplitPlayInitialMove");
				}
				return true;
			}
			// red
		} else {
			// if it has performed the move
			if (ifPointHasPeices(newBoard, 24, 1) && ifPointHasPeices(newBoard, 23, 1)
					&& ifPointHasPeices(newBoard, 13, 4) && ifPointHasPeices(newBoard, 11, 1)) {
				if (GameSettings.getDisplayConsole()) {
					System.out.println("isTwoOneSplitPlayInitialMove");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the board.
	 *
	 * @param cb
	 *            the new board
	 */
	public void setBoard(final Board cb) {
		liveBoard = cb;
	}

	/**
	 * setMyZero
	 *
	 * @param myZero
	 */
	public void setMyZero(final int myZero) {
		this.myZero = myZero;
	}

	/**
	 * Sets the player.
	 *
	 * @param p
	 *            the new player
	 */
	public void setPlayer(final AIPlayer p) {
		currentPlayer = p;
		if (p.black) {
			setMyZero(25);
			oponentZero = 0;
		} else {
			setMyZero(0);
			oponentZero = 25;
		}
	}
}