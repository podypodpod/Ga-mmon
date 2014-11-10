package backgammon.game;


public class AIPlayer extends Player{

	MoveGenerator moveGen;
	
	
	
	
	//default cons
	public AIPlayer(boolean b) {
		super(b);
		
		moveGen = new MoveGenerator();
		
	}
	
	//Clone cons
	public AIPlayer(AIPlayer p) {
		super(p.black);
		
		this.movesLeft = new MovesLeft(p.movesLeft);
		this.black = p.black;
		this.currentRoll1 = p.currentRoll1;
		this.currentRoll2 = p.currentRoll2;
		this.die1 = p.die1;
		this.die2 = p.die2;
		this.turnOver = p.turnOver;
		this.moveGen = p.moveGen;
	}
	
	public Board AIturn(Board currentBoard){
		
		System.out.println("------------AI's Turn!-----------------");
		
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

			
			System.out.println("AI rolls two rolls of "+currentRoll1+" have been made, "+movesLeft.size()+" moves left");
		}else{
			System.out.println("AI rolls a "+currentRoll1+" and a "+ currentRoll2+" have been rolled, "+movesLeft.size()+" moves left");
		}
		
		//get next board (could be null if there arent any next moves)
		Board newBoard = moveGen.getNextMoveBoard(currentBoard, this);
		
		if(newBoard == null){
			//if null, just use the existing board
			newBoard = currentBoard;
		}
		
		//print board
		newBoard.printBoard();
		
		return newBoard;
		
	}
	
}
