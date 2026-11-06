package uk.co.davidlomas.gammon.gui;

import javax.swing.*;
import java.awt.*;

public class MainMenuContainerFrame extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;

	public MainMenuPanel mmp;

	public static void main(final String[] args) {
		// all of it on the Swing thread, the frame being made as well as shown
		SwingUtilities.invokeLater(() -> new MainMenuContainerFrame().run());
	}

	public void createGUI() {

		// Setting up this frame and its panel
		setTitle("GA-mmon");
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		// creating and adding the Panel
		mmp = new MainMenuPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		mainPanel.add(mmp, c);

		add(mainPanel);
		setSize(1166, 600);
		// not exiting, which wouldn't wait for a search still running: the program ends once that has finished
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void run() {
		createGUI();
	}
}
