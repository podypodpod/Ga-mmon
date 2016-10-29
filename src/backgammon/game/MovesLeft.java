/**
 * 	GNU General Public License
 * 
 *  This file is part of GA-mmon.
 *  
 *  GA-mmon is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  GA-mmon is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with GA-mmon.  If not, see <http://www.gnu.org/licenses/>.
*/

package backgammon.game;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class MovesLeft.
 * 
 * used for loops checking which moves the player has left to use
 * 
 * @author David Lomas - 11035527
 */
public class MovesLeft {

	/** The moves left. */
	public List<Integer> movesLeft;

	/**
	 * MovesLeft default constructor
	 * 
	 * Instantiates a new moves left.
	 */
	public MovesLeft() {
		movesLeft = new ArrayList<Integer>();
	}

	/**
	 * Clone constructor
	 * 
	 * Instantiates a new moves left.
	 *
	 * @param movesLeftCopy
	 *            the moves left copy
	 */
	public MovesLeft(MovesLeft movesLeftCopy) {

		// create new list
		this.movesLeft = new ArrayList<Integer>();

		// copy old one
		for (int x : movesLeftCopy.movesLeft) {
			this.movesLeft.add(x);
		}
	}

	/**
	 * Add adds the param passed in to the list
	 *
	 * @param value
	 *            the int to be added
	 */
	void add(int value) {
		movesLeft.add(Integer.valueOf(value));
	}

	/**
	 * Remove removes the value passed in
	 *
	 * @param value
	 *            the value to be removed
	 */
	void remove(int value) {
		movesLeft.remove(Integer.valueOf(value));
	}

	/**
	 * Size.
	 *
	 * @return the size of list
	 */
	int size() {
		return movesLeft.size();
	}

	/**
	 * Gets the next.
	 *
	 * @return the next
	 */
	int getNext() {
		return movesLeft.get(0);
	}

	/**
	 * Clear.
	 */
	void clear() {
		movesLeft.clear();
	}

	/**
	 * setTo
	 * 
	 * Sets the list to the same as the one passed in
	 *
	 * @param diceRolls
	 *            - set the current list to contents of dicerolls
	 */
	public void setTo(List<Integer> diceRolls) {
		clear();
		for (int x : diceRolls) {
			add(x);
		}
	}

	/*
	 * toString method
	 */
	@Override
	public String toString() {
		String str = size() + " moves Left, with Rolls: ";

		for (int x = 0; x < size(); x++) {
			str += movesLeft.get(x) + ", ";
		}

		return str;

	}

	public boolean contains(int i) {
		if (movesLeft.contains(i)) {
			return true;
		}
		return false;
	}

}
