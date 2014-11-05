package backgammon.game;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import backgammon.gui.BoardPanel;
import backgammon.gui.ControlPanel;

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
	static BoardPanel bp;
	ControlPanel cp;
	JPanel mainPanel;
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
		//Creating JFrame and panel
		frame = new JFrame("GAMMON BACK");
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		
		//creating and adding the Board Panel
		bp = new BoardPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		mainPanel.add(bp, c);
		
		//creating and adding the control panel
		cp = new ControlPanel();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;
		mainPanel.add(cp,c);
		
		frame.add(mainPanel);
		frame.setSize(1345,637);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
