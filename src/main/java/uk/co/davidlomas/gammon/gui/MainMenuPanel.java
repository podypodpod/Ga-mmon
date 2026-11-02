package uk.co.davidlomas.gammon.gui;

import uk.co.davidlomas.gammon.genes.StartGa;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public MainMenuPanel() {
		this(MainMenuPanel::startSearch);
	}

	/**
	 * @param startSearch what the Start Search button does, so tests can see it's pressed without running the GA
	 */
	MainMenuPanel(final Runnable startSearch) {
		setLayout(new GridLayout());

		final JButton startSearchButton = new JButton("Start Search");

		startSearchButton.addActionListener(e -> startSearch.run());

		this.add(startSearchButton);

		final JButton playBestTwo = new JButton("Play Best Two");
		this.add(playBestTwo);

		final JButton changeSettings = new JButton("Change Settings");
		this.add(changeSettings);

	}

	/**
	 * Runs the genetic algorithm on its own thread, so the window keeps responding while it searches
	 *
	 * @return the thread it's running on
	 */
	static Thread startSearch() {
		final Thread search = new Thread(() -> StartGa.main(new String[0]), "GA search");
		search.start();
		return search;
	}
}