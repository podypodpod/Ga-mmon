package backgammon.main;

import backgammon.gui.*;
import backgammon.settings.GameSettings;

public class MainWithMenu {

	public static void main (String args[]){
		
			//The Gui Thread
			MainMenuContainerFrame menu = new MainMenuContainerFrame();
			Thread guiThread = new Thread(menu);
			guiThread.start();
	}
}