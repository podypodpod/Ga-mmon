package backgammon.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ContainerFrame extends JFrame implements Runnable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	//GUI stuff
	public BoardPanel bp;
	ControlPanel cp;
	JPanel mainPanel;
	JFrame frame;
	
	
	public ContainerFrame(){	
		
	}


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

		
		//creating and adding the Board Panel
		bp = new BoardPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		mainPanel.add(bp, c);
		
		//creating and adding the control panel
		cp = new ControlPanel();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;
		mainPanel.add(cp,c);
		
		frame.add(mainPanel);
		frame.setSize(1355,637);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
