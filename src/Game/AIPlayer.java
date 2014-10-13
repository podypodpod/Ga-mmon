package Game;

public class AIPlayer extends Player{

	public AIPlayer(boolean b) {
		super(b);
		
		
	}
	
	public void turn(){
		
		currentRoll3 = 0;
		currentRoll4 = 0;	
		
		//roll dice
		currentRoll1 = Die1.RollDie();
		currentRoll2 = Die2.RollDie();
		
		//if you roll a double
		if(currentRoll1 == currentRoll2){
			
			currentRoll3 = currentRoll1;
			currentRoll4 = currentRoll1;
			
			System.out.println("Two rolls of "+currentRoll1+" have been made");
		}else{
			System.out.println("a "+currentRoll1+" and a "+ currentRoll2+" have been rolled");
		}
		
		genPosMoves(currentRoll1, currentRoll2, currentRoll3, currentRoll4);
		
	}
	
	private void genPosMoves(int die1, int die2, int die3, int die4){
		
		if(die3!=0||die4!=0){
			//double
			
		}else{
			//single
			
		}
		
		
	}

}
