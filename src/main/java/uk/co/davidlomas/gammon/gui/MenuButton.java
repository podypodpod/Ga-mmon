package uk.co.davidlomas.gammon.gui;

import javax.swing.*;
import java.awt.*;

/**
 * A button in the main menu's colours. It paints itself, as the look and feel Swing uses on some systems ignores a
 * button's background and picks its own colour for a disabled button's text
 */
class MenuButton extends JButton {

	private static final long serialVersionUID = 1L;

	private static final int ARC = 12;
	private static final Dimension SIZE = new Dimension(280, 50);

	MenuButton(final String text) {
		super(text);
		setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFocusPainted(false);
		setOpaque(false);
		setRolloverEnabled(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setPreferredSize(SIZE);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		setCursor(Cursor.getPredefinedCursor(enabled ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
	}

	@Override
	protected void paintComponent(final Graphics graphics) {
		final Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		final int width = getWidth() - 1;
		final int height = getHeight() - 1;
		if (isEnabled()) {
			g.setColor(fill());
			g.fillRoundRect(0, 0, width, height, ARC, ARC);
		} else {
			g.setColor(MainMenuPanel.MUTED);
			g.drawRoundRect(0, 0, width, height, ARC, ARC);
		}
		if (isFocusOwner()) {
			g.setColor(MainMenuPanel.ACCENT);
			g.setStroke(new BasicStroke(2));
			g.drawRoundRect(2, 2, width - 4, height - 4, ARC, ARC);
		}

		g.setFont(getFont());
		g.setColor(isEnabled() ? MainMenuPanel.FELT : MainMenuPanel.MUTED);
		final FontMetrics metrics = g.getFontMetrics();
		g.drawString(getText(), (getWidth() - metrics.stringWidth(getText())) / 2,
				(getHeight() - metrics.getHeight()) / 2 + metrics.getAscent());
		g.dispose();
	}

	private Color fill() {
		if (getModel().isPressed()) {
			return MainMenuPanel.CREAM.darker();
		}
		return getModel().isRollover() ? Color.WHITE : MainMenuPanel.CREAM;
	}
}
