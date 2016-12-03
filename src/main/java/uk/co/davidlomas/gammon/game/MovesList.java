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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class MovesLeft.
 *
 * used for loops checking which moves the player has left to use
 *
 * @author David Lomas
 */
public class MovesList {

	public final List<Integer> moves;

	public MovesList() {
		moves = new ArrayList<>();
	}

	/**
	 * Clone constructor
	 *
	 * Instantiates a new moves left.
	 *
	 * @param movesListCopy
	 */
	public MovesList(final MovesList movesListCopy) {
		moves = new ArrayList<>();
		for (final int move : movesListCopy.moves) {
			moves.add(move);
		}
	}

	/**
	 * Add adds the param passed in to the list
	 *
	 * @param value
	 */
	public void add(final int value) {
		moves.add(Integer.valueOf(value));
	}

	public void clear() {
		moves.clear();
	}

	public boolean contains(final int i) {
		if (moves.contains(i)) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the next.
	 *
	 * @return the next
	 */
	public int getNext() {
		return moves.get(0);
	}

	/**
	 * Remove removes the value passed in
	 *
	 * @param value
	 */
	public void remove(final int value) {
		moves.remove(Integer.valueOf(value));
	}

	/**
	 * setTo
	 *
	 * Sets the list to the same as the one passed in
	 *
	 * @param diceRolls
	 */
	public void setTo(final List<Integer> diceRolls) {
		clear();
		for (final int roll : diceRolls) {
			add(roll);
		}
	}

	/**
	 * Size.
	 *
	 * @return the size of list
	 */
	public int size() {
		return moves.size();
	}

	/**
	 * toString method
	 */
	@Override
	public String toString() {
		String str = size() + " moves Left, with Rolls: ";

		for (int x = 0; x < size(); x++) {
			str += moves.get(x) + ", ";
		}
		return str;
	}
}
