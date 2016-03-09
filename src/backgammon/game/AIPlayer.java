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
import backgammon.settings.GameSettings;

/**
 * The Class AIPlayer.
 * 
 * extends the Player class and implements methods to allow the AI to choose the next move
 * 
 * @author David Lomas - 11035527
 */
public class AIPlayer extends Player{

	MoveGenerator moveGen;
	private Individual individual;


	public AIPlayer(boolean b, Individual indiv) {
		super(b);
		individual = indiv;
		moveGen = new MoveGenerator(individual);
	}


	public Individual getIndividual(){
		return individual;
	}

	public AIPlayer(AIPlayer p) {
		super(p.black);

		this.movesLeft = new MovesLeft(p.movesLeft);
		this.black = p.black;
		this.die1 = p.die1;
		this.die2 = p.die2;
		this.turnOver = p.turnOver;
		this.moveGen = p.moveGen;
	}

	public Board AIturn(Board currentBoard){

		if(GameSettings.getDisplayConsole()){System.out.println("------------AI's Turn!-----------------");}

		movesLeft = new MovesLeft();
		movesLeft.setTo(dice.RollDice());
		if(GameSettings.getDisplayConsole()){System.out.println("Player has : "+movesLeft.toString());}

		Board newBoard = moveGen.getNextMoveBoard(currentBoard, this);

		//if the board has not changed
		if(newBoard == null){
			newBoard = currentBoard;
		}

		return newBoard;
	}
}