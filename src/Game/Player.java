package Game;
/**
 * The Class Player.
 * 
 * 2 will be made, AI and you
 */
public class Player {

	/** The side boolean. */
	Boolean black;
	
	/** The Dice. */
	Die Die1, Die2;
	
	int currentRoll1, currentRoll2;
	
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
		currentRoll1 = Die1.RollDie();
		currentRoll2 = Die2.RollDie();
		
		System.out.println("a "+currentRoll1+" and a "+ currentRoll2+" have been rolled");
		
		//carry on here
	}
	
	
	
	/**
	 * Move peice.
	 *
	 * @param from the from
	 * @param to the to
	 */
	public void MovePeice(int from, int to){
		
		
		if(black = true){
			Board.Points[from].removeBlackPeice();
			Board.Points[to].addBlackPeice();
		}else{
			Board.Points[from].removeRedPeice();
			Board.Points[to].addRedPeice();
		}

		
	}
}
