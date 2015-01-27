package backgammon.game;

import backgammon.settings.GameSettings;

/**
 * The Class BoardEvaluator.
 * 
 * Useful methods for picking boards from the board list e.g. pick a board where it will take a peice
 */
public class BoardEvaluator {
	
	/** The live board. */
	private Board liveBoard;
	
	/** The current player. */
	private AIPlayer currentPlayer;
	
	/** The opponent zero positions. */
	private int myZero, oponentZero;
	
	
	/**
	 * Board evaluator cons.
	 */
	public void BoardEvalutator(){}
	
	
	/**
	 * Checks for a piece being taken.
	 *
	 * @param newBoard the new board
	 * @return true, if successful
	 */
	public boolean hasAPeiceBeenTaken(Board newBoard){
		if(liveBoard.Points[oponentZero].numEither()<newBoard.Points[oponentZero].numEither()){
			if(GameSettings.getDisplayConsole()){System.out.println("Peice has been taken.");}
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Sets the board.
	 *
	 * @param cb the new board
	 */
	public void setBoard(Board cb){
		liveBoard = cb;
	}
	
	/**
	 * Sets the player.
	 *
	 * @param p the new player
	 */
	public void setPlayer(AIPlayer p){
		currentPlayer = p;
		if(p.black){
			setMyZero(25);
			oponentZero=0;
		}else{
			setMyZero(0);
			oponentZero=25;
		}
	}
	
	/**
	 * Checks for a piece that has been bore.
	 *
	 * @param newBoard the new board
	 * @return true, if successful
	 */
	public boolean hasAPeiceBeenBore(Board newBoard) {
		if(liveBoard.howManyHasPlayerBore(currentPlayer.black)<newBoard.howManyHasPlayerBore(currentPlayer.black)){
			if(GameSettings.getDisplayConsole()){System.out.println("Peice has been BORE");}
			return true;
		}else{
			return false;
		}
	}

	/**getMyZero
	 * 
	 * @return the value of myZero
	 */
	public int getMyZero() {
		return myZero;
	}

	/**setMyZero
	 * 
	 * @param myZero
	 */
	public void setMyZero(int myZero) {
		this.myZero = myZero;
	}
}
