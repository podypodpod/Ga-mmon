package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveGeneratorTest {

	private Board board;
	private AiPlayer player1, player2;
	private MoveGenerator moveGenerator;

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
		player1 = new AiPlayer(true, null);
		player2 = new AiPlayer(false, null);
		moveGenerator = new MoveGenerator(null);
	}

	@Test
	public void callingGenerateMovesOnDefaultBoardWithValidMovesForBlackShouldReturnBoards() {
		// Given
		board.setStartPosition();
		player1.movesLeft.add(3);
		player1.movesLeft.add(5);

		// When
		moveGenerator.generateMoves(board, player1);

		// Then
		assertTrue(moveGenerator.getBoardList().hasContents());
	}

	@Test
	public void callingGenerateMovesOnDefaultBoardWithValidMovesForRedShouldReturnBoards() {
		// Given
		board.setStartPosition();
		player2.movesLeft.add(1);

		// When
		moveGenerator.generateMoves(board, player2);

		// Then
		assertTrue(moveGenerator.getBoardList().hasContents());
	}

	@Test
	public void callingGenerateMovesOnABoardWithNoMovesShouldReturnAnEmptyList() {
		// Given
		board.setStartPosition();
		player2.movesLeft.clear();

		// When
		moveGenerator.generateMoves(board, player2);

		// Then
		assertFalse(moveGenerator.getBoardList().hasContents());
	}
}
