package uk.co.davidlomas.gammon.game;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class MovesLeft.
 * <p>
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
	 * <p>
	 * Instantiates a new moves left.
	 */
	public MovesList(final MovesList movesListCopy) {
		moves = new ArrayList<>(movesListCopy.moves);
	}

	/**
	 * Add adds the param passed in to the list
	 */
	public void add(final int value) {
		moves.add(value);
	}

	public void clear() {
		moves.clear();
	}

	public boolean contains(final int move) {
		return moves.contains(move);
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
	 */
	public void remove(final int value) {
		moves.remove(Integer.valueOf(value));
	}

	/**
	 * setTo
	 * <p>
	 * Sets the list to the same as the one passed in
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
