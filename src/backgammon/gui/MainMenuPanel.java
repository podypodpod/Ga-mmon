package backgammon.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import backgammon.genes.StartGa;

public class MainMenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public MainMenuPanel() {
		this.setLayout(new GridLayout());

		JButton startSearch = new JButton("Start Search");
		startSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new StartGa();
			}
		});
		this.add(startSearch);

		JButton playBestTwo = new JButton("Play Best Two");
		this.add(playBestTwo);

		JButton changeSettings = new JButton("Change Settings");
		this.add(changeSettings);

	}
}