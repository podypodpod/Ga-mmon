package backgammon.game;

import backgammon.settings.GameSettings;

/**
 * The Class BoardEvaluator.
 * 
 * Useful methods for picking boards from the board list e.g. pick a board where it will take a piece
 */
public class BoardEvaluator {
	
	/** The live board. */
	private Board liveBoard;
	
	/** The current player. */
	public AIPlayer currentPlayer;
	
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
			if(GameSettings.getDisplayConsole()){System.out.println("Blot doubled");}
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
			if(GameSettings.getDisplayConsole()){System.out.println("piece spread");}
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
			if(GameSettings.getDisplayConsole()){System.out.println("blocking oponent");}
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
			if(GameSettings.getDisplayConsole()){System.out.println("added to stack");}
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * isTwoOneSplitPlayInitialMove
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isTwoOneSplitPlayInitialMove(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[2].numEither()==1 && newBoard.Points[1].numEither()==1 && newBoard.Points[14].numEither()==1 && newBoard.Points[12].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isTwoOneSplitPlayInitialMove");}
				return true;
			}
		//red
		}else{
			//if it has performed the move
			if(newBoard.Points[24].numEither()==1 && newBoard.Points[23].numEither()==1 && newBoard.Points[13].numEither()==4 && newBoard.Points[11].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isTwoOneSplitPlayInitialMove");}
				return true;
			}
		}
		return false;
	}

	/**
	 * isTwoOneSlotPlayInitialMove
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isTwoOneSlotPlayInitialMove(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[6].numEither()==4 && newBoard.Points[5].numEither()==1 && newBoard.Points[14].numEither()==1 && newBoard.Points[12].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isTwoOneSlotPlayInitialMove");}
				return true;
			}
		//red
		}else{
			//if it has performed the move
			if(newBoard.Points[19].numEither()==4 && newBoard.Points[20].numEither()==1 && newBoard.Points[13].numEither()==4 && newBoard.Points[11].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isTwoOneSlotPlayInitialMove");}
				return true;
			}
		}
		return false;
	}

	/**
	 * isThreeOneInitialMove
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isThreeOneInitialMove(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[6].numEither()==4 && newBoard.Points[5].numEither()==2 && newBoard.Points[8].numEither()==2){
				if(GameSettings.getDisplayConsole()){System.out.println("isThreeOneInitialMove");}
				return true;
			}
		//red
		}else{
			//if it has performed the move
			if(newBoard.Points[19].numEither()==4 && newBoard.Points[20].numEither()==2 && newBoard.Points[17].numEither()==2){
				if(GameSettings.getDisplayConsole()){System.out.println("isThreeOneInitialMove");}
				return true;
			}
		}
		return false;
	}


	/**
	 * isThreeTwoSplitInitialMove
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isThreeTwoSplitInitialMove(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[4].numEither()==1 && newBoard.Points[1].numEither()==1 && newBoard.Points[12].numEither()==4 && newBoard.Points[14].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isThreeTwoSplitInitialMove");}
				return true;
			}
		//red
		}else{
			//if it has performed the move
			if(newBoard.Points[24].numEither()==1 && newBoard.Points[21].numEither()==1 && newBoard.Points[13].numEither()==4 && newBoard.Points[11].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isThreeTwoSplitInitialMove");}
				return true;
			}
		}
		return false;
	}


	/**
	 * isThreeTwoOffenceInitialMove
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isThreeTwoOffenceInitialMove(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[13].numEither()==3 && newBoard.Points[11].numEither()==1 && newBoard.Points[10].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isThreeTwoOffenceInitialMove");}
				return true;
			}
		//red	
		}else{
			//if it has performed the move
			if(newBoard.Points[12].numEither()==3 && newBoard.Points[14].numEither()==1 && newBoard.Points[15].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isThreeTwoOffenceInitialMove");}
				return true;
			}
		}
		return false;
	}


	/**
	 * isFourTwoInitialMove
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFourOneInitialMove(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[24].numEither()==1 && newBoard.Points[23].numEither()==1 && newBoard.Points[13].numEither()==4 && newBoard.Points[9].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isFourOneInitialMove");}
				return true;
			}
		//red	
		}else{
			//if it has performed the move
			if(newBoard.Points[1].numEither()==1 && newBoard.Points[2].numEither()==1 && newBoard.Points[12].numEither()==4 && newBoard.Points[16].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isFourOneInitialMove");}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFourTwoInitialMoveAlt
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFourOneInitialMoveAlt(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[6].numEither()==4 && newBoard.Points[5].numEither()==1 && newBoard.Points[13].numEither()==4 && newBoard.Points[9].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isFourOneInitialMoveAlt");}
				return true;
			}
		//red	
		}else{
			//if it has performed the move
			if(newBoard.Points[19].numEither()==4 && newBoard.Points[20].numEither()==1 && newBoard.Points[12].numEither()==4 && newBoard.Points[16].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isFourOneInitialMoveAlt");}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFourTwoInitialMove
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFourTwoInitialMove(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[8].numEither()==2 && newBoard.Points[4].numEither()==2 && newBoard.Points[6].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isFourTwoInitialMove");}
				return true;
			}
		//red	
		}else{
			//if it has performed the move
			if(newBoard.Points[17].numEither()==2 && newBoard.Points[21].numEither()==2 && newBoard.Points[19].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isFourTwoInitialMove");}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFourThreeInitialMoveSplit
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFourThreeInitialMoveSplit(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[24].numEither()==1 && newBoard.Points[21].numEither()==1 && newBoard.Points[6].numEither()==4 && newBoard.Points[9].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isFourThreeInitialMoveSplit");}
				return true;
			}
		//red	
		}else{
			//if it has performed the move
			if(newBoard.Points[1].numEither()==1 && newBoard.Points[4].numEither()==1 && newBoard.Points[13].numEither()==4 && newBoard.Points[16].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isFourThreeInitialMoveSplit");}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFourThreeInitialMoveBlock
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFourThreeInitialMoveBlock(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[13].numEither()==3 && newBoard.Points[10].numEither()==1 && newBoard.Points[9].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isFourThreeInitialMoveBlock");}
				return true;
			}
		//red	
		}else{
			//if it has performed the move
			if(newBoard.Points[12].numEither()==3 && newBoard.Points[15].numEither()==1 && newBoard.Points[16].numEither()==1){
				if(GameSettings.getDisplayConsole()){System.out.println("isFourThreeInitialMoveBlock");}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFiveOneInitialMove
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveOneInitialMove(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[24].numEither()==1 && newBoard.Points[23].numEither()==1 && newBoard.Points[13].numEither()==4 && newBoard.Points[8].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isFiveOneInitialMove");}
				return true;
			}
		//red	
		}else{
			//if it has performed the move
			if(newBoard.Points[1].numEither()==1 && newBoard.Points[2].numEither()==1 && newBoard.Points[12].numEither()==4 && newBoard.Points[17].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isFiveOneInitialMove");}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFiveOneInitialMoveAlt
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveOneInitialMoveAlt(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[6].numEither()==4 && newBoard.Points[5].numEither()==1 && newBoard.Points[13].numEither()==4 && newBoard.Points[8].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isFiveOneInitialMoveAlt");}
				return true;
			}
		//red	
		}else{
			//if it has performed the move
			if(newBoard.Points[19].numEither()==4 && newBoard.Points[20].numEither()==1 && newBoard.Points[12].numEither()==4 && newBoard.Points[17].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isFiveOneInitialMoveAlt");}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFiveTwoInitialMove
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveTwoInitialMove(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[11].numEither()==1 && newBoard.Points[13].numEither()==3 && newBoard.Points[8].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isFiveOneInitialMoveAlt");}
				return true;
			}
		//red	
		}else{
			//if it has performed the move
			if(newBoard.Points[14].numEither()==1 && newBoard.Points[12].numEither()==3 && newBoard.Points[17].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isFiveOneInitialMoveAlt");}
				return true;
			}
		}
		return false;
	}

	/**
	 * isFiveTwoInitialMoveRisk
	 * 
	 * checks to see if the board passed in has used this particular initial move (move taken from http://www.bkgm.com/openings.html#opening21)
	 * 
	 * @param newBoard the board we are checking
	 * @return true if the move has been made
	 */
	public boolean isFiveTwoInitialMoveRisk(Board newBoard) {
		//black
		if(currentPlayer.black){
			//if it has performed the move
			if(newBoard.Points[24].numEither()==1 && newBoard.Points[22].numEither()==1 && newBoard.Points[13].numEither()==4 && newBoard.Points[8].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isFiveOneInitialMoveAlt");}
				return true;
			}
		//red	
		}else{
			//if it has performed the move
			if(newBoard.Points[1].numEither()==1 && newBoard.Points[3].numEither()==1 && newBoard.Points[12].numEither()==4 && newBoard.Points[17].numEither()==4){
				if(GameSettings.getDisplayConsole()){System.out.println("isFiveOneInitialMoveAlt");}
				return true;
			}
		}
		return false;
	}
	
}
