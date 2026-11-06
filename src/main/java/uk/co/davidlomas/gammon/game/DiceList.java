package uk.co.davidlomas.gammon.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Class DiceList.
 * <p>
 * Container for the Dice rolls, allows the looping through the moves left
 * during possible move creation
 * <p>
 * could have created this code inside another class but I moved it out to make
 * space.
 *
 * @author David Lomas
 */
class DiceList {

	/**
	 * The dice.
	 */
	private final Die dice1;
	private final Die dice2;

	/**
	 * Instantiates a new dice list.
	 */
	DiceList() {
		dice1 = new Die();
		dice2 = new Die();
	}

	int rollOneDie() {
		return dice1.RollDie();
	}

	/**
	 * Roll dice. Rolls two random dice and returns the results
	 *
	 * @return list of Dice rolls
	 */
	List<Integer> RollTwoDice() {

		final List<Integer> diceRolls = new ArrayList<>();

		// roll dice
		diceRolls.add(dice1.RollDie());
		diceRolls.add(dice2.RollDie());

		// add 2 extra if there is a double roll
		if (Objects.equals(diceRolls.get(0), diceRolls.get(1))) {
			diceRolls.add(diceRolls.get(0));
			diceRolls.add(diceRolls.get(0));
		}
		return diceRolls;
	}
}