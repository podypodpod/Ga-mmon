package backgammon.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * ContainerFrame
 * 
 * contains the other frames
 * 
 * This is not currently needed, I could have put this in to the board panel class
 * but Its here for future changes if I add buttons to the GUI it will go in to another class
 * 
 * @author David Lomas - 11035527
 */
public class ContainerFrame extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;
	
	
	//GUI stuff
	public BoardPanel bp;
	JPanel mainPanel;
	JFrame frame;
	
	/**
	 * cons
	 */
	public ContainerFrame(){	
		
	}


	@Override
	public void run() {
		createGUI();
	}
	
	/**
	 * Creates the gUI
	 */
	public void createGUI(){
		
		//Creating JFrame and panel
		frame = new JFrame("GA-mmon");
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		//creating and adding the Board Panel
		bp = new BoardPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		mainPanel.add(bp, c);

		frame.add(mainPanel);
		frame.setSize(1166,600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}