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

public class PlayerTest {

	private Player player1, player2;
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
		player1 = new Player(false);// p1 is red
		player2 = new Player(true);// p2 is black

		board = new Board();
		board.setStartPosition();
	}

	@Test
	public void settingBoardPositionAndValidMovesLeftAndCallingMovePossibleWithASimple2PositionMoveShouldReturnTrue() {
		// leaving a red piece on its own
		player1.movePiece(12, 11, board);
		player2.movesLeft.moves.add(2);

		// When
		final boolean possible = player2.isMovePossible(13, 11, board);

		// Then
		assertTrue(possible);
	}

	@Test
	public void whenCallingPassesBasicChecksWhenThereIsNoPieceOnTheStartItShouldFail() {
		// given
		final int to = 7;
		final int from = 5;
		player1.movesLeft.add(2);

		// then
		assertFalse(player1.passesBasicChecks(to, from, board));
	}

	@Test
	public void whenRedTakesABlackPieceItShouldMoveItTo25Slot() {

		// Given
		player2.movePiece(6, 2, board);

		// When
		player1.movePiece(1, 2, board);

		// Then
		assertEquals(1, board.getRedCount(2));// new piece there
		assertEquals(1, board.getBlackCount(25));// old piece moved
	}

	@Test
	public void whenBlackTakesARedPieceItShouldMoveItTo0Slot() {

		// Given
		player1.movePiece(1, 2, board);

		// When
		player2.movePiece(6, 2, board);

		// Then
		assertEquals(1, board.getBlackCount(2));// new piece there
		assertEquals(1, board.getRedCount(0));// old piece moved
	}

	@Test
	public void whenDoingSimpleMoveToAnEmptySpaceAndCheckingCountShouldReturn1() {
		// When
		player1.movePiece(1, 2, board);

		// Then
		assertEquals(1, board.getRedCount(2));
	}

	@Test
	public void whenMovingARedPieceFrom0SlotBackOnBoardShouldActLikeAnyOtherMove() {
		// Given
		player1.movePiece(19, 0, board);// move a piece to 0 place
		player1.movesLeft.moves.add(2);// give it a move of 2

		// Then
		assertTrue(player1.isMovePossible(0, 2, board));// moving back to
		// board
	}

	@Test
	public void whenMovingAPieceInTheWrongDirectionShouldFailBasicChecks() {
		// given
		final int to = 10;
		final int from = 12;
		player1.movesLeft.add(2);

		// then
		assertFalse(player1.passesBasicChecks(to, from, board));
	}

	@Test
	public void callingMovePossibleOnTakingAPieceWhenYouAlreadyHaveAZeroShouldReturnFalse() {

		// leaving a piece on its own
		player1.movePiece(12, 11, board);

		// moving piece to zero
		player2.movePiece(13, 25, board);
		// give 2 move
		player2.movesLeft.moves.add(2);

		// Then
		assertFalse(player2.isMovePossible(13, 11, board));
	}

	@Test
	public void callingMovePossibleWhenTryingToTakeAnOpponentPieceWith2MarkersOnShouldReturnFalse() {

		// putting 2 red pieces on their own
		player1.movePiece(12, 11, board);
		player1.movePiece(12, 11, board);

		player2.movesLeft.moves.add(2);

		// Then
		assertFalse(player2.isMovePossible(13, 11, board));
	}

	@Test
	public void callingMovePieceWhenThereIsAZeroShouldReturnFalse() {

		// Given
		player1.movePiece(19, 0, board);
		player1.movesLeft.moves.add(2);

		// Then
		assertFalse(player1.isMovePossible(19, 21, board));

	}

	@Test
	public void callingBasicChecksWithAnInvalidNumberTooLowShouldReturnFalse() {
		// given
		final int to = 3;
		final int from = -5;
		player1.movesLeft.add(2);

		// then
		assertFalse(player1.passesBasicChecks(to, from, board));
	}

	@Test
	public void callingBasicChecksWithAnInvalidNumberTooHighShouldReturnFalse() {
		// given
		// 30 is as far as a 6 can take a red piece, borne off from 24
		final int to = 31;
		final int from = 1;
		player1.movesLeft.add(2);

		// then
		assertFalse(player1.passesBasicChecks(to, from, board));
	}

	@Test
	public void validSimpleMoveShouldPassBasicChecks() {
		// given
		final int to = 3;
		final int from = 1;
		player1.movesLeft.add(2);

		// then
		assertTrue(player1.passesBasicChecks(to, from, board));
	}

	@Test
	public void callingDistanceBetweenWith4And10ShouldReturn6() {

		// Given
		final int a = 4;
		final int b = 10;

		// When
		final int c = player1.distanceBetween(a, b);

		// Then
		assertEquals(6, c);
	}

	@Test
	public void callingDistanceBetweenWith23And5ShouldReturn18() {

		// Given
		final int a = 23;
		final int b = 5;

		// When
		final int c = player1.distanceBetween(a, b);

		// Then
		assertEquals(18, c);
	}

	@Test
	public void callingMovePossibleWith2DifferentValidValuesUsing2DifferentValidMovesShouldReturnTrue() {

		// Given
		player1.movesLeft.add(1);
		player1.movesLeft.add(2);

		// Then
		assertTrue(player1.isMovePossible(1, 3, board));
		assertTrue(player1.isMovePossible(1, 2, board));
	}

	@Test
	public void callingPassesBasicChecksWhenThereIsAZeroOnTheSideOfTheMoveShouldReturnFalse() {
		// given
		final int to = 3;
		final int from = 1;

		player1.movesLeft.add(2);
		board.addPiece(0, false);// add zero piece

		// then
		assertFalse(player1.passesBasicChecks(to, from, board));
	}

	@Test
	public void callingPassesBasicChecksWhenThereIsAZeroOnTheSideOfTheOppositionShouldReturnTrue() {
		// given
		final int to = 3;
		final int from = 1;

		player1.movesLeft.add(2);
		board.addPiece(25, true);// adding opponent zero

		// then
		assertTrue(player1.passesBasicChecks(to, from, board));
	}

	// The bearing off tests set up black's position, and check red with the mirror image of it, where point p is 25 - p

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPieceCanBeBorneOffWithTheExactRoll(final boolean black) {
		assertTrue(canMove(black, homeBoard(), 3, 3));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aBiggerRollBearsOffThePieceFurthestFromHome(final boolean black) {
		final Board blackPosition = homeBoard();
		blackPosition.setBlackCount(6, 0);

		assertTrue(canMove(black, blackPosition, 3, 6));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aBiggerRollCantBearOffWhileAPieceIsFurtherFromHome(final boolean black) {
		assertFalse(canMove(black, homeBoard(), 3, 4));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPieceCantBeBorneOffWhileAnotherIsOutsideHome(final boolean black) {
		final Board blackPosition = homeBoard();
		blackPosition.setBlackCount(7, 1);

		assertFalse(canMove(black, blackPosition, 3, 3));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void bearingOffTakesThePieceOffTheBoard(final boolean black) {
		final Board start = black ? homeBoard() : BoardMirror.mirror(homeBoard());
		final int from = black ? 3 : 22;

		new Player(black).movePiece(from, black ? -2 : 27, start);

		assertEquals(0, black ? start.getBlackCount(from) : start.getRedCount(from));
		assertEquals(1, start.howManyHasPlayerBore(black));
	}

	/**
	 * Every black piece is home, one on 3 and two on 6, and red has two on 20
	 */
	private static Board homeBoard() {
		final Board home = new Board();
		home.removeAllPieces();
		home.setBlackCount(3, 1);
		home.setBlackCount(6, 2);
		home.setRedCount(20, 2);
		return home;
	}

	/**
	 * Whether the player can move the piece on black's point from with the roll, mirrored for red
	 */
	private static boolean canMove(final boolean black, final Board blackPosition, final int from, final int roll) {
		final Player player = new Player(black);
		player.movesLeft.add(roll);
		if (black) {
			return player.isMovePossible(from, from - roll, blackPosition);
		}
		return player.isMovePossible(25 - from, 25 - from + roll, BoardMirror.mirror(blackPosition));
	}
}
