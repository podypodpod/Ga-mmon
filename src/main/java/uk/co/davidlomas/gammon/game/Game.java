/*
  GNU General Public License

  This file is part of GA-mmon.

  GA-mmon is free software: you can redistribute it and/or modify it under the
  terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
  A PARTICULAR PURPOSE. See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with
  GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

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
	private final boolean areBothAIs = GameSettings.getAreBothAI();
	private final int timeDelay = GameSettings.getTimeDelay();
	private int p1Roll = 0, p2Roll = 0;
	private final int maxTurns;
	private int turnsPlayed = 0;
	private boolean cutShort = false;

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
		this.maxTurns = maxTurns;

		liveBoard = new Board();
		liveBoard.printBoardGUI();

		gameActive = true;
		this.individual1 = individual1;
		this.individual2 = individual2;

		aiPlayer1 = new AiPlayer(isP1Black, individual1);
		aiPlayer2 = new AiPlayer(!isP1Black, individual2);
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
		if (GameSettings.getDisplayGUI()) {
			try {
				Thread.sleep(timeDelay);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private boolean isGameActive() {
		return gameActive;
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

		// if player 1 has won
		boolean playerOnesTurn = false;
		if (p1Roll > p2Roll) {
			logger.trace("Player 1(Black={}) Has won the roll", aiPlayer1.black);
			playerOnesTurn = true;
			// if player 2 has won
		} else {
			logger.trace("Player 2(Black={}) Has won the roll", aiPlayer2.black);
			playerOnesTurn = false;
		}

		// The Game Loop, while boolean gameActive is true, keep going
		while (isGameActive()) {

			if (areBothAIs && turnsPlayed++ >= maxTurns) {
				logger.debug("Game cut short after {} turns", maxTurns);
				cutShort = true;
				gameActive = false;
				break;
			}
			// the GA interrupts the games it's still playing when it gives up, such as after another game failed
			if (areBothAIs && Thread.currentThread().isInterrupted()) {
				logger.debug("Game stopped as its thread was interrupted");
				cutShort = true;
				gameActive = false;
				break;
			}

			// Player1's Turn if the game is still active and it is now their
			// turn
			if (playerOnesTurn && isGameActive()) {

				// creating the new board with the chosen moves etc
				final Board before = liveBoard;
				liveBoard = new Board(aiPlayer1.AiTurn(liveBoard, nextRoll(aiPlayer1)));

				// a person playing sees the AI's roll, and their own turn starts by showing the board it left
				if (!areBothAIs) {
					aiPlayer2.tell("Player 1, " + aiPlayer1.getColourString() + ", rolled " + aiPlayer1.movesLeft.moves
							+ (liveBoard.equals(before) ? " and couldn't move." : "."));
				}

				// displaying new board
				if (GameSettings.getDisplayGUI()) {
					liveBoard.printBoardGUI();
				}

				// checking if the player has won yet
				// if they have won, then set the gameActive to false and
				// display the message
				if (liveBoard.hasPlayerWon(aiPlayer1.black)) {
					gameActive = false;

					logger.trace("Player 1 has won! (Black={})", aiPlayer1.black);
					if (!areBothAIs) {
						aiPlayer2.tell("You lost, player 1 has won " + getGameStats().getWinType().describe() + ".");
						aiPlayer2.showBoard(liveBoard);
					}

					// if they haven't won then continue
				} else {

					pauseToShowTheBoard();

					// if the player has not won the game, change boolean to
					// allow the other player to take his turn
					playerOnesTurn = false;
				}

				// Player2's Turn (could also be an AI)
			} else if (!playerOnesTurn && isGameActive()) {

				// if you have selected them both to be AI's
				if (areBothAIs) {

					// get the new board with the selected moves in
					liveBoard = new Board(aiPlayer2.AiTurn(liveBoard, nextRoll(aiPlayer2)));

					// displaying new board
					if (GameSettings.getDisplayGUI()) {
						liveBoard.printBoardGUI();
					}

					// checking if the player has now won, if they have then
					// gaveActive is now false and they are told they have won
					if (liveBoard.hasPlayerWon(aiPlayer2.black)) {

						logger.trace("Player 2 has won! (Black={})", aiPlayer2.black);
						gameActive = false;

						// if not, let the game loop continue
					} else {

						pauseToShowTheBoard();

						// if the player has not won the game, change boolean to
						// allow the other player to take his turn
						playerOnesTurn = true;
					}

					// Option for the human player
				} else {

					// Running the turn method, waits for user input on selected
					// move
					aiPlayer2.turn(liveBoard, nextRoll(aiPlayer2));

					// displaying new board
					if (GameSettings.getDisplayGUI()) {
						liveBoard.printBoardGUI();
					}

					// checking if the player has now won, or given up
					// gameActive is false if so, and they are informed
					if (aiPlayer2.hasConceded()) {

						gameActive = false;
						aiPlayer2.tell("Player 1 wins " + getGameStats().getWinType().describe() + ".");

					} else if (liveBoard.hasPlayerWon(aiPlayer2.black)) {

						gameActive = false;
						aiPlayer2.tell("You've won " + getGameStats().getWinType().describe() + "!");

					} else {
						playerOnesTurn = true;
					}
				}
			}
		}
	}

}