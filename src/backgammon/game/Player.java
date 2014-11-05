package backgammon.game;

import java.util.Scanner;

/**
 * The Class Player.
 * 
 * 2 will be made, AI and you
 */
public class Player {
	
	public MovesLeft movesLeft;
	
	/** The side boolean. */
	Boolean black;
	
	/** The Dice. */
	Die die1, die2;
	
	boolean turnOver;
	
	int currentRoll1 = 0, currentRoll2 = 0;
	
	
	
	/**
	 * Instantiates a new player.
	 *
	 * @param b the b
	 */
	public Player(boolean b){
		
		black = b;
		
		die1 = new Die();
		die2 = new Die();
	
	}
	
	/**
	 * Turn.
	 */
	public void turn(Board liveBoard){
		
		turnOver = false;
		
		System.out.println("------------Your Turn!-----------------");
		
		movesLeft = new MovesLeft();
		
		//roll dice
		currentRoll1 = die1.RollDie();
		currentRoll2 = die2.RollDie();
		
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
		
		
		//ASKING WHAT TO DO
		while(!turnOver){
			
			System.out.println("What do you want to do?, "+movesLeft.size()+" moves left");
			
			System.out.println("1) Move a piece");
			System.out.println("2) Skip go");
			System.out.println("3) Concede");
			
			@SuppressWarnings("resource")
			Scanner Scanner = new Scanner(System.in);
			
			int option = Scanner.nextInt();

			//MOVE A piece
			if(option == 1){
				System.out.println("from which point: ");
				int from = Scanner.nextInt();
				System.out.println("to which point");
				int to = Scanner.nextInt();
				
				//IF ITS POSSIBLE
				if(movepiecePoss(from, to, liveBoard)){
					//MOVE THE PIECE
					movepiece(from, to, liveBoard);
					//REMOVE THE "MOVE"
					movesLeft.remove(Integer.valueOf((distanceBetween(from, to))));
					
					if(movesLeft.size()==0){
						turnOver = true;
					}
				}else{
					System.out.println("invalid move");
				}
			//SKIP GO
			}else if(option == 2){
				turnOver = true;
			//CONCEEDING
			}else if(option == 3){
				turnOver = true;
				
			}
		}
	}
	
	
	
	/**
	 * Move piece.
	 *
	 * @param from the from
	 * @param to the to
	 */
	public boolean movepiecePoss(int from, int to, Board liveBoard){
		
		//FROM piece
		
		//simply checking if its within the array bounds
		if(from>=0 && from<=25 && to<=25 && to>=0){
			
			//checking there is at least 1 chip at the starting position and it is their chip
			if( (liveBoard.Points[from].numEither()>0) && (liveBoard.Points[from].getCol()==black) ){
		
				
				//making sure if you have a piece at 0 then you have to move that first
				if(!((from!=0 || from!=25) &&
						//and your zero pieces does have a piece on it, deny
						(black && liveBoard.Points[0].numEither()!=0) || (!black && liveBoard.Points[25].numEither()!=0))) {
					
					//TO piece
					
					//checking it is not moving in to the 0 spaces
					if(!(black && to==0)||(!black && to==25)){
					
						//looping through the moves Left array to check against what they have asked for
						boolean validLength = false;
						 for(int x: movesLeft.movesLeft){
							 if( x == distanceBetween(from,to)){
								 validLength = true;
							 }
						 }
						 if(validLength){
							 
							 //need to check the destination point has only 1 enemy chip or less or empty or one of yours
							 if((liveBoard.Points[to].getCol()==black) || (liveBoard.Points[to].getCol()!=black && liveBoard.Points[to].numEither()<=1)){
								 
								 //DONE CHECKING
								 //possible to move the piece
								 return true;
								 }
							 }
						 }
					}
				}
		}
		//else the move is not possible
		return false;

	}
	
	public void movepiece(int from, int to, Board liveBoard){
		 //actually moving the piece
		 
		 //if there is an opposing piece there
		 if(liveBoard.Points[to].getCol()!=black && liveBoard.Points[to].numEither()==1){
			 
			 liveBoard.Points[from].removePiece(black);
			 liveBoard.Points[to].removePiece(!black);
			 liveBoard.Points[to].addPiece(black);
			 
			 if(black){
				 liveBoard.Points[0].addRedPiece();
			 }else{
				 liveBoard.Points[25].addBlackPiece();
			 }
			 
		 //if its empty
		 }else{
			 
			 liveBoard.Points[from].removePiece(black);
			 liveBoard.Points[to].addPiece(black);
		 }
	}
	
	public int distanceBetween(int a, int b){
		if(a>b){
			return (a-b);
		}else{
			return (b-a);
		}
	}
}
