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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
		assertEquals(0, board.getBlackCount(1), "point [1] should start with 0");
		board.addPiece(1, true);
		assertEquals(1, board.getBlackCount(1), "point [1] didnt get added to");
	}

	@Test
	public void addingOneToBlack25SlotAndThenCallingIsThereZeroShouldReturnTrue() {
		board.addPiece(25, true);
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
		board.addPiece(0, false);
		final Boolean isThereZero = board.isThereZero(false);
		assertEquals(true, isThereZero);
	}

	@Test
	public void givenADefaultBoardThePositionsShouldBeAsFollows() {

		// Given standard BG start is applied

		// Then

		// RED
		assertEquals(2, board.getRedCount(1));
		assertEquals(5, board.getRedCount(12));
		assertEquals(3, board.getRedCount(17));
		assertEquals(5, board.getRedCount(19));

		// BLACK
		assertEquals(5, board.getBlackCount(6));
		assertEquals(3, board.getBlackCount(8));
		assertEquals(5, board.getBlackCount(13));
		assertEquals(2, board.getBlackCount(24));
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
		blackPosition.setBlackCount(7, 1);

		assertFalse(forColour(blackPosition, black).canPlayerBear(black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPlayerWithAPieceOnTheBarCannotBearOff(final boolean black) {
		final Board blackPosition = endGameBoard();
		blackPosition.setBlackCount(25, 1);

		assertFalse(forColour(blackPosition, black).canPlayerBear(black));
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

	@Test
	public void eachCountIsKeptApartFromTheOtherPlayersAndTheNextPoints() {
		final Board empty = new Board();
		empty.removeAllPieces();

		empty.setBlackCount(4, 3);
		empty.setRedCount(5, 2);

		assertEquals(3, empty.getBlackCount(4));
		assertEquals(3, empty.getCount(4, true));
		assertEquals(0, empty.getRedCount(4));
		assertEquals(2, empty.getRedCount(5));
		assertEquals(2, empty.getCount(5, false));
		assertEquals(0, empty.getBlackCount(5));
	}

	@Test
	public void piecesCanBeAddedToAndRemovedFromEitherPlayersCount() {
		final Board empty = new Board();
		empty.removeAllPieces();

		empty.addPiece(0, false);
		empty.addPiece(25, true);
		empty.addPiece(25, true);
		empty.removePiece(25, true);

		assertEquals(1, empty.getRedCount(0));
		assertEquals(1, empty.getBlackCount(25));
	}

	@Test
	public void aPointBelongsToWhoeverHasMorePiecesOnItAndAnEmptyOneCountsAsRed() {
		final Board empty = new Board();
		empty.removeAllPieces();
		empty.setBlackCount(3, 2);
		empty.setRedCount(4, 5);

		assertTrue(empty.getCol(3));
		assertEquals(2, empty.numEither(3));
		assertFalse(empty.getCol(4));
		assertEquals(5, empty.numEither(4));
		assertFalse(empty.getCol(5));
		assertEquals(0, empty.numEither(5));
	}

	@Test
	public void aCopyDoesNotChangeWithTheBoardItWasCopiedFrom() {
		final Board original = new Board();
		final Board copy = new Board(original);
		assertEquals(original, copy);
		assertEquals(original.hashCode(), copy.hashCode());

		original.addPiece(6, true);

		assertEquals(5, copy.getBlackCount(6));
		assertNotEquals(original, copy);
	}

	@Test
	public void removingEveryPieceLeavesWhatsBeenBorneOff() {
		final Board withBorneOff = new Board();
		withBorneOff.setBlackBore(2);

		withBorneOff.removeAllPieces();

		assertEquals(0, withBorneOff.numEither(6));
		assertEquals(2, withBorneOff.getBlackBore());
	}

	/**
	 * Black has a piece on 1 and three on 6, its home quarter, and red has two on 20
	 */
	private static Board endGameBoard() {
		final Board endGame = new Board();
		endGame.removeAllPieces();
		endGame.setBlackCount(1, 1);
		endGame.setBlackCount(6, 3);
		endGame.setRedCount(20, 2);
		return endGame;
	}

	private static Board forColour(final Board blackPosition, final boolean black) {
		return black ? blackPosition : BoardMirror.mirror(blackPosition);
	}
}