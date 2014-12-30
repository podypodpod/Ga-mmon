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

		//Stops it multithreading
		try {
			guiThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public void playIndividualsVsEachOther(Individual ip1, Individual ip2){

		if(GameSettings.getDisplayGUI()){
			startGui();
		}

		//The Game Thread
		currentGame = new Game(ip1, ip2);
		Thread gameThread = new Thread(currentGame);
		gameThread.start();

		//stops it multithreading and ruining the GUI
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}