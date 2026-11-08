package uk.co.davidlomas.gammon.gui;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
		setSize(620, 700);
		setMinimumSize(new Dimension(420, 660));
		setLocationRelativeTo(null);
		// not exiting, which wouldn't wait for a search still running: the program ends once that has finished. It has
		// to leave a window showing until then, or there'd be nothing to show a search was still going
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				if (!mmp.showRunningSearch()) {
					dispose();
				}
			}
		});
		setVisible(true);
	}

	@Override
	public void run() {
		createGUI();
	}
}
