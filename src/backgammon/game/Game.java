package backgammon.game;




/**
 * The Class Game.
 */
public class Game implements Runnable{
	
	/** The Player2. */
	AIPlayer Player2, PlayerAI;
	
	/** The board. */
	Board liveBoard;
	
	/** The AI turn. */
	boolean AITurn = false;


	/** The active game boolean, if the game is active. */
	boolean gameActive;
	
	
	// Quick booleans to change when you want a real player / AI player or change the team color
	boolean isAIBlack = true;
	boolean areBothAIs = true;

	
	//initial rolls
	/** The p2 roll. */
	int aiRoll = 0, p2Roll = 0;
	
	/**
	 * Instantiates a new game.
	 */
	public Game(){
		
		liveBoard = new Board();
		liveBoard.printBoardGUI();
		
		//game active
		gameActive = true;
		
		PlayerAI = new AIPlayer(isAIBlack);
		if(areBothAIs){
			Player2 = new AIPlayer(!isAIBlack);
		}else{
			Player2 = new AIPlayer(!isAIBlack);
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

	@Override
	public void run() {
		
		System.out.print("     Welcome! New game, you are ");
		if(isAIBlack){
			System.out.println("RED");
		}else{
			System.out.println("BLACK");
		}
		
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
				
				if(areBothAIs){
					liveBoard = new Board(((AIPlayer) Player2).AIturn(liveBoard));
					liveBoard.printBoardGUI();
					AITurn = true;
				}else{
					
					Player2.turn(liveBoard);
					liveBoard.printBoardGUI();
					AITurn = true;
				}

			}
		}
		
		
	}
	
}