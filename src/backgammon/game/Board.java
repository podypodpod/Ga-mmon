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

import backgammon.settings.GameSettings;

/**
 * The Class Board.
 *
 * Essentially the board state class
 *
 * Holds all the data on the board, where all the peices are, if the player is
 * in a position to move etc
 */
public class Board {

	private int redBore, blackBore;

	public Point[] Points;

	public boolean isInitialMove = false;

	public Board() {
		Points = new Point[26];

		for (int x = 0; x < Points.length; x++) {
			Points[x] = new Point();
		}

		setStartPosition();

		redBore = 0;
		blackBore = 0;
	}

	public Board(final Board copy) {
		// 26 new points
		Points = new Point[26];

		// copying each one over in a loop
		for (int i = 0; i < copy.Points.length; i++) {
			// copy
			final Point x = new Point(copy.Points[i]);
			// add
			Points[i] = x;
		}

		// copying bored
		redBore = copy.redBore;
		blackBore = copy.blackBore;
	}

	/**
	 * Adds the to bear.
	 *
	 * @param black
	 *            the player color
	 */
	public void addToBear(final boolean black) {

		if (black) {
			blackBore++;
		} else {
			redBore++;
		}
	}

	/**
	 * Bear piece.
	 *
	 * @param bearPeice
	 *            the position to bear
	 * @param black
	 *            the player color
	 */
	public void bearPiece(final int bearPeice, final boolean black) {

		Points[bearPeice].removePiece(black);
		addToBear(black);
	}

	/**
	 * Can player bear.
	 *
	 * checks the board positions etc to check if the player can legally bear
	 *
	 * @param black
	 *            the player color
	 * @return true, if successful
	 */
	public boolean canPlayerBear(final boolean black) {
		if (black) {
			// looping all areas apart from the players own quarter
			for (int x = 7; x < 25; x++) {
				// if any of them contain a black piece
				if (Points[x].getBlackCount() > 0) {
					return false;
				}
			}

		} else {
			// looping all areas apart from the players own quarter
			for (int x = 1; x < 18; x++) {
				// if any of them contain a red piece
				if (Points[x].getRedCount() > 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Equals.
	 *
	 * Board comparison, if they are the same (same peice positions)
	 *
	 * @param b
	 *            the board
	 * @return true, if they are the same
	 */
	public boolean equals(final Board b) {

		boolean theSame = true;

		// checking beared points
		if (redBore != b.redBore || blackBore != b.blackBore) {
			theSame = false;
		}

		// checking points
		for (int x = 0; x < Points.length; x++) {
			if (!Points[x].equals(b.Points[x])) {
				theSame = false;
			}
		}

		return theSame;

	}

	public int getBlackBore() {
		return blackBore;
	}

	public int getNumberOfCheckersOnStacks(final Boolean black) {
		int num = 0;

		// loop points
		for (int x = 0; x < Points.length; x++) {
			// if the current point has more than 2 checkers (a stack) and is
			// ours
			if (Points[x].numEither() > 2 && Points[x].getCol() == black) {
				// Add the count of the stack to the int
				num += Points[x].numEither();
			}
		}
		return num;
	}

	/**
	 * getNumberOfPiecesSurroundOpponent
	 *
	 * @param black
	 *            player color
	 * @return int - number of pieces
	 */
	public int getNumberOfPiecesSurroundOpponent(final Boolean black) {
		int num = 0;
		// loop points
		for (int x = 0; x < Points.length; x++) {
			// if there is an oposition peice
			if (Points[x].numEither() > 0 && Points[x].getCol() != black) {
				if (x < 25 && x > 0) {
					// checking one before that
					if (Points[x - 1].numEither() > 0 && Points[x - 1].getCol() == black) {
						num++;
					}
					// checking the point after it
					if (Points[x + 1].numEither() > 0 && Points[x + 1].getCol() == black) {
						num++;
					}
				}
			}
		}
		return num;
	}

	/**
	 * getNumOfBlots
	 *
	 * @param black
	 *            - player color
	 * @return int - number of blots
	 */
	public int getNumOfBlots(final boolean black) {
		int num = 0;
		for (int x = 0; x < Points.length; x++) {
			if (Points[x].numEither() == 1 && Points[x].getCol() == black) {
				num++;
			}
		}
		return num;
	}

	/**
	 * getNumOfHomePointsCovered
	 *
	 * @param black
	 *            - player color
	 * @return int - num of points covered
	 */
	public int getNumOfHomePointsCovered(final Boolean black) {
		int num = 0;
		// BLACK
		if (black) {
			for (int x = 1; x <= 6; x++) {
				if (Points[x].numEither() > 0 && Points[x].getCol() == black) {
					num++;
				}
			}
			// RED
		} else {
			for (int x = 19; x <= 24; x++) {
				if (Points[x].numEither() > 0 && Points[x].getCol() == black) {
					num++;
				}
			}
		}
		return num;
	}

	public int getRedBore() {
		return redBore;
	}

	/**
	 * Checks for player winning.
	 *
	 * @param black
	 *            the player color
	 * @return true, if they have won
	 */
	public boolean hasPlayerWon(final Boolean black) {
		// checking if the bore pieces = 15
		if (black && blackBore == 15 || !black && redBore == 15) {
			// won
			return true;
		} else {
			// not won yet
			return false;
		}
	}

	/**
	 * How many has player bore.
	 *
	 * @param black
	 *            the blackBore
	 * @return the num of bore
	 */
	public int howManyHasPlayerBore(final boolean black) {
		if (black) {
			return blackBore;
		} else {
			return redBore;
		}
	}

	public boolean isthereZero(final boolean black) {

		if (black) {
			if (Points[25].getBlackCount() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if (Points[0].getRedCount() > 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	public void printBoard() {
		System.out.println("|---------------------------------------|");
		System.out
				.println("|  Black 0 = " + Points[25].numEither() + "        |" + "  Beared: " + blackBore + "     |");
		System.out.println("|---------------------------------------|");
		System.out.println("|NUM| 1| 2| 3| 4| 5| 6| 7| 8| 9|10|11|12|");
		System.out.print("|RED");
		for (int x = 1; x <= 12; x++) {
			System.out.print("| " + Points[x].getRedCount());
		}
		System.out.println("|");
		System.out.print("|BLK");
		for (int x = 1; x <= 12; x++) {
			System.out.print("| " + Points[x].getBlackCount());
		}
		System.out.println("|");
		System.out.println("|---------------------------------------|");
		System.out.println("|NUM|13|14|15|16|17|18|19|20|21|22|23|24|");
		System.out.print("|RED");
		for (int x = 13; x <= 24; x++) {
			System.out.print("| " + Points[x].getRedCount());
		}
		System.out.println("|");
		System.out.print("|BLK");
		for (int x = 13; x <= 24; x++) {
			System.out.print("| " + Points[x].getBlackCount());
		}
		System.out.println("|");
		System.out.println("|---------------------------------------|");
		System.out.println("|  Red 0 = " + Points[0].numEither() + "          |" + "  Beared: " + redBore + "     |");
		System.out.println("|---------------------------------------|");
	}

	public void printBoardGUI() {
		if (GameSettings.getDisplayGUI()) {
			GameManager.boardContainerFrame.bp.printCheckers(Points, redBore, blackBore);
		}
	}

	public void setBlackBore(final int blackBore) {
		this.blackBore = blackBore;
	}

	public void setRedBore(final int redBore) {
		this.redBore = redBore;
	}

	public void setStartPosition() {
		// Resetting all to 0
		for (int x = 0; x < Points.length; x++) {
			Points[x].setBlackCount(0);
			Points[x].setRedCount(0);
		}
		// completing the starting position of the checkers, the rest left at 0
		// Red Checkers
		Points[1].setRedCount(2);
		Points[12].setRedCount(5);
		Points[17].setRedCount(3);
		Points[19].setRedCount(5);
		// Black Checkers
		Points[6].setBlackCount(5);
		Points[8].setBlackCount(3);
		Points[13].setBlackCount(5);
		Points[24].setBlackCount(2);
	}

}
