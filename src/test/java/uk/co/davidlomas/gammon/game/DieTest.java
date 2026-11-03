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