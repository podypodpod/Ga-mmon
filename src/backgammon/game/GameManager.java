package backgammon.game;

import backgammon.gui.ContainerFrame;
import backgammon.settings.GameSettings;

public class GameManager {

	public static ContainerFrame containerFrame;
	static Game currentGame;
	private int numOfGames = 0;
	
	public GameManager(){	
				
	}
	
	public void setNumerOfGames(int num){
		numOfGames = num;
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
	
	
	public void startGames(){
		if(!GameSettings.getNonGuiMode()){
			startGui();
		}
		
		//Looping the game thread for number of games wanted
		for(int x = 0;x<numOfGames;x++){
			
			//The Game Thread
			currentGame = new Game();
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
}
