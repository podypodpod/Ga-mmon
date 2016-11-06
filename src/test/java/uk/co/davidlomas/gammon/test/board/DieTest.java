/**
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

package uk.co.davidlomas.gammon.test.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.co.davidlomas.gammon.game.Die;

public class DieTest {

	Die d1;

	@Test
	public void GetDieValueShouldEqualWhatIsSet() {
		// when
		d1.setDieValue(4);

		// then
		assertEquals(4, d1.getDieValue());
	}

	@Test
	public void RollingShouldProduceAnIntBetween1And6() {
		// Given
		final int x = d1.RollDie();
		boolean y;

		if (x > 0.99 && x < 6.01) {
			y = true;
		} else {
			y = false;
		}
		assertTrue(y);
	}

	@Before
	public void setUp() throws Exception {
		d1 = new Die();
	}

}
