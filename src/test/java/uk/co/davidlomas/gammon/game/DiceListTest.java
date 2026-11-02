package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiceListTest {
	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
	}

	@Test
	public void rollingTwoDiceGivesTwoMovesOrFourForADouble() {
		RandomSource.setSeed(1);
		final DiceList diceList = new DiceList();
		boolean sawADouble = false;
		boolean sawTwoDifferentDice = false;

		for (int roll = 0; roll < 200; roll++) {
			final List<Integer> moves = diceList.RollTwoDice();

			for (final int move : moves) {
				assertTrue(move >= 1 && move <= 6, "dice value " + move);
			}
			if (moves.size() == 4) {
				sawADouble = true;
				for (final int move : moves) {
					assertEquals(moves.get(0), move, "a double plays the same number four times");
				}
			} else {
				sawTwoDifferentDice = true;
				assertEquals(2, moves.size());
				assertNotEquals(moves.get(0), moves.get(1), "two of the same number should have been a double");
			}
		}

		assertTrue(sawADouble, "200 rolls without a double");
		assertTrue(sawTwoDifferentDice, "200 rolls that were all doubles");
	}

	@Test
	public void rollingOneDieGivesOneToSix() {
		RandomSource.setSeed(1);
		final DiceList diceList = new DiceList();

		for (int roll = 0; roll < 200; roll++) {
			final int value = diceList.rollOneDie();
			assertTrue(value >= 1 && value <= 6, "dice value " + value);
		}
	}
}
