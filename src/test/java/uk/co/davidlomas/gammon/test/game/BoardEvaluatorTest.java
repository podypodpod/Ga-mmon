package uk.co.davidlomas.gammon.test.game;

import org.junit.*;
import uk.co.davidlomas.gammon.game.AiPlayer;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.BoardEvaluator;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static junit.framework.TestCase.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link BoardEvaluator}
 */
public class BoardEvaluatorTest {

	/**
	 * Before class
	 */
	@BeforeClass
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	/**
	 * After class
	 */
	@AfterClass
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	/** Class under test */
	private BoardEvaluator classUnderTest;

	/** Starting board */
	private Board startingBoard;

	/** Second board */
	private Board secondBoard;

	@Before
	public void setup() {
		classUnderTest = new BoardEvaluator();
		startingBoard = new Board();
		secondBoard = new Board();
		final AiPlayer aiPlayer = new AiPlayer(true, new Individual());

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
	 * Checking that a {@link BoardEvaluator#hasABlotBeenDoubled(Board)} returns false when the point goes from empty to 2 pieces in one move
	 * <p>
	 * Blot = single piece that could be taken
	 * Double = second piece on top, now safe
	 */
	@Test
	public void testingWhenWePutTwoPiecesOnAnEmptyPointThenHasABlotBeenDoubledReturnsFalse() {
		// GIVEN a starting board with no peices on point 3
		assertThat(startingBoard.Points[3].numEither()).as("point 3").isEqualTo(0);

		// WHEN we add two black pieces to point 3
		secondBoard.Points[3].addBlackPiece();
		secondBoard.Points[3].addBlackPiece();

		// THEN it returns false
		assertThat(classUnderTest.hasABlotBeenDoubled(secondBoard)).as("has a blot been doubled").isFalse();
	}

	/**
	 * piece been bore = a player has moved from the board to bearing area
	 */
	@Test
	public void testingThatBearingAPieceThenCallingHasAHasBeenBoreReturnsTrue() {
		secondBoard.setBlackBore(1);
		assertTrue("Piece has not been bore", classUnderTest.hasAPieceBeenBore(secondBoard));
	}

	/**
	 * piece been bore = a player has moved from the board to bearing area
	 */
	@Test
	public void testingThatMovingAPieceNotToBearThenCallingHasAHasBeenBoreReturnsTrue() {
		secondBoard.Points[3].addBlackPiece();
		assertFalse("Piece has been bore", classUnderTest.hasAPieceBeenBore(secondBoard));
	}


	/**
	 * Enemy player has a piece on its zero section = a piece has been taken
	 */
	@Test
	public void testingThatTakingAnEnemyPieceAndCallingHasAPieceHasBeenTakenReturnsTrue() {
		secondBoard.Points[0].addBlackPiece();
		assertTrue("Hasn't recognized you taking an oppositions piece", classUnderTest.hasAPieceBeenTaken(secondBoard));
	}

	/**
	 * Enemy player has a piece on its zero section = a piece has been taken
	 */
	@Test
	public void testingThatMovingAPieceAndCallingHasAPieceHasBeenTakenReturnsFalse() {
		secondBoard.Points[4].addBlackPiece();
		assertFalse("Has counted moving a piece as taking a piece", classUnderTest.hasAPieceBeenTaken(secondBoard));
	}

	/**
	 * Enemy player has a piece on its zero section = a piece has been taken
	 */
	@Test
	public void testingThatTakingMyOwnPieceAndCallingHasAPieceHasBeenTakenReturnsFalse() {
		secondBoard.Points[25].addRedPiece();
		assertFalse("Is counting taking your own piece as a positive", classUnderTest.hasAPieceBeenTaken(secondBoard));
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
	 * Moving a piece to another point with pieces frm the same player
	 */
	@Test
	public void testingHasAStackBeenAddedToPositive() {
		secondBoard.Points[13].addBlackPiece();
		assertTrue("a piece wasn't stacked on position 13", classUnderTest.hasAStackBeenAddedTo(secondBoard));
	}

}
