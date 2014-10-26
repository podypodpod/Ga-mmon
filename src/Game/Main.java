package Game;
import javax.swing.JFrame;

/**
 * The Class Main.
 * 
 * 
 */
public class Main {
public static BoardPanel bp;
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		JFrame frame = new JFrame("GAMMON BACK");
		bp = new BoardPanel();
		frame.add(bp);
		frame.setSize(1169,637);
		frame.setVisible(true);
		
		@SuppressWarnings("unused")
		Game game = new Game();
	}
	

}
