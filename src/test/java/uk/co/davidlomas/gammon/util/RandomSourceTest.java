package uk.co.davidlomas.gammon.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomSourceTest {

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
	}

	@Test
	public void theSameDiceSeedRollsTheSameDiceWhateverElseUsesTheRandom() {
		// two games can make different choices, which use the main random, and still roll the same dice
		RandomSource.setDiceSeed(11);
		final List<Integer> first = rollDice(20, 0);

		RandomSource.setDiceSeed(11);
		final List<Integer> second = rollDice(20, 7);

		assertEquals(first, second);
	}

	@Test
	public void diceRollOneToSix() {
		RandomSource.setSeed(1);
		for (final int roll : rollDice(200, 0)) {
			assertTrue(roll >= 1 && roll <= 6, "rolled " + roll);
		}
	}

	/**
	 * Rolls this many dice, drawing other numbers from the main random between each roll
	 */
	private static List<Integer> rollDice(final int rolls, final int otherDrawsBetween) {
		final List<Integer> dice = new ArrayList<>();
		for (int roll = 0; roll < rolls; roll++) {
			dice.add(RandomSource.rollDie());
			for (int draw = 0; draw < otherDrawsBetween; draw++) {
				RandomSource.nextInt(100);
			}
		}
		return dice;
	}
}
