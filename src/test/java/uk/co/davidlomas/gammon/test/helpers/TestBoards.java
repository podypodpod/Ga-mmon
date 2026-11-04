package uk.co.davidlomas.gammon.test.helpers;

import uk.co.davidlomas.gammon.game.Board;

/**
 * Boards for tests to set up positions on
 */
public class TestBoards {

	public static Board empty() {
		final Board board = new Board();
		board.removeAllPieces();
		return board;
	}
}
