package backgammon.game;
/**
 * The Class Board.
 * 
 * 
 */
public class Board {

	/** The black bore. */
	int redBore, blackBore;
	
	/** The Points. */
	public Point[] Points;

	
	/**
	 * Instantiates a new board.
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
	 * Instantiates a new board, cloning the one passed in
	 *
	 * @param copy the copy board
	 */
	public Board(Board copy){
		
		this.Points = new Point[26];
		for(int i = 0; i<copy.Points.length; i++){
			Point x = new Point(copy.Points[i]);
			this.Points[i] = x;
		}

		this.redBore = copy.redBore;
		this.blackBore = copy.blackBore;
	}
	
	
	/**
	 * Sets the start position.
	 */
	public void setStartPosition(){
		//Resetting all to 0
		for(int x =0; x<Points.length;x++){
			Points[x].setBlackCount(0);
			Points[x].setRedCount(0);
		}

		//completing the starting position of the checkers
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
	 * Prints the board.
	 * 
	 * An actual Board resembling a BackGammon board and not just a list of values
	 */
	public void printBoard(){
		System.out.println("|---------------------------------------|");
		System.out.println("|  Black 0 = "+ Points[25].numEither()+"        |"  + "  Beared: 0      |");
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
		System.out.println("|  Red 0 = "+ Points[0].numEither()+"          |"+ "  Beared: 0      |");
		System.out.println("|---------------------------------------|");
	}
	
	/**
	 * Prints the board gui.
	 */
	public void printBoardGUI(){
		Main.containerFrame.bp.printCheckers(Points, redBore, blackBore);
	}
	
	
	
	/**
	 * Checks if is there zero.
	 *
	 * @param black the black
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
	
}