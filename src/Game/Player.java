package Game;

import java.util.Scanner;

/**
 * The Class Player.
 * 
 * 2 will be made, AI and you
 */
public class Player {
	
	int movesLeft = 0;

	/** The side boolean. */
	Boolean black;
	
	/** The Dice. */
	Die Die1, Die2;
	
	int currentRoll1 = 0, currentRoll2 = 0, currentRoll3 = 0, currentRoll4 = 0;
	
	/**
	 * Instantiates a new player.
	 *
	 * @param b the b
	 */
	public Player(boolean b){
		
		black = b;
		
		Die1 = new Die();
		Die2 = new Die();
	
	}
	
	/**
	 * Turn.
	 */
	public void turn(){
		
		System.out.println("------------Your Turn!-----------------");
		
		
		currentRoll3 = 0;
		currentRoll4 = 0;	
		
		//roll dice
		currentRoll1 = Die1.RollDie();
		currentRoll2 = Die2.RollDie();
		
		//if you roll a double
		if(currentRoll1 == currentRoll2){
			
			currentRoll3 = currentRoll1;
			currentRoll4 = currentRoll1;
			
			movesLeft = 4;
			
			System.out.println("Two rolls of "+currentRoll1+" have been made, you have 4 moves of "+currentRoll1);
		}else{
			System.out.println("A "+currentRoll1+" and a "+ currentRoll2+" have been rolled");
			
			movesLeft = 2;
		}
		
		System.out.println("What do you want to do?, "+movesLeft+" moves left");
		
		System.out.println("1) Move a peice");
		System.out.println("2) Skip go");
		System.out.println("3) Concede");
		
		Scanner Scanner = new Scanner(System.in);
		int option = Scanner.nextInt();
	}
	
	
	
	/**
	 * Move peice.
	 *
	 * @param from the from
	 * @param to the to
	 */
	public boolean MovePeice(int from, int to){
		
		//FROM
		
		//checking there is at least 1 chip at the starting position and it is their chip
		if( (Board.Points[from].numEither()>0) && (Board.Points[from].getCol()==black) ){
			
			//NOT...if your going from anything but a zero peice
			if(!((from!=0 || from!=25) &&
					//and your zero peices does have a pecie on it, deny
					(black && Board.Points[0].numEither()!=0) || (!black && Board.Points[25].numEither()!=0))) {
				
				//TO 
				
				//

					
					
					// --movesLeft if it actually moves one
					
					
			}
		}
		//else the move is not possible
		return false;

	}
}
