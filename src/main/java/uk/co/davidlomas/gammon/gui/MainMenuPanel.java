package uk.co.davidlomas.gammon.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import uk.co.davidlomas.gammon.genes.StartGa;

public class MainMenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public MainMenuPanel() {
		setLayout(new GridLayout());

		final JButton startSearch = new JButton("Start Search");

		startSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				new StartGa();
			}
		});

		this.add(startSearch);

		final JButton playBestTwo = new JButton("Play Best Two");
		this.add(playBestTwo);

		final JButton changeSettings = new JButton("Change Settings");
		this.add(changeSettings);

	}
}