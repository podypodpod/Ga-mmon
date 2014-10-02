import javax.swing.JFrame;

/**
 * The Class Main.
 * 
 * 
 */
public class Main {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("GAMMON");
		GUI GUI = new GUI();
		frame.add(GUI);
		frame.setSize(1169,637);
		frame.setVisible(true);
		
		//Game game = new Game();
	}

}
