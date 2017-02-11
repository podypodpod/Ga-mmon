package uk.co.davidlomas.gammon.test.game;

import org.junit.*;
import org.junit.rules.ExpectedException;
import uk.co.davidlomas.gammon.game.AiPlayer;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.BoardList;
import uk.co.davidlomas.gammon.game.MoveGenerator;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import java.lang.reflect.MalformedParametersException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardListTest {

    private AiPlayer player;
    private BoardList classUnderTest;
    private Board board;
    private Individual individual;

    @BeforeClass
    public static void beforeClass() {
        SettingsUtil.resetSettings();
    }

    @AfterClass
    public static void afterClass() {
        SettingsUtil.resetSettings();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Before
    public void setup() {
        individual = new Individual();
        player = new AiPlayer(true, individual);
        classUnderTest = new BoardList(individual);
        board = new Board();
    }

    /**
     * Testing adding a blank board to the board list
     */
    @Test
    public void testingAddingABoardObjectToTheBoardList() {
        assertFalse(classUnderTest.hasContents());

        classUnderTest.addBoardWithNextMove(board);

        assertTrue(classUnderTest.hasContents());
    }

    /**
     * Testing that when adding a board to the list with a next move
     */
    @Test
    public void testingAddingABoardWithNextMoveToTheBoardList() {
        assertFalse(classUnderTest.hasContents());

        player.movesLeft.add(4);
        classUnderTest.addBoardWithNextMove(board, 1, 4, player, new MoveGenerator(individual));

        assertTrue(classUnderTest.hasContents());
    }

    /**
     * Testing that when adding a board to the list with a next move, without the moves list to fulfill the move, it throws an exception
     */
    @Test
    public void testingAddingABoardWithNextMoveToTheBoardListWithNoMovesLeft() {
        assertFalse(classUnderTest.hasContents());

        exception.expect(MalformedParametersException.class);
        classUnderTest.addBoardWithNextMove(board, 0, 0, player, new MoveGenerator(individual));

        assertTrue(classUnderTest.hasContents());
    }
}
