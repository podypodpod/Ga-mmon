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

	// Each thread has its own randoms, so games played at the same time on different threads, as the fitness
	// calculation does, neither share one nor change each other's: seeding sets only the calling thread's
	private static final ThreadLocal<Random> random = ThreadLocal.withInitial(Random::new);

	// The dice have a random of their own, so the same dice can be rolled again in another game even though the
	// players' choices, which use the main one, differ. The fitness calculation uses this to give both players the
	// same luck
	private static final ThreadLocal<Random> dice = ThreadLocal.withInitial(Random::new);

	private RandomSource() {
	}

	/**
	 * Seed the random source so every following random value on this thread is repeatable. Only this thread: work
	 * started on another thread, such as a search started from the GUI, isn't seeded by it, so seed inside the thread
	 * that does the work, as FitnessCalculator.meet does
	 *
	 * @param seed the seed
	 */
	public static void setSeed(final long seed) {
		random.set(new Random(seed));
		// a different seed for the dice, so they don't repeat the main random's numbers. Multiplying by an odd
		// constant spreads small seeds such as 0 and 1 apart (Random already XORs its seed with 0x5DEECE66D, so that
		// can't be used here: it would cancel out)
		dice.set(new Random(seed * 0x9E3779B97F4A7C15L + 1));
	}

	/**
	 * Start the dice from this seed, so a game can be played with the same dice as another
	 *
	 * @param seed the seed
	 */
	public static void setDiceSeed(final long seed) {
		dice.set(new Random(seed));
	}

	/**
	 * Go back to an unseeded, unpredictable random source on this thread
	 */
	public static void reset() {
		random.set(new Random());
		dice.set(new Random());
	}

	/**
	 * @return a roll of one die, 1 to 6, from the dice's own random
	 */
	public static int rollDie() {
		return dice.get().nextInt(6) + 1;
	}

	/**
	 * @return a random long, such as a seed for the dice
	 */
	public static long nextLong() {
		return random.get().nextLong();
	}

	/**
	 * @param bound the upper bound (exclusive), must be positive
	 *
	 * @return a random int between 0 (inclusive) and bound (exclusive)
	 */
	public static int nextInt(final int bound) {
		return random.get().nextInt(bound);
	}

	/**
	 * @return a random double between 0.0 (inclusive) and 1.0 (exclusive)
	 */
	public static double nextDouble() {
		return random.get().nextDouble();
	}

	/**
	 * @return the underlying random, for library calls such as Collections.shuffle
	 */
	public static Random getRandom() {
		return random.get();
	}
}
