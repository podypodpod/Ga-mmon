package uk.co.davidlomas.gammon.test.game;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.game.AiPlayer;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.BoardEvaluator;
import uk.co.davidlomas.gammon.game.Player;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

public class BoardEvaluatorTest {

  @BeforeClass
  public static void beforeClass() {
    SettingsUtil.resetSettings();
  }

  @AfterClass
  public static void afterClass() {
    SettingsUtil.resetSettings();
  }

  private BoardEvaluator classUnderTest;
  private Board startingBoard;
  private Board secondBoard;

  @Before
  public void setup() {
    classUnderTest = new BoardEvaluator();
    startingBoard = new Board();
    secondBoard = new Board();
    AiPlayer aiPlayer = new AiPlayer(true, new Individual());

    classUnderTest.setBoard(startingBoard);
    classUnderTest.setPlayer(aiPlayer);
  }

  @Test
  public void testingSettingAndGettingTheBoardReturnsTrueWithTheSameBoard() {
    Assert.assertEquals(classUnderTest.getBoard(), startingBoard);
  }

  @Test
  public void testingSettingAndGettingTheBoardReturnsFalseWithTheADifferentBoard() {
    Board newBoard = new Board();
    newBoard.setBlackBore(5);
    Assert.assertNotEquals(classUnderTest.getBoard(), newBoard);
  }

  /**
   * blot = single piece that could be taken
   * double = second piece on top, now safe
   */
  @Test
  public void testingWhenWePutAnExtraPieceOnTopOfASoloThenHasABlotBeenDoubledReturnsTrue() {
    //given
    startingBoard.Points[2].addBlackPiece();

    //when
    secondBoard.Points[2].addBlackPiece();
    secondBoard.Points[2].addBlackPiece();

    //when
    assertTrue("position points[2] hasn't been doubled", classUnderTest.hasABlotBeenDoubled(secondBoard));
  }

  /**
   * blot = single piece that could be taken
   * double = second piece on top, now safe
   */
  @Test
  public void testingWhenWePutAPieceOnAnEmptyPointThenHasABlotBeenDoubledReturnsFalse() {
    //given
    startingBoard.Points[2].addBlackPiece();

    //when
    secondBoard.Points[3].addBlackPiece();

    //when
    assertFalse("position points[2] has been doubled", classUnderTest.hasABlotBeenDoubled(secondBoard));
  }

  /**
   * blot = single piece that could be taken
   * double = second piece on top, now safe
   */
  @Test
  public void testingWhenWePutTwoPiecesOnAnEmptyPointThenHasABlotBeenDoubledReturnsFalse() {
    //given
    startingBoard.Points[2].addBlackPiece();

    //when
    secondBoard.Points[3].addBlackPiece();
    secondBoard.Points[3].addBlackPiece();

    //when
    assertFalse("position points[3] is returning true to a blot, this is because liveBoard.getNumOfBlots(currentPlayer.black) > newBoard.getNumOfBlots(currentPlayer.black)", classUnderTest.hasABlotBeenDoubled(secondBoard));
  }

  /**
   * piece been bore = a player has moved from the board to bearing area
   */
  @Test
  public void testingThatBearingAPieceThenCallingHasAHasBeenBoreReturnsTrue() {
    secondBoard.setBlackBore(1);
    assertTrue("Piece has not been bore",classUnderTest.hasAPieceBeenBore(secondBoard));
  }

  /**
   * piece been bore = a player has moved from the board to bearing area
   */
  @Test
  public void testingThatMovingAPieceNotToBearThenCallingHasAHasBeenBoreReturnsTrue() {
    secondBoard.Points[3].addBlackPiece();
    assertFalse("Piece has been bore",classUnderTest.hasAPieceBeenBore(secondBoard));
  }


  /**
   * Enemy player has a piece on its zero section = a piece has been taken
   */
  @Test
  public void testingThatTakingAnEnemyPieceAndCallingHasAPieceHasBeenTakenReturnsTrue() {
    secondBoard.Points[0].addBlackPiece();
    assertTrue("Hasn't recognized you taking an oppositions piece",classUnderTest.hasAPieceBeenTaken(secondBoard));
  }

  /**
   * Enemy player has a piece on its zero section = a piece has been taken
   */
  @Test
  public void testingThatMovingAPieceAndCallingHasAPieceHasBeenTakenReturnsFalse() {
    secondBoard.Points[4].addBlackPiece();
    assertFalse("Has counted moving a piece as taking a piece",classUnderTest.hasAPieceBeenTaken(secondBoard));
  }

  /**
   * Enemy player has a piece on its zero section = a piece has been taken
   */
  @Test
  public void testingThatTakingMyOwnPieceAndCallingHasAPieceHasBeenTakenReturnsFalse() {
    secondBoard.Points[25].addRedPiece();
    assertFalse("Is counting taking your own piece as a positive",classUnderTest.hasAPieceBeenTaken(secondBoard));
  }

  /**
   * moving a piece solo and then checking it has registered
   */
  @Test
  public void testingThatAPieceHasBeenMovedSoloPositive() {
    secondBoard.Points[7].addBlackPiece();
    assertTrue(classUnderTest.hasAPieceBeenMovedSolo(secondBoard));
  }

  /**
   * Testing if a piece that can bear has been spread, increasing the chances of getting a correct roll
   */
  @Test
  public void testingHasAPieceBeenSpreadPositive() {
    throw new RuntimeException();
  }


  /**
   * Moving a piece to another point with pieces frm the same player
   */
  @Test
  public void testingHasAStackBeenAddedToPositive() {
    secondBoard.Points[13].addBlackPiece();
    assertTrue("a piece wasn't stacked on position 13", classUnderTest.hasAStackBeenAddedTo(secondBoard));
  }

  /**
   *
   */
  @Test
  public void testingIfTheOpponentHasBeenBlocked() {
    throw new RuntimeException();
  }

  @Test
  public void testingInitialMove() {
    throw new RuntimeException();
  }


}
