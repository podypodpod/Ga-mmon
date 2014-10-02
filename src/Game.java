
// TODO: Auto-generated Javadoc
/**
 * The Class Game.
 */
public class Game {
	
	/** The Player2. */
	Player PlayerAI,Player2;
	
	/** The board. */
	Board board;
	
	/** The AI turn. */
	boolean AITurn = false;
	
	//initial rolls
	/** The p2 roll. */
	int aiRoll = 0, p2Roll = 0;
	
	/**
	 * Instantiates a new game.
	 */
	Game(){
		
		board = new Board();
		
		PlayerAI = new Player(true);
		Player2 = new Player(false);
		
		
		System.out.println("     Welcome! New game, you are RED");
		
		board.printBoard();
		
		System.out.println("     Initial dice throw commencing..");

		initialRoll();
		
		System.out.println("     AI rolls: "+aiRoll+"     You roll: "+p2Roll);
		if(aiRoll>p2Roll){
			System.out.println("   AI wins! wait for youre turn...");
			AITurn = true;
		}else{
			System.out.println("   You win! what would you like to do?");
			AITurn = false;
		}

		//presume here there needs to be a loop going to a turn() method on each player with a global boolean that ends it when the game is complete
	}
	
	/**
	 * Initial roll.
	 */
	public void initialRoll(){
		
		aiRoll = PlayerAI.Die1.RollDie();
		p2Roll = Player2.Die1.RollDie();

		if(aiRoll==p2Roll){
			initialRoll();
		}
	}
	
	
}
