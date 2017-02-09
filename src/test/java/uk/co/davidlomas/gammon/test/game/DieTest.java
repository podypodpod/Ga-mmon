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

package uk.co.davidlomas.gammon.test.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.game.Die;
import uk.co.davidlomas.gammon.test.helpers.Settings;

public class DieTest {

  @BeforeClass
  public static void beforeClass() {
    Settings.resetSettings();
  }

  @AfterClass
  public static void afterClass() {
    Settings.resetSettings();
  }

  private Die d1;

  @Before
  public void setUp() throws Exception {
    d1 = new Die();
  }

  @Test
  public void GetDieValueShouldEqualWhatIsSet() {
    d1.setDieValue(4);
    assertEquals(4, d1.getDieValue());
    d1.setDieValue(100);
    assertEquals(100, d1.getDieValue());
  }

  @Test
  public void RollingShouldProduceAnIntBetween1And6() {
    int roll;
    for (int x = 0; x > 10; x++) {
      roll = d1.RollDie();
      assertTrue(roll >= 1 && roll <= 6);
    }
  }
}