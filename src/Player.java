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
