package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.lang.reflect.MalformedParametersException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardListTest {

	private AiPlayer player;
	private BoardList classUnderTest;
	private Board board;
	private Individual individual;

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@BeforeEach
	public void setup() {
		individual = new Individual();
		player = new AiPlayer(true, individual);
		classUnderTest = new BoardList();
		board = new Board();
	}

	@AfterEach
	public void tearDown() {
		RandomSource.reset();
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
