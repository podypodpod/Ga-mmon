package uk.co.davidlomas.gammon.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.genes.StartGa;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MainMenuPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final static Logger logger = LoggerFactory.getLogger(MainMenuPanel.class);

	public MainMenuPanel() {
		this(MainMenuPanel::startSearch);
	}

	/**
	 * @param startSearch starts a search, and is given what to run when it ends. Tests pass their own, so they can
	 *                    see the button pressed without running the GA
	 */
	MainMenuPanel(final Consumer<Runnable> startSearch) {
		setLayout(new GridLayout());

		final JButton startSearchButton = new JButton("Start Search");

		// one search at a time: they share the settings and the attempt count, so two would save over each other
		startSearchButton.addActionListener(e -> {
			startSearchButton.setEnabled(false);
			startSearch.accept(() -> startSearchButton.setEnabled(true));
		});

		this.add(startSearchButton);

		final JButton playBestTwo = new JButton("Play Best Two");
		this.add(playBestTwo);

		final JButton changeSettings = new JButton("Change Settings");
		this.add(changeSettings);

	}

	/**
	 * Runs the genetic algorithm on its own thread, so the window keeps responding while it searches. If the search
	 * fails, the error is shown in a message box rather than only in the log
	 *
	 * @param whenDone run on the Swing thread once the search has finished or failed
	 *
	 * @return the thread it's running on
	 */
	static Thread startSearch(final Runnable whenDone) {
		final Thread search = new Thread(() -> {
			try {
				StartGa.main(new String[0]);
			} catch (final RuntimeException e) {
				logger.error("The search failed", e);
				if (!GraphicsEnvironment.isHeadless()) {
					SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
							"The search failed: " + e, "GA-mmon", JOptionPane.ERROR_MESSAGE));
				}
			} finally {
				SwingUtilities.invokeLater(whenDone);
			}
		}, "GA search");
		// so a search still running can't keep the program open once the window has gone
		search.setDaemon(true);
		search.start();
		return search;
	}
}
