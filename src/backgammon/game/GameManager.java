package backgammon.game;

import backgammon.gui.ContainerFrame;
import backgammon.genes.Individual;
import backgammon.settings.GameSettings;

public class GameManager {

	public static ContainerFrame containerFrame;
	private static Game currentGame;

	public GameManager(){
		
	}


	public void startGui(){
		//The Gui Thread
		containerFrame = new ContainerFrame();
		Thread guiThread = new Thread(containerFrame);
		guiThread.start();

		//Stops it multi-threading
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
		
		/*
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