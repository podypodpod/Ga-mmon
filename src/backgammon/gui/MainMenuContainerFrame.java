package backgammon.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainMenuContainerFrame extends JFrame implements Runnable{
	private static final long serialVersionUID = 1L;

	public MainMenuPanel mmp;
	JPanel mainPanel;
	JFrame frame;

	@Override
	public void run() {
		createGUI();
	}
	
	public void createGUI(){
		
		//Creating JFrame and panel
		frame = new JFrame("GA-mmon");
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		//creating and adding the Panel
		mmp = new MainMenuPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		mainPanel.add(mmp, c);

		frame.add(mainPanel);
		frame.setSize(1166,600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}