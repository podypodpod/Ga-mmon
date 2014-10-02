import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;


public class GUI extends JPanel{

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		Graphics2D g2 = (Graphics2D) g;
	
		Image imgBG = Toolkit.getDefaultToolkit().getImage("images/Backgammon blank board2.png");
	    g2.drawImage(imgBG, 0, 0, this);
	    g2.finalize();
		
	}
	
	
}
