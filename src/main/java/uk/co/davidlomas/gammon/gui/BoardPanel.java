package uk.co.davidlomas.gammon.gui;

import uk.co.davidlomas.gammon.game.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * BoardPanel
 *
 * @author David Lomas
 */
public class BoardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	// loaded once, rather than from disk every time the board is drawn
	private static final Image BACKGROUND_IMAGE = loadBackgroundImage();
	private static final int CHECKER_SIZE = 50;

	/**
	 * What to draw. The game hands over a new one for each move, a copy of its board, so a paint never sees the
	 * board half way through a move. Null until the first board arrives, until then only the empty board is drawn
	 */
	private volatile Board drawn = null;

	private static Image loadBackgroundImage() {
		try (InputStream is = BoardPanel.class.getResourceAsStream("/blank_board.png")) {
			if (is == null) {
				throw new IllegalStateException("blank_board.png isn't on the classpath");
			}
			return ImageIO.read(is);
		} catch (final IOException e) {
			throw new UncheckedIOException("Couldn't load the board image", e);
		}
	}

	BoardPanel() {
		setSize(1169, 637);
	}

	/**
	 * Where the checker for a point is drawn, its top left corner. 0 and 25 are the bars, between the two halves
	 *
	 * @param point the point, 0 to 25
	 *
	 * @return the corner of the checker
	 */
	static java.awt.Point checkerCorner(final int point) {
		if (point < 0 || point > 25) {
			throw new IllegalArgumentException("There's no point " + point + " on the board, they go from 0 to 25");
		}
		if (point == 0) {
			return new java.awt.Point(510, 150);
		} else if (point == 25) {
			return new java.awt.Point(510, 300);
		} else if (point < 7) {
			// top right half
			return new java.awt.Point(990 - point * 70, 80);
		} else if (point < 13) {
			// top left half
			return new java.awt.Point(500 - (point - 6) * 70, 80);
		} else if (point < 19) {
			// bottom left half
			return new java.awt.Point(20 + (point - 12) * 70, 430);
		}
		// bottom right half
		return new java.awt.Point(500 + (point - 18) * 70, 430);
	}

	/**
	 * Where the count of borne off checkers is drawn, its top left corner
	 *
	 * @param black the player
	 *
	 * @return the corner of the checker
	 */
	static java.awt.Point borneOffCorner(final boolean black) {
		return black ? new java.awt.Point(1050, 150) : new java.awt.Point(1050, 390);
	}

	@Override
	public void paintComponent(final Graphics graphics) {

		super.paintComponent(graphics);
		setBackground(Color.WHITE);
		final Graphics2D graphics2D = (Graphics2D) graphics;

		// setting font for the drawn strings on the game
		graphics2D.setFont(new Font("default", Font.BOLD, 16));

		// Drawing background
		graphics2D.drawImage(BACKGROUND_IMAGE, 0, 0, this);

		final Board board = drawn;
		if (board == null) {
			return;
		}

		// Drawing checkers on board, skipping the empty points
		for (int i = 0; i < Board.POINTS; i++) {
			if (board.numEither(i) != 0) {
				final Color colour = board.getBlackCount(i) != 0 ? Color.BLACK : Color.RED;
				// the bar's count sits slightly further in
				final int textOffset = i == 0 || i == 25 ? 22 : 20;
				drawChecker(graphics2D, colour, checkerCorner(i), board.numEither(i), textOffset);
			}
		}

		// Drawing checkers that have been bored
		if (board.getBlackBore() != 0) {
			drawChecker(graphics2D, Color.BLACK, borneOffCorner(true), board.getBlackBore(), 20);
		}
		if (board.getRedBore() != 0) {
			drawChecker(graphics2D, Color.RED, borneOffCorner(false), board.getRedBore(), 20);
		}
	}

	private static void drawChecker(final Graphics2D graphics2D, final Color colour, final java.awt.Point corner,
									final int count, final int textOffset) {
		graphics2D.setColor(colour);
		graphics2D.fillOval(corner.x, corner.y, CHECKER_SIZE, CHECKER_SIZE);
		graphics2D.setColor(Color.WHITE);
		graphics2D.drawOval(corner.x, corner.y, CHECKER_SIZE, CHECKER_SIZE);
		graphics2D.drawString(count + "", corner.x + textOffset, corner.y + 30);
	}

	/**
	 * printCheckers
	 * <p>
	 * method to call to print board. It takes a copy, as the game carries on changing its board while this draws
	 */
	public void printCheckers(final Board board) {
		drawn = new Board(board);
		repaint();
	}

}
