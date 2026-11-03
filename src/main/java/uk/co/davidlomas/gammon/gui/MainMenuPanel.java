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
		final Thread search = searchThread(whenDone);
		search.start();
		return search;
	}

	/**
	 * The thread a search runs on, not yet started, so tests can make it a daemon before starting it. The real one
	 * isn't a daemon, so the program waits for a search to finish rather than cutting it off mid-save
	 *
	 * @param whenDone run on the Swing thread once the search has finished or failed
	 *
	 * @return the thread, not started
	 */
	static Thread searchThread(final Runnable whenDone) {
		return new Thread(() -> {
			try {
				StartGa.main(new String[0]);
			} catch (final Throwable e) {
				// any failure is reported, including Errors such as a settings class failing to load
				logger.error("The search failed", e);
				if (!GraphicsEnvironment.isHeadless()) {
					SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
							"The search failed: " + e, "GA-mmon", JOptionPane.ERROR_MESSAGE));
				}
				if (e instanceof Error) {
					throw (Error) e;
				}
			} finally {
				SwingUtilities.invokeLater(whenDone);
			}
		}, "GA search");
	}
}
