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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest {

	private Board board;

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}


	@BeforeEach
	public void setUp() throws Exception {
		board = new Board();
	}

	/**
	 * Checking the clone cons copies values
	 */
	@Test
	public void testingCloneBoardConstructor() {
		board.setBlackBore(10);
		final Board clonedBoard = new Board(board);
		assertEquals(10, clonedBoard.getBlackBore(), "Cloning boards doesn't keep values");
	}

	@Test
	public void testingAddingACounterToABoardsPointsWorks() {
		assertEquals(0, board.Points[1].getBlackCount(), "point [1] should start with 0");
		board.Points[1].addBlackPiece();
		assertEquals(1, board.Points[1].getBlackCount(), "point [1] didnt get added to");
	}

	@Test
	public void addingOneToBlack25SlotAndThenCallingIsThereZeroShouldReturnTrue() {
		board.Points[25].addBlackPiece();
		final Boolean isThereZero = board.isThereZero(true);
		assertEquals(true, isThereZero);
	}

	@Test
	public void callingIsThereZeroOnADefaultBoardShouldReturnFalseForBlack() {
		final Boolean isThereZero = board.isThereZero(true);
		assertEquals(false, isThereZero);
	}

	@Test
	public void callingIsThereZeroOnADefaultBoardShouldReturnFalseForRed() {
		final Boolean isThereZero = board.isThereZero(false);
		assertEquals(false, isThereZero);
	}

	@Test
	public void addingOneToRed0SlotAndThenCallingIsThereZeroShouldReturnTrue() {
		board.Points[0].addRedPiece();
		final Boolean isThereZero = board.isThereZero(false);
		assertEquals(true, isThereZero);
	}

	@Test
	public void givenADefaultBoardThePositionsShouldBeAsFollows() {

		// Given standard BG start is applied

		// RED
		final Point x1 = board.Points[1];
		final Point x12 = board.Points[12];
		final Point x17 = board.Points[17];
		final Point x19 = board.Points[19];
		// BLACK
		final Point x6 = board.Points[6];
		final Point x8 = board.Points[8];
		final Point x13 = board.Points[13];
		final Point x24 = board.Points[24];

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

	// The bearing off tests set up black's position, and check red with the mirror image of it

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPlayerWithEveryPieceHomeCanBearOff(final boolean black) {
		assertTrue(forColour(endGameBoard(), black).canPlayerBear(black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPlayerWithAPieceJustOutsideHomeCannotBearOff(final boolean black) {
		final Board blackPosition = endGameBoard();
		blackPosition.Points[7].setBlackCount(1);

		assertFalse(forColour(blackPosition, black).canPlayerBear(black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPlayerWithAPieceOnTheBarCannotBearOff(final boolean black) {
		final Board blackPosition = endGameBoard();
		blackPosition.Points[25].setBlackCount(1);

		assertFalse(forColour(blackPosition, black).canPlayerBear(black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void homePointsCoveredCountsOnlyThePlayersOwnPoints(final boolean black) {
		final Board blackPosition = endGameBoard();
		// an opponent's piece in the home quarter isn't one of ours
		blackPosition.Points[3].setRedCount(1);

		assertEquals(2, forColour(blackPosition, black).getNumOfHomePointsCovered(black));
	}

	@Test
	public void theMirrorImageSwapsTheBorneOffCounts() {
		final Board blackPosition = endGameBoard();
		blackPosition.setBlackBore(11);
		blackPosition.setRedBore(4);

		final Board mirrored = BoardMirror.mirror(blackPosition);

		assertEquals(4, mirrored.getBlackBore());
		assertEquals(11, mirrored.getRedBore());
	}

	/**
	 * Black has a piece on 1 and three on 6, its home quarter, and red has two on 20
	 */
	private static Board endGameBoard() {
		final Board endGame = new Board();
		for (final Point point : endGame.Points) {
			point.setBlackCount(0);
			point.setRedCount(0);
		}
		endGame.Points[1].setBlackCount(1);
		endGame.Points[6].setBlackCount(3);
		endGame.Points[20].setRedCount(2);
		return endGame;
	}

	private static Board forColour(final Board blackPosition, final boolean black) {
		return black ? blackPosition : BoardMirror.mirror(blackPosition);
	}
}