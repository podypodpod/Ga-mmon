package backgammon.game;

import javax.swing.JOptionPane;

import backgammon.genes.Individual;
import backgammon.settings.GameSettings;

/**
 * The Class Game.
 * 
 * Contains the game loop and creates a new board, players etc
 */
public class Game implements Runnable{

	/**  The Players created. */
	AIPlayer Player1, Player2;

	/** The board. */
	private Board liveBoard;

	/** The AI turn. */
	private boolean p1sTurn = false;

	/** The active game boolean, if the game is active. */
	private boolean gameActive;

	/** The is p1 black. */
	private boolean isP1Black = GameSettings.isP1Black();
	
	/** The are both a is. */
	private boolean areBothAIs = GameSettings.getAreBothAI();
	
	/** The time delay. */
	private int timeDelay = GameSettings.getTimeDelay();

	/**  initial rolls. */
	private int p1Roll = 0, p2Roll = 0;

	/** The indiv2. */
	private Individual indiv1, indiv2;
	
	private Individual gameVictor = null;
	
	
	/**
	 * Instantiates a new game.
	 *
	 * @param i1 the i1
	 * @param i2 the i2
	 */
	public Game(Individual i1, Individual i2){

		liveBoard = new Board();
		liveBoard.printBoardGUI();

		//game active
		setGameActive(true);
		
		indiv1 = i1;
		indiv2 = i2;
		
		Player1 = new AIPlayer(isP1Black, indiv1);
		Player2 = new AIPlayer(!isP1Black, indiv2);
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
		if(GameSettings.getDisplayConsole()){
			System.out.print("Welcome! New game with two players");
			
			if(!areBothAIs){
				System.out.println("You are Player 2");
			}
			
			if(isP1Black){
				System.out.println("Player 1 is BLACK, Player 2 is RED");
			}else{
				System.out.println("Player 1 is RED, Player 2 is BLACK");
			}
			System.out.println("Initial dice throw commencing..");
		}
		
		
		//initial roll method, decides who moves first
		initialRoll();

		if(GameSettings.getDisplayConsole()){System.out.println("Player 1 rolls: "+p1Roll+"     Player 2: "+p2Roll);}
		//if player 1 has won
		if(p1Roll>p2Roll){
			if(GameSettings.getDisplayConsole()){System.out.println("Player 1(Black="+Player1.black+") Has won the roll");}
			p1sTurn = true;
		//if player 2 has won
		}else{
			if(GameSettings.getDisplayConsole()){System.out.println("Player 2(Black="+Player2.black+") Has won the roll");}
			p1sTurn = false;
		}
		
		//The Game Loop, while boolean gameActive is true, keep going
		while(isGameActive()){
			
			//Player1's Turn if the game is still active and it is now their turn
			if(p1sTurn && isGameActive()){
				
				//creating the new board with the chosen moves etc
				liveBoard = new Board(Player1.AIturn(liveBoard));
				
				//displaying new board
				liveBoard.printBoardGUI();
								
				//checking if the player has won yet
				//if they have won, then set the gameActive to false and display the message
				if(liveBoard.hasPlayerWon(Player1.black)){
					setGameActive(false);
					//JOptionPane.showMessageDialog(null, "Player 1 has won! (Black="+Player1.black+")");
					System.out.println("Player 1 has won! (Black="+Player1.black+")");
					gameOver();
					
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
			}else if(!p1sTurn && isGameActive()){
				
				//if you have selected them both to be AI's
				if(areBothAIs){
					
					//get the new board with the selected moves in
					liveBoard = new Board(Player2.AIturn(liveBoard));
					
					//display the new board
					liveBoard.printBoardGUI();
					
					//checking if the player has now won, if they have then gaveActive is now false and they are told they have won
					if(liveBoard.hasPlayerWon(Player2.black)){
						setGameActive(false);
						//JOptionPane.showMessageDialog(null, "Player 2 has won! (Black="+Player2.black+")");
						System.out.println("Player 2 has won! (Black="+Player2.black+")");
						gameOver();
						
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
						setGameActive(false);
						JOptionPane.showMessageDialog(null, "Player 2 has won! (Black="+Player2.black+")");
						gameOver();
					}else{
						p1sTurn = true;
					}
				}
			}
		}
	}
	
	/**
	 * Game Over.
	 * 
	 * What happens when the game comes to a finish
	 * 
	 * Working out the fitness of the game
	 */
	public void gameOver(){
		if(liveBoard.hasPlayerWon(Player1.black)){
			double reductions = liveBoard.howManyHasPlayerBore(Player2.black)*0.04;
			if(indiv1!=null){indiv1.setFitness(1-reductions);}
			if(indiv2!=null){indiv2.setFitness(reductions);}
			
			gameVictor = indiv1;
			
		}else if(liveBoard.hasPlayerWon(Player2.black)){
			double reductions = liveBoard.howManyHasPlayerBore(Player1.black)*0.04;
			if(indiv2!=null){indiv2.setFitness(1-reductions);}
			if(indiv1!=null){indiv1.setFitness(reductions);}
			
			gameVictor = indiv2;
		}
	}

	/**
	 * Gets the victor.
	 *
	 * @return the victor
	 */
	public Individual getVictor() {
		return gameVictor;
	}

	public boolean isGameActive() {
		return gameActive;
	}

	public void setGameActive(boolean gameActive) {
		this.gameActive = gameActive;
	}
}