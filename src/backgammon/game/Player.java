package backgammon.game;

import java.util.Scanner;

import backgammon.settings.GameSettings;

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
	
	DiceList dice;

	boolean turnOver;


	/**
	 * Instantiates a new player.
	 *
	 * @param b the b
	 */
	public Player(boolean b){

		black = b;

		movesLeft = new MovesLeft();
		
		dice = new DiceList();

		die1 = new Die();
		die2 = new Die();
	}

	/**
	 * Turn.
	 */
	public void turn(Board liveBoard){

		turnOver = false;

		if(GameSettings.getDisplayConsole()){System.out.println("------------Your Turn!-----------------");}

		//Rolling the dice
		movesLeft.setTo(dice.RollDice());
		
		System.out.println("Player has : "+movesLeft.toString());
		
		//ASKING WHAT TO DO
		while(!turnOver){

			if(GameSettings.getDisplayConsole()){
				System.out.println("What do you want to do?, "+movesLeft.size()+" moves left");
	
				System.out.println("1) Move a piece");
				System.out.println("2) Bear off a piece");
				System.out.println("3) Skip or Finish go");
				System.out.println("4) Concede");
			}

			@SuppressWarnings("resource")
			Scanner Scanner = new Scanner(System.in);

			int option = Scanner.nextInt();

			//MOVE A piece
			if(option == 1){
				System.out.println("from which point (1,2,3 etc): ");
				int from = Scanner.nextInt();
				System.out.println("to which point (1,2,3,bear=-1 etc)");
				int to = Scanner.nextInt();

				//IF ITS POSSIBLE
				if(movePiecePoss(from, to, liveBoard)){
					//MOVE THE PIECE
					movePiece(from, to, liveBoard);
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
				if(liveBoard.canPlayerBear(this.black)){

					//CHECK ITS A VALID MOVE/LENGTH

					System.out.println("Bear wich piece?");

					int bearPeice = Scanner.nextInt();
					if(movePiecePoss(bearPeice, -1, liveBoard)){
						liveBoard.bearPiece(bearPeice, this.black);
					}
				}else{
					System.out.println("Can not bear pieces yet");
				}
			}else if(option == 3){
				turnOver = true;
				//CONCEEDING
			}else if(option == 4){
				turnOver = true;

			}
		}
	}



	/**
	 * Move piece. 
	 * @param from the from
	 * @param to the to
	 */
	public boolean movePiecePoss(int from, int to, Board liveBoard){

		//FROM piece

		//simply checking if its within the array bounds, -1 = bear
		if(from>=0 && from<=25 && to<=26 && to>=-1){

			//checking there is at least 1 chip at the starting position and it is their chip
			if( (liveBoard.Points[from].numEither()>0) && (liveBoard.Points[from].getCol()==black) ){

				//checking its going the right direction
				if((black && to<from)||(!black && to>from)){

					//making sure if you have a piece at 0 then you have to move that first
					if(liveBoard.isthereZero(black)){
						if((black && from!=25)||(!black && from!=0)){
							return false;
						}
					}

					//TO piece, -1 = bear

					//checking it is not moving in to the 0 spaces
					if(to!=0 && to!=25 && to!=-1 && to!=26){

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

						
					//TO: BEARING, if the player can bear
					}else if(((to==-1 || to==26) && liveBoard.canPlayerBear((liveBoard.Points[from].getCol())))){
						
						int y = 0;
						
						if(to==-1){
							y=1;
						}else{
							y=-1;
						}
						
						//looping through the moves Left array to check against what they have asked for
						boolean validLength = false;
						for(int x: movesLeft.movesLeft){
							if( x == distanceBetween(from,(to+y))){
								validLength = true;
							}
						}
						if(validLength){
							return true;
						}
					}
				}
			}
		}
		//else the move is not possible
		return false;

	}

	public void movePiece(int from, int to, Board liveBoard){
		//actually moving the piece

		//if there is an opposing piece there
		if(to!=-1 && to!=26){
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
		//BEARING THE PEICE OFF
		}else if(to==-1 || to==26){
			liveBoard.Points[from].removePiece(black);
			liveBoard.addToBear(black);
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
