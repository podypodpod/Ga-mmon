/*
 * GNU General Public License
 *
 * This file is part of GA-mmon.
 *
 * GA-mmon is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.genes.Individual;

/**
 * The Class AIPlayer.
 *
 * extends the Player class and implements methods to allow the AI to choose the
 * next move
 *
 * @author David Lomas
 */
public class AiPlayer extends Player {

  private final static Logger logger = LoggerFactory.getLogger(AiPlayer.class);
  private final MoveGenerator moveGenerator;
  private Individual individual;

  /**
   * Clone constructor
   *
   * @param playerToClone the player
   */
  public AiPlayer(final AiPlayer playerToClone) {
    super(playerToClone.black);
    movesLeft = new MovesList(playerToClone.movesLeft);
    black = playerToClone.black;
    turnOver = playerToClone.turnOver;
    moveGenerator = playerToClone.moveGenerator;
  }

  public AiPlayer(final boolean black, final Individual individual) {
    super(black);
    this.individual = individual;
    moveGenerator = new MoveGenerator(individual);
  }

  Board AiTurn(final Board currentBoard) {

    logger.trace("------------AI's Turn!-----------------");

    movesLeft = new MovesList();
    movesLeft.setTo(dice.RollTwoDice());

    logger.trace("Player has {}", movesLeft.toString());

    Board newBoard = moveGenerator.getNextMoveBoard(currentBoard, this);

    // if the board has not changed
    if (newBoard == null) {
      newBoard = currentBoard;
    }
    return newBoard;
  }

  public Individual getIndividual() {
    return individual;
  }
}