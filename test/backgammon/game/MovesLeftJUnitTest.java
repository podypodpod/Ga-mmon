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

package backgammon.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MovesLeftJUnitTest {

	MovesLeft testSubject;

	@Before
	public void setUp() throws Exception {

		testSubject = new MovesLeft();
	}

	@Test
	public void testingCloneConstructor() {
		// Given
		testSubject.add(4);
		testSubject.add(9);
		testSubject.add(3);

		MovesLeft newMovesLeft;

		// When
		newMovesLeft = new MovesLeft(testSubject);

		// Then
		assertEquals(4, newMovesLeft.getNext());

	}

	@Test
	public void testingMovesLeftAddIntFucntion() {

		// When
		testSubject.add(5);

		// Then
		assertEquals(5, testSubject.getNext());

	}

	@Test
	public void testingRemoveMethodRemovingAnItemFromTheArray() {

		// Given
		testSubject.add(4);
		testSubject.add(9);

		// When
		testSubject.remove(4);
		final int x = testSubject.getNext();

		// Then
		assertEquals(9, x);

	}

	@Test
	public void testingSizeMethod() {
		// Given
		testSubject.add(4);
		testSubject.add(9);
		testSubject.add(3);

		// When
		final int x = testSubject.size();

		// Then
		assertEquals(3, x);

	}

}