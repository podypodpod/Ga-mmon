package backgammon.game;

import backgammon.settings.GameSettings;

/**
 * The Class Board.
 * 
 * Holds all the data on the board, where all the peices are, if the player is in a position to move etc
 */
public class Board {

	/**  The black and red bore amounts. */
	int redBore, blackBore;
	
	/**  The Points array, where all the pieces are located on the board. */
	public Point[] Points;
	
	boolean isInitialMove = false;
	
	/**
	 * Board default Constructor
	 * Instantiates a new board and sets the starting position to the default start pos.
	 */
	public Board(){
		
		//Creating 26 points
		Points = new Point[26];
		
		//creating the blank objects
		for(int x = 0; x<Points.length;x++){
			Points[x] = new Point();
		}
		
		//Resetting all to 0
		setStartPosition();
		
		//setting the bore chips to 0
		redBore = 0;
		blackBore = 0;
	}
	
	/**
	 * Instantiates a new board, cloning the one passed in.
	 *
	 * @param copy the copy board
	 */
	public Board(Board copy){
		//26 new points
		this.Points = new Point[26];
		
		//coppying each one over in a loop
		for(int i = 0; i<copy.Points.length; i++){
			//copy
			Point x = new Point(copy.Points[i]);
			//add
			this.Points[i] = x;
		}
		
		//copying bored
		this.redBore = copy.redBore;
		this.blackBore = copy.blackBore;
	}
	
	
	/**
	 * setStartPosition
	 * 
	 * Sets the start position in a standard game of backgammon.
	 */
	public void setStartPosition(){
		
		//Resetting all to 0
		for(int x =0; x<Points.length;x++){
			Points[x].setBlackCount(0);
			Points[x].setRedCount(0);
		}

		//completing the starting position of the checkers, the rest left at 0
		//Red Checkers
		Points[1].setRedCount(2);
		Points[12].setRedCount(5);
		Points[17].setRedCount(3);
		Points[19].setRedCount(5);
		//Black Checkers
		Points[6].setBlackCount(5);
		Points[8].setBlackCount(3);
		Points[13].setBlackCount(5);
		Points[24].setBlackCount(2);
	}
	
	/**
	 * Prints the board to console.
	 * 
	 * An actual Board resembling a BackGammon board and not just a list of values
	 */
	public void printBoard(){
		System.out.println("|---------------------------------------|");
		System.out.println("|  Black 0 = "+ Points[25].numEither()+"        |"+ "  Beared: "+blackBore+"     |");
		System.out.println("|---------------------------------------|");
		System.out.println("|NUM| 1| 2| 3| 4| 5| 6| 7| 8| 9|10|11|12|");
		System.out.print("|RED");
		for(int x = 1; x<=12;x++){
			System.out.print("| "+Points[x].getRedCount());
		}
		System.out.println("|");
		System.out.print("|BLK");
		for(int x = 1; x<=12;x++){
			System.out.print("| "+Points[x].getBlackCount());
		}
		System.out.println("|");
		System.out.println("|---------------------------------------|");
		System.out.println("|NUM|13|14|15|16|17|18|19|20|21|22|23|24|");
		System.out.print("|RED");
		for(int x = 13; x<=24;x++){
			System.out.print("| "+Points[x].getRedCount());
		}
		System.out.println("|");
		System.out.print("|BLK");
		for(int x = 13; x<=24;x++){
			System.out.print("| "+Points[x].getBlackCount());
		}
		System.out.println("|");
		System.out.println("|---------------------------------------|");
		System.out.println("|  Red 0 = "+ Points[0].numEither()+"          |"+ "  Beared: "+redBore+"     |");
		System.out.println("|---------------------------------------|");
	}
	
	/**
	 * Prints the board gui.
	 */
	public void printBoardGUI(){
		if(GameSettings.getDisplayGUI()){
			GameManager.containerFrame.bp.printCheckers(Points, redBore, blackBore);
		}
	}
	
	
	/**
	 * Checks if is there zero.
	 *
	 * @param black the color of the player
	 * @return true, if is there zero
	 */
	public boolean isthereZero(boolean black){
		
		if(black){
			if(Points[25].getBlackCount()>0){
				return true;
			}else{
				return false;
			}
		}else{
			if(Points[0].getRedCount()>0){
				return true;
			}else{
				return false;
			}
		}
		
	}
	
	
	/**
	 * Can player bear.
	 * 
	 * checks the board positions etc to check if the player can legally bear
	 *
	 * @param black the player color
	 * @return true, if successful
	 */
	public boolean canPlayerBear(boolean black){
		
		//BLACK
		if(black){
			//looping all areas apart from the players own quarter
			for(int x = 7; x<25;x++){
				//if any of them contain a black piece
				if(Points[x].getBlackCount()>0){
					return false;
				}
			}
			
		//RED
		}else{
			//looping all areas apart from the players own quarter
			for(int x = 1; x<18;x++){
				//if any of them contain a red piece
				if(Points[x].getRedCount()>0){
					return false;
				}
			}
		}
		return true;
		
	}

	/**
	 * Checks for player winning.
	 *
	 * @param black the player color
	 * @return true, if they have won
	 */
	public boolean hasPlayerWon(Boolean black) {
		//checking if the bore pieces = 15
		if((black && blackBore == 15)||(!black && redBore == 15)){
			//won
			return true;
		}else{
			//not won yet
			return false;
		}
	}

	/**
	 * Bear piece.
	 *
	 * @param bearPeice the position to bear
	 * @param black the player color
	 */
	public void bearPiece(int bearPeice, boolean black) {
		
		Points[bearPeice].removePiece(black);
		addToBear(black);
	}
	
	/**
	 * Adds the to bear.
	 *
	 * @param black the player color
	 */
	public void addToBear(boolean black){
		
		if(black){
			blackBore++;
		}else
		{
			redBore++;
		}
	}
	
	/**
	 * Equals.
	 * 
	 * Board comparison, if they are the same (same peice positions)
	 *
	 * @param b the board
	 * @return true, if they are the same
	 */
	public boolean equals(Board b){
		
		boolean theSame = true;
		
		//checking beared points
		if(this.redBore != b.redBore || this.blackBore != b.blackBore){
			theSame=false;
		}
		
		//checking points
		for(int x = 0; x<this.Points.length; x++){
			if(!(this.Points[x].equals(b.Points[x]))){
				theSame = false;
			}
		}
		
		return theSame;
		
	}
	
	/**
	 * How many has player bore.
	 *
	 * @param black the blackBore
	 * @return the num of bore
	 */
	public int howManyHasPlayerBore(boolean black){
		if(black){
			return blackBore;
		}else{
			return redBore;
		}
	}
	/**
	 * getNumOfBlots
	 * 
	 * @param black - player color
	 * @return int - number of blots
	 */
	public int getNumOfBlots(boolean black) {
		int num = 0;
		for(int x = 0; x<Points.length;x++){
			if(Points[x].numEither()==1 && Points[x].getCol()==black){
				num++;
			}
		}
		return num;
	}
	
	/**
	 * getNumOfHomePointsCovered
	 * 
	 * @param black - player color
	 * @return int - num of points covered
	 */
	public int getNumOfHomePointsCovered(Boolean black) {
		int num = 0;
		//BLACK
		if(black){
			for(int x = 1; x<=6;x++){
				if(Points[x].numEither()>0 && Points[x].getCol()==black){
					num++;
				}
			}
		//RED
		}else{
			for(int x = 19; x<=24;x++){
				if(Points[x].numEither()>0 && Points[x].getCol()==black){
					num++;
				}
			}
		}
		return num;
	}

	/**
	 * getNumberOfPiecesSurroundOpponent
	 * 
	 * @param black player color
	 * @return int - number of pieces
	 */
	public int getNumberOfPiecesSurroundOpponent(Boolean black) {
		int num=0;
		//loop points
		for(int x = 0; x<Points.length;x++){
			//if there is an oposition peice
			if(Points[x].numEither()>0 && Points[x].getCol()!=black){
				if(x<25 && x>0){
					//checking one before that
					if(Points[x-1].numEither()>0 &&Points[x-1].getCol()==black){
						num++;
					}
					//checking the point after it
					if(Points[x+1].numEither()>0 &&Points[x+1].getCol()==black){
						num++;
					}
				}
			}
		}
		
		return num;
	}

	public int getNumberOfCheckersOnStacks(Boolean black) {
		int num = 0;
		
		//loop points
		for(int x = 0; x<Points.length;x++){
			//if the current point has more than 2 checkers (a stack) and is ours
			if(Points[x].numEither()>2 && Points[x].getCol()==black){
				//Add the count of the stack to the int
				num += Points[x].numEither();
			}
		}
		return num;
	}
	
}
