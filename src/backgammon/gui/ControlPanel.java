package backgammon.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ControlPanel extends JPanel implements ActionListener {
	
	JComboBox<String> toBox, fromBox;
	JLabel fromLbl, toLbl, imgLbl, fillerLbl;
	JButton moveBtn, concedeBtn, skipBtn;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControlPanel(){
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.HORIZONTAL;
		setSize(100,100);
		setBackground(Color.BLACK);
		
		String[] values = {"0","1","2","3","4","5","Bear"};
		
		//Labels
		fillerLbl = new JLabel("          ");
		fillerLbl.setForeground(Color.WHITE);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.1;
		c.weighty = 0.1;
		this.add(fillerLbl,c);
		
		fromLbl = new JLabel("From: ");
		fromLbl.setForeground(Color.WHITE);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.1;
		c.weighty = 0.1;
		this.add(fromLbl,c);
		
		toLbl = new JLabel("To: ");
		toLbl.setForeground(Color.WHITE);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.1;
		c.weighty = 0.1;
		this.add(toLbl,c);
		
		//JComboBoxes
		fromBox = new JComboBox<String>(values);
		fromBox.setSelectedIndex(1);
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.1;
		c.weighty = 0.1;
		this.add(fromBox,c);
		
		toBox = new JComboBox<String>(values);
		toBox.setSelectedIndex(1);
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0.1;
		c.weighty = 0.1;
		this.add(toBox,c);
		
		
	    //BUTTONS
	    moveBtn = new JButton("Move peice");
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.1;
		c.weighty = 0.1;
		moveBtn.addActionListener(this);
	    this.add(moveBtn,c);
	    
	    concedeBtn = new JButton("Concede Defeat");
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 0.1;
		c.weighty = 0.1;
		concedeBtn.addActionListener(this);
	    this.add(concedeBtn,c);
	    
	    skipBtn = new JButton("Skip Go");
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 0.1;
		c.weighty = 0.1;
		skipBtn.addActionListener(this);
	    this.add(skipBtn,c);
	    
	    Image image1 = Toolkit.getDefaultToolkit().getImage("images/Gammon.png");
	    imgLbl  = new JLabel(new ImageIcon(image1));
		c.gridx = 1;
		c.gridy = 5;
		c.weightx = 0.1;
		c.weighty = 0.1;
	    this.add(imgLbl,c);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == moveBtn){
			System.out.println("Moving peice from: "+fromBox.getSelectedIndex()+ " to " + toBox.getSelectedIndex());
		}else if(e.getSource() == concedeBtn){
			
		}else if(e.getSource() == skipBtn){
			
		}
		
	}

}
