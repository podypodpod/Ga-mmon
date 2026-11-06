package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.util.RandomSource;

/**
 * A dice roll class.
 *
 * @author David Lomas
 */
public class Die {

	private int value;

	public int getValue() {
		return value;
	}

	public void setValue(final int value) {
		this.value = value;
	}

	public int RollDie() {
		value = RandomSource.rollDie();
		return value;
	}
}