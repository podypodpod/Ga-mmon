package uk.co.davidlomas.gammon.gui;

import javax.swing.*;
import java.awt.Dimension;

public class MainMenuContainerFrame extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;

	public MainMenuPanel mmp;

	public static void main(final String[] args) {
		// all of it on the Swing thread, the frame being made as well as shown
		SwingUtilities.invokeLater(() -> new MainMenuContainerFrame().run());
	}

	public void createGUI() {

		setTitle("GA-mmon");
		mmp = new MainMenuPanel();
		add(mmp);
		setSize(620, 640);
		setMinimumSize(new Dimension(420, 600));
		setLocationRelativeTo(null);
		// not exiting, which wouldn't wait for a search still running: the program ends once that has finished
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void run() {
		createGUI();
	}
}
