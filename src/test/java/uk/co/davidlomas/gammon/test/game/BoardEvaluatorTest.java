package uk.co.davidlomas.gammon.test.game;

import org.junit.*;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.BoardEvaluator;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.Assert.assertTrue;

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
    }

    @Test
    public void testingSettingAndGettingTheBoard() {
        classUnderTest.setBoard(startingBoard);
        Assert.assertEquals(classUnderTest.getBoard(), startingBoard);
    }

    /**
     * blot = single piece that could be taken
     * double = second piece on top, now safe
     */
    @Test
    public void testingHasABlotBeenDoubledPositive() {
        secondBoard.Points[2].addBlackPiece();
        assertTrue(classUnderTest.hasABlotBeenDoubled(secondBoard));
    }

    /**
     * piece been bore = a player has moved from the board to bearing area
     */
    @Test
    public void testingThatAPieceHasBeenBorePositive() {
        secondBoard.setBlackBore(1);
        assertTrue(classUnderTest.hasAPieceBeenBore());
    }

    /**
     * Enemy player has a piece on its zero section = a piece has been taken
     */
    @Test
    public void testingThatAPieceHasBeenTakenPositive() {
        //TODO: couldnt test so not sure if right, not sure which 0 0 is black etc
        secondBoard.Points[0].addBlackPiece();
        assertTrue(classUnderTest.hasAPieceBeenTaken(secondBoard));
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
        secondBoard.Points[2].addBlackPiece();
        assertTrue(classUnderTest.hasAStackBeenAddedTo());
    }

    /**
     *
     */
    @Test
    public void testingIfTheOpponentHasBeenBlocked() {
        //TODO: Not sure what i need to do
        throw new RuntimeException();
    }

    @Test
    public void testingInitialMove() {
        throw new RuntimeException();
    }


}
