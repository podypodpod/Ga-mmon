package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DieTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	private Die die1;

	@BeforeEach
	public void setUp() throws Exception {
		die1 = new Die();
	}

	@Test
	public void GetDieValueShouldEqualWhatIsSet() {
		die1.setValue(4);
		assertEquals(4, die1.getValue());
		die1.setValue(100);
		assertEquals(100, die1.getValue());
	}

	@Test
	public void RollingShouldProduceAnIntBetween1And6() {
		int roll;
		for (int x = 0; x < 10; x++) {
			roll = die1.RollDie();
			assertTrue(roll >= 1 && roll <= 6);
		}
	}
}