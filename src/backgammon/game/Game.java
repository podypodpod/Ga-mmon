package backgammon.game;

import javax.swing.JOptionPane;

/**
 * The Class Game.
 * 
 * Contains the game loop and creates a new board, players etc
 */
public class Game implements Runnable{

	/**  The Players created. */
	AIPlayer Player1, Player2;

	/** The board. */
	Board liveBoard;

	/** The AI turn. */
	boolean p1sTurn = false;

	/** The active game boolean, if the game is active. */
	boolean gameActive;

	// Quick booleans to change when you want a real player / AI player or change the team color
	/** The is p1 black. */
	boolean isP1Black = true;
	
	/** The are both a is. */
	boolean areBothAIs = true;
	
	/** The time delay. */
	int timeDelay = 200;

	//initial rolls
	/** The p2 roll. */
	int p1Roll = 0, p2Roll = 0;

	/**
	 * Instantiates a new game.
	 */
	public Game(){

		liveBoard = new Board();
		liveBoard.printBoardGUI();

		//game active
		gameActive = true;

		Player1 = new AIPlayer(isP1Black);
		Player2 = new AIPlayer(!isP1Black);
	}

	/**
	 * Initial roll.
	 * 
	 * Keeps rolling the dice until both players have a different score and therefore a winner
	 * 
	 */
	public void initialRoll(){

		p1Roll = Player1.die1.RollDie();
		p2Roll = Player2.die1.RollDie();

		if(p1Roll==p2Roll){
			initialRoll();
		}
	}
	
	/**
	 * Run method
	 * 
	 * Contains the game loop.
	 */
	@Override
	public void run() {
		
		//Printing game info out to console
		System.out.print("     Welcome! New game, you are ");
		if(isP1Black){
			System.out.println("RED");
		}else{
			System.out.println("BLACK");
		}
		System.out.println("     Initial dice throw commencing..");
		
		//initial roll method, decides who moves first
		initialRoll();

		System.out.println("     AI rolls: "+p1Roll+"     You roll: "+p2Roll);
		//if player 1 has won
		if(p1Roll>p2Roll){
			System.out.println("     AI wins! wait for your turn...");
			p1sTurn = true;
		//if player 2 has won
		}else{
			System.out.println("     You win the roll!....");
			p1sTurn = false;
		}
		
		//The Game Loop, while boolean gameActive is true, keep going
		while(gameActive){
			
			//Player1's Turn if the game is still active and it is now their turn
			if(p1sTurn && gameActive){
				
				//creating the new board with the chosen moves etc
				liveBoard = new Board(Player1.AIturn(liveBoard));
				
				//displaying new board
				liveBoard.printBoardGUI();
				
				//TODO: Create an option to start a new game
				
				//checking if the player has won yet
				//if they have won, then set the gameActive to false and display the message
				if(liveBoard.hasPlayerWon(Player1.black)){
					gameActive = false;
					JOptionPane.showMessageDialog(null, "Player AI has won!");
					
				//if they haven't won then continue
				}else{

					//just to slow the visual process down, needed if both are AI
					try {
						Thread.sleep(timeDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					//if the player has not won the game, change boolean to allow the other player to take his turn
					p1sTurn = false;
				}

			//Player2's Turn (could also be an AI)
			}else if(!p1sTurn && gameActive){
				
				//if you have selected them both to be AI's
				if(areBothAIs){
					
					//get the new board with the selected moves in
					liveBoard = new Board(Player2.AIturn(liveBoard));
					
					//display the new board
					liveBoard.printBoardGUI();
					
					//checking if the player has now won, if they have then gaveActive is now false and they are told they have won
					if(liveBoard.hasPlayerWon(Player2.black)){
						gameActive = false;
						JOptionPane.showMessageDialog(null, "Player 2 has won!");
						
					//if not, let the game loop contine
					}else{

						//just to slow the visual process down, needed if both are AI
						try {
							Thread.sleep(timeDelay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						//if the player has not won the game, change boolean to allow the other player to take his turn
						p1sTurn = true;
					}
					
				//Option for the human player
				}else{
					
					//Running the turn method, waits for user input on selected move
					Player2.turn(liveBoard);
					
					//prints the board with any new moves
					liveBoard.printBoardGUI();
					
					//checking if the player has now won
					//gameActive is false if they have, and they are informed
					if(liveBoard.hasPlayerWon(Player2.black)){
						gameActive = false;
						JOptionPane.showMessageDialog(null, "Player 2 has won!");
					}else{
						p1sTurn = true;
					}
				}
			}
		}
	}
}