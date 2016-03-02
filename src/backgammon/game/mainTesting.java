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

import backgammon.genes.Individual;

/**
 * mainTesting
 * 
 * A test class used for testing players etc.
 * 
 * running this will start a game or group of games depending on what is un-commented
 * If you want the GUI visable/not visable then use the variables in the backgammon.settings file
 * 
 * @author David Lomas - 11035527
 */
public class mainTesting {

	public static void main(String[] args) {
		
		//Creating the game manager and two players
		GameManager gn = new GameManager();
		Individual x = new Individual();
		Individual x2 = new Individual();
		
		
		
		//Playing 1 game
		System.out.println("---------INDIV 1----------");
		x.loadFromFile("PlayerFromGen0");
		//System.out.println(x.toString());
		
		System.out.println("----------INDIV 2---------");
		x2.loadFromFile("PlayerFromGen49");
		//System.out.println(x2.toString());
		
		
		GameStats gs = gn.playIndividualsVsEachOther(x, x2);

		System.out.println("Player who won the game: "+gs.getVictor());
		
		
		
		
		//For testing a large number of games between 2 players
		/*

		x.loadFromFile("PlayerFromGen0");
		System.out.println("Individual from generation 1, stats: "+x.toString());
		

		x2.loadFromFile("PlayerFromGen49");
		System.out.println("Individual from generation 50, stats: "+x2.toString());
		
			int num = 0;
			for(int e = 0; e<1000 ; e++){
				GameStats gs = gn.playIndividualsVsEachOther(x, x2);
				if(gs.getVictor().equals(x2)){
					num++;
				}
			}
			System.out.println("Individual from gen 50 won "+ num + "/1000 games against player from generation 1");
			
			*/

	}
}