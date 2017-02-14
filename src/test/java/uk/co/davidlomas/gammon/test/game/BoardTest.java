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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.Point;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.Assert.assertEquals;

public class BoardTest {

  private Board board;

  @BeforeClass
  public static void beforeClass() {
    SettingsUtil.resetSettings();
  }

  @AfterClass
  public static void afterClass() {
    SettingsUtil.resetSettings();
  }


  @Before
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
    assertEquals("Cloning boards doesn't keep values", 10, clonedBoard.getBlackBore());
  }

  @Test
  public void testingAddingACounterToABoardsPointsWorks() {
    assertEquals("point [1] should start with 0", board.Points[1].getBlackCount(), 0);
    board.Points[1].addBlackPiece();
    assertEquals("point [1] didnt get added to", board.Points[1].getBlackCount(), 1);
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
}