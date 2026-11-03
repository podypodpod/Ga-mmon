package uk.co.davidlomas.gammon.test.helpers;

import uk.co.davidlomas.gammon.game.Board;

/**
 * Turns a board round so black's position becomes red's, so a test written for black can check red too.
 * <p>
 * Black moves from 24 down to 1 with its bar on 25; red is the mirror image, moving from 1 up to 24 with its bar
 * on 0. So point p becomes 25 - p and the colours swap.
 */
public class BoardMirror {

	public static Board mirror(final Board board) {
		final Board mirrored = new Board();
		for (int point = 0; point < board.Points.length; point++) {
			mirrored.Points[25 - point].setBlackCount(board.Points[point].getRedCount());
			mirrored.Points[25 - point].setRedCount(board.Points[point].getBlackCount());
		}
		mirrored.setBlackBore(board.getRedBore());
		mirrored.setRedBore(board.getBlackBore());
		return mirrored;
	}
}
