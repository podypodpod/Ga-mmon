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

public class BoardJUnitTest {

	Board testSubject;

	@Before
	public void setUp() throws Exception {

		testSubject = new Board();
	}

	@Test
	public void testingCloneBoardConstructor() {
		// Given
		Board newBoard;
		testSubject.setBlackBore(10);

		// When
		newBoard = new Board(testSubject);

		// Then
		assertEquals(10, newBoard.getBlackBore());
	}

	@Test
	public void testingIsThereZeroFunctionWhenSendingInColorBLACKtrueWithAZeroPeice() {
		// Given
		testSubject.Points[25].addBlackPiece();

		// When
		final Boolean x = testSubject.isthereZero(true);

		// Then
		assertEquals(true, x);
	}

	@Test
	public void testingIsThereZeroFunctionWhenSendingInColorBlackTRUEWithDefaultBoard() {
		// When
		final Boolean x = testSubject.isthereZero(true);

		// Then
		assertEquals(false, x);
	}

	@Test
	public void testingIsThereZeroFunctionWhenSendingInColorREDFalseWithDefaultBoard() {
		// When
		final Boolean x = testSubject.isthereZero(false);

		// Then
		assertEquals(false, x);
	}

	@Test
	public void testingIsThereZeroFunctionWhenSendingInColorREDtrueWithAZeroPeice() {
		// Given
		testSubject.Points[0].addRedPiece();

		// When
		final Boolean x = testSubject.isthereZero(false);

		// Then
		assertEquals(true, x);
	}

	@Test
	public void willReturnABoardInTheStandardBGStartPosition() {

		// Given
		// standard BG start is applied

		// When

		// RED
		final Point x1 = testSubject.Points[1];
		final Point x12 = testSubject.Points[12];
		final Point x17 = testSubject.Points[17];
		final Point x19 = testSubject.Points[19];
		// BLACK
		final Point x6 = testSubject.Points[6];
		final Point x8 = testSubject.Points[8];
		final Point x13 = testSubject.Points[13];
		final Point x24 = testSubject.Points[24];

		// Then

		// RED
		assertEquals(2, x1.getRedCount());
		assertEquals(5, x12.getRedCount());
		assertEquals(3, x17.getRedCount());
		assertEquals(5, x19.getRedCount());

		// BLACK
		assertEquals(5, x6.getBlackCount());
		assertEquals(3, x8.getBlackCount());
		assertEquals(5, x13.getBlackCount());
		assertEquals(2, x24.getBlackCount());

	}
}
