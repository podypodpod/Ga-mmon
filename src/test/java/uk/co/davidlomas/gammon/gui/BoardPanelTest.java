package uk.co.davidlomas.gammon.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Draws the board panel into an image, which needs no screen, and checks the colour where checkers should be
 */
public class BoardPanelTest {

	// inside the checker drawn on each spot, away from the count written on it
	private static final int[] POINT_SIX = {580, 100};
	private static final int[] POINT_ONE = {930, 100};
	private static final int[] POINT_TWO = {860, 100};
	private static final int[] BLACK_BORNE_OFF = {1060, 165};
	private static final int[] RED_BORNE_OFF = {1060, 405};

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void beforeAnyBoardArrivesItDrawsTheEmptyBoard() {
		final BufferedImage drawn = draw(new BoardPanel());

		assertNotEquals(Color.BLACK.getRGB(), colourAt(drawn, POINT_SIX));
		assertNotEquals(Color.RED.getRGB(), colourAt(drawn, POINT_ONE));
	}

	@Test
	public void eachPlayersCheckersAreDrawnOnTheirPoints() {
		final BoardPanel panel = new BoardPanel();
		panel.printCheckers(new Board().Points, 0, 0);

		final BufferedImage drawn = draw(panel);

		// the start position has black on 6, red on 1 and nothing on 2
		assertEquals(Color.BLACK.getRGB(), colourAt(drawn, POINT_SIX));
		assertEquals(Color.RED.getRGB(), colourAt(drawn, POINT_ONE));
		assertEquals(colourAt(draw(new BoardPanel()), POINT_TWO), colourAt(drawn, POINT_TWO));
	}

	@Test
	public void piecesBorneOffAreDrawnOnlyOnceThereAreSome() {
		final BoardPanel panel = new BoardPanel();
		panel.printCheckers(new Board().Points, 0, 3);

		final BufferedImage drawn = draw(panel);

		assertEquals(Color.BLACK.getRGB(), colourAt(drawn, BLACK_BORNE_OFF));
		assertEquals(colourAt(draw(new BoardPanel()), RED_BORNE_OFF), colourAt(drawn, RED_BORNE_OFF));
	}

	private static BufferedImage draw(final BoardPanel panel) {
		final BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics = image.createGraphics();
		try {
			panel.paintComponent(graphics);
		} finally {
			graphics.dispose();
		}
		return image;
	}

	private static int colourAt(final BufferedImage image, final int[] spot) {
		return image.getRGB(spot[0], spot[1]);
	}
}
