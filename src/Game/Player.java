package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The Class Player.
 * 
 * 2 will be made, AI and you
 */
public class Player {
	
	static List<Integer> movesLeft;
	
	/** The side boolean. */
	Boolean black;
	
	/** The Dice. */
	Die Die1, Die2;
	
	int currentRoll1 = 0, currentRoll2 = 0;
	
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
		
		movesLeft = new ArrayList<Integer>();
		
		//roll dice
		currentRoll1 = Die1.RollDie();
		currentRoll2 = Die2.RollDie();
		
		//adding them to the array list
		movesLeft.add(currentRoll1);
		movesLeft.add(currentRoll2);
		
		//if you roll a double
		if(currentRoll1 == currentRoll2){
			
			movesLeft.add(currentRoll1);
			movesLeft.add(currentRoll1);
			
			
			System.out.println("Two rolls of "+currentRoll1+" have been made, you have 4 moves of "+currentRoll1);
			
		}else{
			System.out.println("A "+currentRoll1+" and a "+ currentRoll2+" have been rolled");
			
		}
		
		System.out.println("What do you want to do?, "+movesLeft.size()+" moves left");
		
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
	public boolean MovePeicePoss(int from, int to){
		
		//FROM PEICE
		
		//checking there is at least 1 chip at the starting position and it is their chip
		if( (Board.Points[from].numEither()>0) && (Board.Points[from].getCol()==black) ){
			
			//making sure if you have a peice at 0 then you have to move that first
			if(!((from!=0 || from!=25) &&
					//and your zero pieces does have a piece on it, deny
					(black && Board.Points[0].numEither()!=0) || (!black && Board.Points[25].numEither()!=0))) {
				
				//TO PEICE
				
				//looping through the moves Left array to check against what they have asked for
				boolean validLength = false;
				 for(int x: movesLeft){
					 if( x == distanceBetween(from,to)){
						 validLength = true;
					 }
				 }
				 if(validLength){
					 
					 //need to check the desination point has only 1 enemy chip or less or empty or one of yours
					 if((Board.Points[to].getCol()==black) || (Board.Points[to].getCol()!=black && Board.Points[to].numEither()<=1)){
						 
						 //DONE CHECKING
						 //possible to move the peice
						 return true;
						 }
					 }
				 }
			}
		//else the move is not possible
		return false;

	}
	
	public void movePeice(int from, int to){
		 //actually moving the piece
		 
		 //if there is an opposing piece there
		 if(Board.Points[to].getCol()!=black && Board.Points[to].numEither()==1){
			 
			 Board.Points[from].removePiece(black);
			 Board.Points[to].removePiece(!black);
			 Board.Points[to].addPiece(black);
			 if(black){
				 Board.Points[0].addBlackPeice();
			 }else{
				 Board.Points[25].addRedPeice();
			 }
			 
		 //if its empty
		 }else{
			 
			 Board.Points[from].removePiece(black);
			 Board.Points[to].addPiece(black);
		 }
	}
	
	private int distanceBetween(int a, int b){
		if(a>b){
			return (a-b);
		}else{
			return (b-a);
		}
	}
}
