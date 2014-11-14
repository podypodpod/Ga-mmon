package backgammon.game;

import backgammon.gui.ContainerFrame;

/**
 * The Main class
 * 
 * Creates 2 threads, one for the gui and one for the game loop
 * 
 */
public class Main {

	public static ContainerFrame containerFrame;
	static Game currentGame;
	
	/**
	 * The main method.
	 * 
	 *  Creates 2 threads, one for the gui and one for the game loop
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		//The Gui Thread
		containerFrame = new ContainerFrame();
		Thread guiThread = new Thread(containerFrame);
		guiThread.start();
		try {
			guiThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		//The Game Thread
		currentGame = new Game();
		Thread gameThread = new Thread(currentGame);
		gameThread.start();
		try {
			guiThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
