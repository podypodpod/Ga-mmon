/*
  GNU General Public License

  This file is part of GA-mmon.

  GA-mmon is free software: you can redistribute it and/or modify it under the
  terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
  A PARTICULAR PURPOSE. See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with
  GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.util;

import java.util.Random;

/**
 * The single source of randomness for the whole program (dice, move choices,
 * attribute values, crossover and mutation).
 * <p>
 * Seeding it makes games and evolution repeatable, which tests rely on.
 *
 * @author David Lomas
 */
public final class RandomSource {

	private static Random random = new Random();

	private RandomSource() {
	}

	/**
	 * Seed the random source so every following random value is repeatable
	 *
	 * @param seed the seed
	 */
	public static void setSeed(final long seed) {
		random = new Random(seed);
	}

	/**
	 * Go back to an unseeded, unpredictable random source
	 */
	public static void reset() {
		random = new Random();
	}

	/**
	 * @param bound the upper bound (exclusive), must be positive
	 *
	 * @return a random int between 0 (inclusive) and bound (exclusive)
	 */
	public static int nextInt(final int bound) {
		return random.nextInt(bound);
	}

	/**
	 * @return a random double between 0.0 (inclusive) and 1.0 (exclusive)
	 */
	public static double nextDouble() {
		return random.nextDouble();
	}

	/**
	 * @return the underlying random, for library calls such as Collections.shuffle
	 */
	public static Random getRandom() {
		return random;
	}
}
