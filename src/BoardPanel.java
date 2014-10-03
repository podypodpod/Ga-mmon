import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;


public class BoardPanel extends JPanel{
	
	Ellipse2D.Float[] redCheckers = new Ellipse2D.Float[15];
	Ellipse2D.Float[] blackCheckers = new Ellipse2D.Float[15];
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BoardPanel(){
		for(int i=0;i<redCheckers.length;i++){
			redCheckers[i] = new Ellipse2D.Float(0,0,0,0);
		}
		for(int i=0;i<blackCheckers.length;i++){
			blackCheckers[i] = new Ellipse2D.Float(0,0,0,0);
		}
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		Graphics2D g2 = (Graphics2D) g;

		//Drawing background
		Image imgBG = Toolkit.getDefaultToolkit().getImage("images/Backgammon blank board2.png");
	    g2.drawImage(imgBG, 0, 0, this);
	    
	    //Drawing Red checkers
	    g2.setColor(Color.RED);
	    for(int i = 0; i<redCheckers.length;i++){
	    	g2.fill(redCheckers[i]);
	    }
	    //Drawing Black checkers
	    g2.setColor(Color.BLACK);
	    for(int i = 0; i<blackCheckers.length;i++){
	    	g2.fill(blackCheckers[i]);
	    }
		
	}
	
	public void printCheckers(int point, int amount, boolean color){
/**
		//if its one of the 0 points
		if(point==0||point==25){
			
		//if its the top half of the board
		}else if(point<25&&point>0){
			
		//if its the bottom half
		}else{
			
		}
*/
	}
	
	
}
