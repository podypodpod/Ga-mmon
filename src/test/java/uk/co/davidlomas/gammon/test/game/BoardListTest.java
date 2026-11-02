package uk.co.davidlomas.gammon.test.game;

import org.junit.*;
import uk.co.davidlomas.gammon.game.AiPlayer;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.BoardList;
import uk.co.davidlomas.gammon.game.MoveGenerator;
import uk.co.davidlomas.gammon.game.Point;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.IndividualAttribute;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.lang.reflect.MalformedParametersException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
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

	@Before
	public void setup() {
		individual = new Individual();
		player = new AiPlayer(true, individual);
		classUnderTest = new BoardList(individual);
		board = new Board();
	}

	@After
	public void tearDown() {
		RandomSource.reset();
	}

	/**
	 * A player with bearAPiece at 100 should always bear off when it can, whatever the random numbers
	 */
	@Test
	public void playerWithMaxBearAPieceAlwaysBearsOffWhenItCan() {
		for (int seed = 0; seed < 50; seed++) {
			RandomSource.setSeed(seed);

			final Board chosenBoard = chooseEndGameMove(100, 0);

			assertTrue("Didn't bear off with seed " + seed, chosenBoard.getBlackBore() > 0);
		}
	}

	/**
	 * A player with bearAPiece and spreadAHomePiece at 0 never looks for a bear off, so it falls back to a
	 * random move, which sometimes still bears off and sometimes doesn't
	 */
	@Test
	public void playerWithZeroBearAPieceFallsBackToARandomMove() {
		boolean boreOff = false;
		boolean didNotBearOff = false;

		for (int seed = 0; seed < 50; seed++) {
			RandomSource.setSeed(seed);

			final Board chosenBoard = chooseEndGameMove(0, 0);

			if (chosenBoard.getBlackBore() > 0) {
				boreOff = true;
			} else {
				didNotBearOff = true;
			}
		}

		assertTrue("Never bore off with a random move", boreOff);
		assertTrue("Always bore off with a random move", didNotBearOff);
	}

	/**
	 * Sets up an end game board where black can bear off, rolls a 1 and a 2 and lets the black player choose a move
	 *
	 * @return the board the player chose
	 */
	private Board chooseEndGameMove(final int bearAPiece, final int spreadAHomePiece) {
		final Individual endGameIndividual = new Individual();
		endGameIndividual.setAttribute(0, new IndividualAttribute("bearAPiece", bearAPiece));
		endGameIndividual.setAttribute(5, new IndividualAttribute("spreadAHomePiece", spreadAHomePiece));

		final Board endGameBoard = new Board();
		for (final Point point : endGameBoard.Points) {
			point.setBlackCount(0);
			point.setRedCount(0);
		}
		// black has every piece in its home quarter, one of them can bear off with the 1
		endGameBoard.Points[1].setBlackCount(1);
		endGameBoard.Points[6].setBlackCount(3);
		// red is well out of the way
		endGameBoard.Points[20].setRedCount(2);

		final AiPlayer endGamePlayer = new AiPlayer(true, endGameIndividual);
		endGamePlayer.movesLeft.add(1);
		endGamePlayer.movesLeft.add(2);

		return new MoveGenerator(endGameIndividual).getNextMoveBoard(endGameBoard, endGamePlayer);
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

		assertThrows(MalformedParametersException.class,
				() -> classUnderTest.addBoardWithNextMove(board, 0, 0, player, new MoveGenerator(individual)));
	}
}
