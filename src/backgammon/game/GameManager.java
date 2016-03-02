/**
 * 	GNU General Public License
 * 
 *  This file is part of GA-mmon.
 *  
 *  GA-mmon is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  GA-mmon is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with GA-mmon.  If not, see <http://www.gnu.org/licenses/>.
*/

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