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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.Player;
import uk.co.davidlomas.gammon.test.helpers.Settings;

public class PlayerTest {

  private Player player1, player2;
  private Board board;

  @BeforeClass
  public static void beforeClass() {
    Settings.resetSettings();
  }

  @AfterClass
  public static void afterClass() {
    Settings.resetSettings();
  }

  @Before
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
    Assert.assertFalse(player1.passesBasicChecks(to, from, board));
  }

  @Test
  public void whenRedTakesABlackPieceItShouldMoveItTo25Slot() {

    // Given
    player2.movePiece(6, 2, board);

    // When
    player1.movePiece(1, 2, board);

    // Then
    assertEquals(1, board.Points[2].getRedCount());// new piece there
    assertEquals(1, board.Points[25].getBlackCount());// old piece moved
  }

  @Test
  public void whenBlackTakesARedPieceItShouldMoveItTo0Slot() {

    // Given
    player1.movePiece(1, 2, board);

    // When
    player2.movePiece(6, 2, board);

    // Then
    assertEquals(1, board.Points[2].getBlackCount());// new piece there
    assertEquals(1, board.Points[0].getRedCount());// old piece moved
  }

  @Test
  public void whenDoingSimpleMoveToAnEmptySpaceAndCheckingCountShouldReturn1() {
    // When
    player1.movePiece(1, 2, board);

    // Then
    assertEquals(1, board.Points[2].getRedCount());
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
    Assert.assertFalse(player1.passesBasicChecks(to, from, board));
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
    Assert.assertFalse(player1.passesBasicChecks(to, from, board));
  }

  @Test
  public void callingBasicChecksWithAnInvalidNumberTooHighShouldReturnFalse() {
    // given
    final int to = 27;
    final int from = 1;
    player1.movesLeft.add(2);

    // then
    Assert.assertFalse(player1.passesBasicChecks(to, from, board));
  }

  @Test
  public void validSimpleMoveShouldPassBasicChecks() {
    // given
    final int to = 3;
    final int from = 1;
    player1.movesLeft.add(2);

    // then
    Assert.assertTrue(player1.passesBasicChecks(to, from, board));
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
    board.Points[0].addRedPiece();// add zero piece

    // then
    Assert.assertFalse(player1.passesBasicChecks(to, from, board));
  }

  @Test
  public void callingPassesBasicChecksWhenThereIsAZeroOnTheSideOfTheOppositionShouldReturnTrue() {
    // given
    final int to = 3;
    final int from = 1;

    player1.movesLeft.add(2);
    board.Points[25].addBlackPiece();// adding opponent zero

    // then
    Assert.assertTrue(player1.passesBasicChecks(to, from, board));
  }

}
