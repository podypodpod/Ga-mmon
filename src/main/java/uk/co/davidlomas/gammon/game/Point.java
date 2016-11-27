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

/**
 * The Class Point.
 *
 * represents the point on a backgammon board
 *
 * contains pieces
 *
 * Used by the board 24 times to create the board state
 *
 * @author David Lomas - 11035527
 */
public class Point {

	private int blackCount;
	private int redCount;

	/**
	 * default constructor Instantiates a new point.
	 */
	public Point() {
		blackCount = 0;
		redCount = 0;
	}

	/**
	 * clone constructor Instantiates a new point.
	 *
	 * @param p
	 *            the point to copy
	 */
	public Point(final Point p) {
		blackCount = p.blackCount;
		redCount = p.redCount;
	}

	/**
	 * Adds the black piece.
	 */
	public void addBlackPiece() {
		blackCount++;
	}

	/**
	 * Adds the piece.
	 *
	 * @param black
	 *            the black
	 */
	public void addPiece(final Boolean black) {
		if (black) {
			addBlackPiece();
		} else {
			addRedPiece();
		}
	}

	/**
	 * Adds the red piece.
	 */
	public void addRedPiece() {
		redCount++;
	}

	/**
	 * Equals.
	 *
	 * @param p
	 *            the point
	 * @return true, if successful
	 */
	public boolean equals(final Point p) {
		boolean theSame = true;

		if (redCount != p.redCount) {
			theSame = false;
		} else if (blackCount != p.blackCount) {
			theSame = false;
		}

		return theSame;
	}

	/**
	 * Gets the black count.
	 *
	 * @return the black count
	 */
	public int getBlackCount() {
		return blackCount;
	}

	/**
	 * Gets the col.
	 *
	 * @return the col
	 */
	public boolean getCol() {
		if (blackCount > redCount) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the red count.
	 *
	 * @return the red count
	 */
	public int getRedCount() {
		return redCount;
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		if (blackCount == 0 && redCount == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Num of either value.
	 *
	 * @return the int
	 */
	public int numEither() {
		if (blackCount > redCount) {
			return blackCount;
		} else {
			return redCount;
		}
	}

	/**
	 * Removes the black piece.
	 */
	public void removeBlackPiece() {
		blackCount--;
	}

	/**
	 * Removes the piece.
	 *
	 * @param black
	 *            the black
	 */
	public void removePiece(final Boolean black) {
		if (black) {
			removeBlackPiece();
		} else {
			removeRedPiece();
		}
	}

	/**
	 * Removes the red piece.
	 */
	public void removeRedPiece() {
		redCount--;
	}

	/**
	 * Sets the black count.
	 *
	 * @param bc
	 *            the new black count
	 */
	public void setBlackCount(final int bc) {
		blackCount = bc;
	}

	/**
	 * Sets the red count.
	 *
	 * @param rc
	 *            the new red count
	 */
	public void setRedCount(final int rc) {
		redCount = rc;
	}
}