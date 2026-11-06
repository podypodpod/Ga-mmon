package uk.co.davidlomas.gammon.gui;

import javax.swing.*;
import java.awt.*;

/**
 * ContainerFrame
 * <p>
 * contains the other frames
 * <p>
 * Run it on the Swing thread, with SwingUtilities.invokeAndWait, so bp is ready when it returns
 *
 * @author David Lomas
 */
public class BoardContainerFrame extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;

	public BoardPanel bp;

	private void createGUI() {

		// Setting up this frame and its panel
		setTitle("GA-mmon");
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		// creating and adding the Board Panel
		bp = new BoardPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		mainPanel.add(bp, c);

		add(mainPanel);
		setSize(1166, 600);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void run() {
		createGUI();
	}
}