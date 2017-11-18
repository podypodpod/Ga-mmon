package uk.co.davidlomas.gammon.gui;

import uk.co.davidlomas.gammon.genes.StartGa;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public MainMenuPanel() {
		setLayout(new GridLayout());

		final JButton startSearch = new JButton("Start Search");

		startSearch.addActionListener(e -> new StartGa());

		this.add(startSearch);

		final JButton playBestTwo = new JButton("Play Best Two");
		this.add(playBestTwo);

		final JButton changeSettings = new JButton("Change Settings");
		this.add(changeSettings);

	}
}