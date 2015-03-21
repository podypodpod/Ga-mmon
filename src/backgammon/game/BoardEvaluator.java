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

	/**
	 * hasABlotBeenDoubled
	 * 
	 * Checks to see if the board passed in has had a single piece on its own (a blot)
	 * doubled and therefore safe from being taken
	 * 
	 * @param newBoard the board we are checking
	 * @return true if it has been doubled
	 */
	public boolean hasABlotBeenDoubled(Board newBoard) {
		if(liveBoard.getNumOfBlots(currentPlayer.black)>newBoard.getNumOfBlots(currentPlayer.black)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * hasAPieceBeenSpread
	 * 
	 * checks to see if the board passed in has moved a piece to another spot on the home board when bearing is applicable
	 * 
	 * @param newBoard the board we are checking
	 * @return true if it has been spread
	 */
	public boolean hasAPieceBeenSpread(Board newBoard) {
		if(liveBoard.getNumOfHomePointsCovered(currentPlayer.black)<newBoard.getNumOfHomePointsCovered(currentPlayer.black)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * hasTheOponentBeenBlocked
	 * 
	 * checks to see if the board passed in has moved a piece to block the oponent
	 * 
	 * @param newBoard the board we are checking
	 * @return true if it has been blocked
	 */
	public boolean hasTheOponentBeenBlocked(Board newBoard) {
		if(liveBoard.getNumberOfPiecesSurroundOpponent(currentPlayer.black)<newBoard.getNumberOfPiecesSurroundOpponent(currentPlayer.black)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * hasAStackBeenAddedTo
	 * 
	 * checks to see if the board passed in has moved a piece on top of an existing stack
	 * 
	 * @param newBoard the board we are checking
	 * @return true if it has been blocked
	 */
	public boolean hasAStackBeenAddedTo(Board newBoard) {
		if(liveBoard.getNumberOfCheckersOnStacks(currentPlayer.black)<newBoard.getNumberOfCheckersOnStacks(currentPlayer.black)){
			return true;
		}else{
			return false;
		}
	}
}
