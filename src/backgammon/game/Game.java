package backgammon.game;

import javax.swing.JOptionPane;

/**
 * The Class Game.
 */
public class Game implements Runnable{

	/** The Player2. */
	AIPlayer Player1, Player2;

	/** The board. */
	Board liveBoard;

	/** The AI turn. */
	boolean AITurn = false;


	/** The active game boolean, if the game is active. */
	boolean gameActive;


	// Quick booleans to change when you want a real player / AI player or change the team color
	boolean isAIBlack = true;
	boolean areBothAIs = true;
	int timeDelay = 5;


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

		Player1 = new AIPlayer(isAIBlack);
		Player2 = new AIPlayer(!isAIBlack);
	}

	/**
	 * Initial roll.
	 */
	public void initialRoll(){

		aiRoll = Player1.die1.RollDie();
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

		
		
		//The Game Loop
		while(gameActive){
			
			//The PlayerAI's Turn
			if(AITurn && gameActive){
				
				//creating the new board with the chosen moves etc
				liveBoard = new Board(Player1.AIturn(liveBoard));
				
				//displaying new board
				liveBoard.printBoardGUI();
				
				//checking if the player has now won
				if(liveBoard.hasPlayerWon(Player1.black)){
					gameActive = false;
					JOptionPane.showMessageDialog(null, "Player AI has won!");
				}else{

					//just to slow the visual process down, needed if both are AI
					try {
						Thread.sleep(timeDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					//if the player has not won the game, change boolean to allow the other player to take his turn
					AITurn = false;
				}

			//Player 2's Turn (could also be an AI)
			}else if(!AITurn && gameActive){
				
				//if you have selected them both to be AI's
				if(areBothAIs){
					
					//get the new board with the selected moves in
					liveBoard = new Board(Player2.AIturn(liveBoard));
					
					//display the new board
					liveBoard.printBoardGUI();
					
					//checking if the player has now won
					if(liveBoard.hasPlayerWon(Player2.black)){
						gameActive = false;
						JOptionPane.showMessageDialog(null, "Player 2 has won!");
					}else{

						//just to slow the visual process down, needed if both are AI
						try {
							Thread.sleep(timeDelay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						//if the player has not won the game, change boolean to allow the other player to take his turn
						AITurn = true;
					}
					
				//HUMAN PLAYER
				}else{
					
					//Running the turn method, waits for user input on selected move
					Player2.turn(liveBoard);
					
					//prints the board with any new moves
					liveBoard.printBoardGUI();
					
					//checking if the player has now won
					if(liveBoard.hasPlayerWon(Player2.black)){
						gameActive = false;
						JOptionPane.showMessageDialog(null, "Player 2 has won!");
					}else{
						AITurn = true;
					}
					
				}

			}
		}
	}

}
