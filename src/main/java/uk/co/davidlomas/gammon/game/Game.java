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

import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.settings.GameSettings;

/**
 * The Class Game.
 *
 * Contains the game loop and creates a new board, players etc
 *
 * This class just needs two Individuals passed in to it to play a game, if null
 * the player will pick random moves
 *
 * To get a human player, in the backgammon.settings package change the
 * gameSettings class, areBothAIs = false
 *
 * @author David Lomas
 */
class Game implements Runnable {

  private final static Logger logger = LoggerFactory.getLogger(Game.class);
  private static final double BORE_PIECE_SCORE_MULTIPLIER = 0.04;

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

  /**
   * Instantiates a new game.
   */
  Game(final Individual individual1, final Individual individual2) {

    liveBoard = new Board();
    liveBoard.printBoardGUI();

    gameActive = true;
    this.individual1 = individual1;
    this.individual2 = individual2;

    aiPlayer1 = new AiPlayer(isP1Black, individual1);
    aiPlayer2 = new AiPlayer(!isP1Black, individual2);
  }

  GameStats getGameStats() {
    final Individual gameVictor;
    final double playerOneScore;
    final double playerTwoScore;

    if (liveBoard.hasPlayerWon(aiPlayer1.black)) {
      final double reductions = liveBoard.howManyHasPlayerBore(aiPlayer2.black) * BORE_PIECE_SCORE_MULTIPLIER;
      playerOneScore = 1 - reductions;
      playerTwoScore = reductions;
      gameVictor = individual1;
    } else {
      final double reductions = liveBoard.howManyHasPlayerBore(aiPlayer1.black) * BORE_PIECE_SCORE_MULTIPLIER;
      playerTwoScore = 1 - reductions;
      playerOneScore = reductions;
      gameVictor = individual2;
    }
    return new GameStats(gameVictor, playerOneScore, playerTwoScore);
  }

  /**
   * Initial roll.
   *
   * Keeps rolling the dice until both players have a different score and
   * therefore a winner
   */
  private void initialRoll() {

    p1Roll = aiPlayer1.dice.rollOneDie();
    p2Roll = aiPlayer2.dice.rollOneDie();

    if (p1Roll == p2Roll) {
      initialRoll();
    }

    liveBoard.isInitialMove = true;
  }

  private boolean isGameActive() {
    return gameActive;
  }

  /**
   * Run method
   *
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

      // Player1's Turn if the game is still active and it is now their
      // turn
      if (playerOnesTurn && isGameActive()) {

        // creating the new board with the chosen moves etc
        liveBoard = new Board(aiPlayer1.AiTurn(liveBoard));

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

          // if they haven't won then continue
        } else {

          if (GameSettings.getDisplayGUI()) {
            // just to slow the visual process down, needed if both
            // are AI
            try {
              Thread.sleep(timeDelay);
            } catch (final InterruptedException e) {
              e.printStackTrace();
            }
          }

          // if the player has not won the game, change boolean to
          // allow the other player to take his turn
          playerOnesTurn = false;
        }

        // Player2's Turn (could also be an AI)
      } else if (!playerOnesTurn && isGameActive()) {

        // if you have selected them both to be AI's
        if (areBothAIs) {

          // get the new board with the selected moves in
          liveBoard = new Board(aiPlayer2.AiTurn(liveBoard));

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

            if (GameSettings.getDisplayGUI()) {
              // just to slow the visual process down, needed if
              // both are AI
              try {
                Thread.sleep(timeDelay);
              } catch (final InterruptedException e) {
                e.printStackTrace();
              }
            }

            // if the player has not won the game, change boolean to
            // allow the other player to take his turn
            playerOnesTurn = true;
          }

          // Option for the human player
        } else {

          // Running the turn method, waits for user input on selected
          // move
          aiPlayer2.turn(liveBoard);

          // displaying new board
          if (GameSettings.getDisplayGUI()) {
            liveBoard.printBoardGUI();
          }

          // checking if the player has now won
          // gameActive is false if they have, and they are informed
          if (liveBoard.hasPlayerWon(aiPlayer2.black)) {

            gameActive = false;
            JOptionPane.showMessageDialog(null, "Player 2 has won! (Black=" + aiPlayer2.black + ")");

          } else {
            playerOnesTurn = true;
          }
        }
        liveBoard.isInitialMove = false;
      }
    }
  }

}