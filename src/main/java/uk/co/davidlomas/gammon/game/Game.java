package uk.co.davidlomas.gammon.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.settings.GameSettings;

import java.util.List;

/**
 * The Class Game.
 * <p>
 * Contains the game loop and creates a new board, players etc
 * <p>
 * This class just needs two Individuals passed in to it to play a game
 * <p>
 * To get a human player as player 2, set areBothAIs to false in
 * conf/GameSettings.properties, or use PlayAgainstAi
 *
 * @author David Lomas
 */
class Game implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(Game.class);

	// A game between two AIs is cut short after this many turns. Backgammon has no limit, but players whose genes
	// value hitting over getting home can keep sending each other back for ever. A normal game is 50 to 60 turns
	static final int MAX_TURNS = 300;

	private final AiPlayer aiPlayer1;
	private final AiPlayer aiPlayer2;

	private final Individual individual1;
	private final Individual individual2;

	private Board liveBoard;
	private boolean gameActive;
	private final boolean isP1Black = GameSettings.isP1Black();
	private final boolean areBothAIs;
	private final boolean showsOnBoardWindow;
	private final int timeDelay = GameSettings.getTimeDelay();
	private int p1Roll = 0, p2Roll = 0;
	private final int maxTurns;
	private int turnsPlayed = 0;
	private boolean cutShort = false;
	private GameObserver observer = GameObserver.NONE;

	// the opening move is played with the two dice from the opening roll, rather than a new roll. Null once it's used
	private List<Integer> openingDice;

	/**
	 * Instantiates a new game.
	 */
	Game(final Individual individual1, final Individual individual2) {
		this(individual1, individual2, MAX_TURNS);
	}

	/**
	 * A game cut short after maxTurns, if both players are AIs, so tests can use a short limit
	 */
	Game(final Individual individual1, final Individual individual2, final int maxTurns) {
		this(individual1, individual2, maxTurns, GameSettings.getAreBothAI(), GameSettings.getDisplayGUI());
	}

	/**
	 * @param areBothAIs         false if a person plays player 2
	 * @param showsOnBoardWindow whether to draw on the window GameManager shares between games, and pause to show it
	 */
	Game(final Individual individual1, final Individual individual2, final int maxTurns, final boolean areBothAIs,
		 final boolean showsOnBoardWindow) {
		this.maxTurns = maxTurns;
		this.areBothAIs = areBothAIs;
		this.showsOnBoardWindow = showsOnBoardWindow;

		liveBoard = new Board();
		showBoard();

		gameActive = true;
		this.individual1 = individual1;
		this.individual2 = individual2;

		aiPlayer1 = new AiPlayer(isP1Black, individual1);
		aiPlayer2 = new AiPlayer(!isP1Black, individual2);
	}

	void setObserver(final GameObserver observer) {
		this.observer = observer;
	}

	/**
	 * Player 2, the one a person plays when areBothAI is off, so tests can give it scripted moves
	 */
	AiPlayer getPlayerTwo() {
		return aiPlayer2;
	}

	GameStats getGameStats() {
		// the winner gains the points the win is worth, 1, 2 or 3, and the loser loses them, as in backgammon, so a
		// loser who saves a gammon is a point better off. A person playing as player 2 can concede, which hands
		// player 1 the game, scored as the board stands
		// A game cut short is a draw, with no victor and nothing to either player
		// the check that cuts a game short counts a turn that's never played
		final int turns = Math.min(turnsPlayed, maxTurns);
		if (cutShort) {
			return new GameStats(null, WinType.DRAW, 0, 0, turns);
		}
		final boolean playerOneWon = liveBoard.hasPlayerWon(aiPlayer1.black) || aiPlayer2.hasConceded();
		final WinType winType = liveBoard.winTypeAgainst(playerOneWon ? aiPlayer2.black : aiPlayer1.black);
		final int points = winType.getPoints();
		return playerOneWon
				? new GameStats(individual1, winType, points, -points, turns)
				: new GameStats(individual2, winType, -points, points, turns);
	}

	/**
	 * Initial roll.
	 * <p>
	 * Keeps rolling the dice until both players have a different score and
	 * therefore a winner
	 */
	private void initialRoll() {

		p1Roll = aiPlayer1.dice.rollOneDie();
		p2Roll = aiPlayer2.dice.rollOneDie();

		if (p1Roll == p2Roll) {
			initialRoll();
			return;
		}

		openingDice = List.of(p1Roll, p2Roll);
	}

	/**
	 * The dice for the next turn: the opening roll's dice for the first move of the game, a new roll after that
	 */
	private List<Integer> nextRoll(final Player player) {
		if (openingDice != null) {
			final List<Integer> opening = openingDice;
			openingDice = null;
			return opening;
		}
		return player.dice.RollTwoDice();
	}

	/**
	 * Slows the game down so it can be watched, needed if both are AI. If the thread is interrupted while waiting, it
	 * stays marked as interrupted, so the game loop sees that and stops
	 */
	private void pauseToShowTheBoard() {
		if (showsOnBoardWindow) {
			try {
				Thread.sleep(timeDelay);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Run method
	 * <p>
	 * Contains the game loop.
	 */
	@Override
	public void run() {

		// Printing game info out to console
		logger.trace("Welcome! New game with two players");

		if (!areBothAIs) {
			logger.trace("You are Player 2");
		}

		if (isP1Black) {
			logger.trace("Player 1 is BLACK, Player 2 is RED");
		} else {
			logger.trace("Player 1 is RED, Player 2 is BLACK");
		}
		logger.trace("Initial dice throw commencing..");

		// initial roll method, decides who moves first
		initialRoll();

		logger.trace("Player 1 rolls: {} Player 2: {}", p1Roll, p2Roll);
		if (!areBothAIs) {
			aiPlayer2.tell("Opening roll: player 1 rolled " + p1Roll + ", you rolled " + p2Roll + ". "
					+ (p1Roll > p2Roll ? "Player 1 moves" : "You move") + " first, using those two dice.");
		}

		boolean playerOnesTurn = p1Roll > p2Roll;
		logger.trace("Player {} has won the roll", playerOnesTurn ? 1 : 2);

		while (gameActive) {
			if (areBothAIs && shouldStop()) {
				cutShort = true;
				gameActive = false;
			} else if (playerOnesTurn) {
				aiTurn(aiPlayer1);
			} else if (areBothAIs) {
				aiTurn(aiPlayer2);
			} else {
				personTurn();
			}
			playerOnesTurn = !playerOnesTurn;
		}
	}

	/**
	 * Whether a game between two AIs should end without a winner: it's gone on too long, or the GA has given up on it
	 * and interrupted it, such as after another game failed
	 */
	private boolean shouldStop() {
		if (turnsPlayed++ >= maxTurns) {
			logger.debug("Game cut short after {} turns", maxTurns);
			return true;
		}
		if (Thread.currentThread().isInterrupted()) {
			logger.debug("Game stopped as its thread was interrupted");
			return true;
		}
		return false;
	}

	private void showBoard() {
		if (showsOnBoardWindow) {
			liveBoard.printBoardGUI();
		}
	}

	/**
	 * An AI's turn. If a person is playing, the AI is player 1: they're told its roll here, and their own turn starts by
	 * showing the board it left
	 */
	private void aiTurn(final AiPlayer player) {
		final Board before = liveBoard;
		liveBoard = new Board(player.AiTurn(liveBoard, nextRoll(player)));
		if (!areBothAIs) {
			aiPlayer2.tell("Player 1, " + player.getColourString() + ", rolled " + player.movesLeft.moves
					+ (liveBoard.equals(before) ? " and couldn't move." : "."));
		}
		showBoard();
		observer.turnPlayed(liveBoard);

		if (liveBoard.hasPlayerWon(player.black)) {
			gameActive = false;
			logger.trace("{} has won", player.getColourString());
			if (!areBothAIs) {
				aiPlayer2.tell("You lost, player 1 has won " + getGameStats().getWinType().describe() + ".");
				aiPlayer2.showBoard(liveBoard);
			}
		} else {
			pauseToShowTheBoard();
		}
	}

	/**
	 * A person's turn as player 2, waiting for them to choose their moves
	 */
	private void personTurn() {
		aiPlayer2.turn(liveBoard, nextRoll(aiPlayer2));
		showBoard();
		observer.turnPlayed(liveBoard);

		if (aiPlayer2.hasConceded()) {
			gameActive = false;
			aiPlayer2.tell("Player 1 wins " + getGameStats().getWinType().describe() + ".");
		} else if (liveBoard.hasPlayerWon(aiPlayer2.black)) {
			gameActive = false;
			aiPlayer2.tell("You've won " + getGameStats().getWinType().describe() + "!");
		}
	}

}