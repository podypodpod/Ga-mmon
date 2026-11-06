/*
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