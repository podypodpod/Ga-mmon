package backgammon.game;

import backgammon.gui.ContainerFrame;
import backgammon.genes.Individual;
import backgammon.settings.GameSettings;

/**
 * GameManager
 * 
 * Allows the starting of games and returns the results of games without having to deal with all the actual methods etc in the game class
 * 
 * this class will be called when testing players etc, you can just make an object and then pass it two individuals to the method to play a game and return results.
  * 
 * @author David Lomas - 11035527
 */
public class GameManager {

	public static ContainerFrame containerFrame;
	private static Game currentGame;

	public GameManager(){
		
	}

	/**
	 * startGui
	 * 
	 * starts the GUI, shouldn't be called outside of this class
	 */
	private void startGui(){
		//The Gui Thread
		containerFrame = new ContainerFrame();
		Thread guiThread = new Thread(containerFrame);
		guiThread.start();

		//Stops it multi-threading,
		try {
			if(!GameSettings.getMultiThreading()){guiThread.join();};
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * playIndividualsVsEachOther
	 * 
	 * @param ip1 indiv 1
	 * @param ip2 indiv 2
	 * @return GameStats result of game (winner, values)
	 */
	public GameStats playIndividualsVsEachOther(Individual ip1, Individual ip2){
		
		if(GameSettings.getDisplayGUI()){
			startGui();
		}

		//The Game Thread
		currentGame = new Game(ip1, ip2);
		
		/* THREADING NOT IMPLEMENTED YET
		Thread gameThread = new Thread(currentGame);
		gameThread.start();
		//stops it multi-threading and ruining the GUI
		try {
			if(!GameSettings.getMultiThreading()){gameThread.join();};
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
		currentGame.run();
		return currentGame.getGameStats();
	}
}