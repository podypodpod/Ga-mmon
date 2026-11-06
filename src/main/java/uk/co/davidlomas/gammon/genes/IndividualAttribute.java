package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.util.RandomSource;

/**
 * IndividualAttribute
 * <p>
 * One of an individual's genes: the name of a feature of the board, and a value from 0 to 100 that gives the weight
 * the player puts on it. See MoveChooser
 *
 * @author David Lomas
 */
public class IndividualAttribute {

	private static final int MAX_VALUE = 100;

	private String name = "";
	private int value = 0;

	public IndividualAttribute(final String name) {
		this.name = name;
		value = RandomSource.nextInt(100);
	}

	@SuppressWarnings("SameParameterValue")
	public IndividualAttribute(final String name, final int value) {
		this.name = name;
		this.value = checked(value);
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setValue(final int value) {
		this.value = checked(value);
	}

	/**
	 * @throws IllegalArgumentException if it isn't 0 to 100. A gene has 7 bits in the chromosome, so a value that needs
	 *                                  more would put all the genes after it in the wrong place
	 */
	private static int checked(final int value) {
		if (value < 0 || value > MAX_VALUE) {
			throw new IllegalArgumentException("A gene goes from 0 to " + MAX_VALUE + ", not " + value);
		}
		return value;
	}

	@Override
	public String toString() {
		return name + ": " + value;
	}
}