package Game;

import java.util.ArrayList;

public class AIPlayer extends Player{

	public AIPlayer(boolean b) {
		super(b);
		
		
	}
	
	public void turn(){
		
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

			
			System.out.println("Two rolls of "+currentRoll1+" have been made");
		}else{
			System.out.println("a "+currentRoll1+" and a "+ currentRoll2+" have been rolled");
		}
		
		genPosMoves();
		
	}
	
	private void genPosMoves(){
			
			for(int y = 0; y<Board.Points.length;y++){
				for(int x: movesLeft){
					
				}
			}
	}

}
