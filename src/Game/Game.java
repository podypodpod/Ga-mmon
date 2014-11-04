package Game;

import javax.swing.JFrame;

/**
 * The Class Game.
 */
public class Game {
	
	/** The Player2. */
	Player Player2;
	AIPlayer PlayerAI;
	
	/** The board. */
	Board liveBoard;
	
	/** The AI turn. */
	boolean AITurn = false;
	
	//GUI stuff
	public static BoardPanel bp;
	JFrame frame;

	/** The active game boolean, if the game is active. */
	boolean gameActive;
	
	//initial rolls
	/** The p2 roll. */
	int aiRoll = 0, p2Roll = 0;
	
	/**
	 * Instantiates a new game.
	 */
	Game(){
		
		//GUI
		frame = new JFrame("GAMMON BACK");
		bp = new BoardPanel();
		frame.add(bp);
		frame.setSize(1169,637);
		frame.setVisible(true);

		
		liveBoard = new Board();
		liveBoard.printBoardGUI();
		
		//game active
		gameActive = true;
		
		PlayerAI = new AIPlayer(true);
		Player2 = new Player(false);
		
		
		System.out.println("     Welcome! New game, you are RED");
		
		System.out.println("     Initial dice throw commencing..");

		initialRoll();
		
		System.out.println("     AI rolls: "+aiRoll+"     You roll: "+p2Roll);
		if(aiRoll>p2Roll){
			System.out.println("     AI wins! wait for your turn...");
			AITurn = true;
		}else{
			System.out.println("     You win the roll!....");
			AITurn = false;
		}
		
		//presume here there needs to be a loop going to a turn() method on each player with a global boolean that ends it when the game is complete
		while(gameActive){
			if(AITurn){
				liveBoard = new Board(PlayerAI.AIturn(liveBoard));
				liveBoard.printBoardGUI();
				AITurn = false;
				
			}else{
				Player2.turn(liveBoard);
				liveBoard.printBoardGUI();
				AITurn = true;
			}
		}
		
	}
	
	/**
	 * Initial roll.
	 */
	public void initialRoll(){
		
		aiRoll = PlayerAI.die1.RollDie();
		p2Roll = Player2.die1.RollDie();

		if(aiRoll==p2Roll){
			initialRoll();
		}
	}
	
	
}
