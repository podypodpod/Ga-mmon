package backgammon.game;

/**
 * The Main class
 * 
 */
public class Main {

	/**
	 * The main method.
	 * 
	 *  uses the game manager to start a list of games etc
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		GameManager gm = new GameManager();
		
		gm.setNumerOfGames(3);
		
		gm.startGames();
	}
	

}
