package backgammon.game;

import backgammon.genes.Individual;
import backgammon.settings.GameSettings;

/**
 * The Class AIPlayer.
 * 
 * extends the Player class
 */
public class AIPlayer extends Player{

	/** The move generator for the player to use. */
	MoveGenerator moveGen;
	
	/** player personallity is just an appropriate name for the individual*/
	private Individual playerPersonality;
	
	/**
	 * Instantiates a new AI player.
	 *
	 * @param b the color of the player, true = black
	 */
	public AIPlayer(boolean b, Individual indiv) {
		//super is the player class, the boolean is the colour of the player
		super(b);
		playerPersonality = indiv;
		moveGen = new MoveGenerator(playerPersonality);
	}
	
	/**
	 * Instantiates a new AI player, cloning the old one.
	 *
	 * @param p the player you are cloning
	 */
	public AIPlayer(AIPlayer p) {
		super(p.black);
		
		this.movesLeft = new MovesLeft(p.movesLeft);
		this.black = p.black;
		this.die1 = p.die1;
		this.die2 = p.die2;
		this.turnOver = p.turnOver;
		this.moveGen = p.moveGen;
	}
	
	/**
	 * AI Player's turn.
	 * Dice are rolled, all possible moves generated, best is selected, turn over.
	 *
	 * @param currentBoard the current board
	 * @return the resulting board at the end
	 */
	public Board AIturn(Board currentBoard){
		
		if(GameSettings.getDisplayConsole()){System.out.println("------------AI's Turn!-----------------");}
		
		movesLeft = new MovesLeft();

		//Rolling the dice
		movesLeft.setTo(dice.RollDice());
		
		if(GameSettings.getDisplayConsole()){System.out.println("Player has : "+movesLeft.toString());}
		
		
		//get next board (could be null if there aren't any next moves)
		Board newBoard = moveGen.getNextMoveBoard(currentBoard, this);
		
		//if the board has not changed
		if(newBoard == null){
			//if null, just use the existing board
			newBoard = currentBoard;
		}
		
		//return the new board, with the new moves implemented (or not if not possible, could be the same)
		return newBoard;
	}
}