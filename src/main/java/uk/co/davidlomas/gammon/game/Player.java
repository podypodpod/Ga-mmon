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

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Player. the base class for players and contains code that only
 * covers human players.
 *
 * two players will always be made, the AI will extend this to use AIPlayers
 * though
 *
 * @author David Lomas
 */
public class Player {

  private final static Logger logger = LoggerFactory.getLogger(Player.class);

  public MovesList movesLeft;
  protected Boolean black;
  final DiceList dice;
  boolean turnOver;

  /**
   * Instantiates a new player.
   */
  public Player(final boolean black) {
    this.black = black;
    movesLeft = new MovesList();
    dice = new DiceList();
  }

  /**
   * Returns true if the move suggested is a legal move
   */
  public boolean isMovePossible(final int from, final int to, final Board liveBoard) {

    if (!passesBasicChecks(to, from, liveBoard)) {
      return false;
    }

    if (!movingToZeroPieces(to)) {
      if (isValidLength(from, to)) {
        if (validDestination(to, liveBoard)) {
          return true;
        }
      }

      // BEARING, bearing is counted as point -1 or 26
    } else if (tryingAndCanBear(to, from, liveBoard)) {

      if (moveRequiredIsAvailable(to, from)) {
        return true;
      }
    }
    return false;
  }

  private boolean isValidLength(final int from, final int to) {
    for (final int move : movesLeft.moves) {
      if (move == distanceBetween(from, to)) {
        return true;
      }
    }
    return false;
  }

  /**
   * returns the distance between the two points
   */
  public int distanceBetween(final int a, final int b) {
    if (a > b) {
      return a - b;
    } else {
      return b - a;
    }
  }

  /**
   * returns true if start position has a piece on it
   */
  private boolean hasAPieceAtStart(final int from, final Board liveBoard) {
    return liveBoard.Points[from].numEither() > 0 && liveBoard.Points[from].getCol() == black;
  }

  /**
   * Move piece.
   */
  public void movePiece(final int from, final int to, final Board liveBoard) {

    // if not bearing positions
    if (to != -1 && to != 26) {

      // if there is an opposing piece there
      if (liveBoard.Points[to].getCol() != black && liveBoard.Points[to].numEither() == 1) {

        liveBoard.Points[from].removePiece(black);
        liveBoard.Points[to].removePiece(!black);
        liveBoard.Points[to].addPiece(black);

        if (black) {
          liveBoard.Points[0].addRedPiece();
        } else {
          liveBoard.Points[25].addBlackPiece();
        }

      } else {// if its empty
        liveBoard.Points[from].removePiece(black);
        liveBoard.Points[to].addPiece(black);

      }
    } else {// ELSE WE ARE BEARING THE PIECE OFF
      liveBoard.Points[from].removePiece(black);
      liveBoard.addToBear(black);

    }
  }

  /**
   * returns true if the piece is moving in the right direction
   */
  private boolean movingInTheRightDirection(final int to, final int from) {
    return black && to < from || !black && to > from;
  }

  /**
   * returns true if not moving to a zero space
   */
  private boolean movingToZeroPieces(final int to) {
    return !(to != 0 && to != 25 && to != -1 && to != 26);
  }

  /**
   * returns true if they are not moving the zero piece which needs to be
   * moved (only legal move)
   */
  private boolean notMovingTheZero(final int from) {
    return black && from != 25 || !black && from != 0;
  }

  /**
   * returns true if it passes basic checks of backgammon rules
   */
  public boolean passesBasicChecks(final int to, final int from, final Board liveBoard) {

    // FROM piece
    if (!withinArrayBounds(to, from)) {
      return false;
    }

    if (!hasAPieceAtStart(from, liveBoard)) {
      return false;
    }

    if (!movingInTheRightDirection(to, from)) {
      return false;
    }

    if (liveBoard.isThereZero(black)) {
      if (notMovingTheZero(from)) {
        return false;
      }
    }
    return true;
  }

  /**
   * returns true if the distance between the two points matches the moves
   * left they have
   */
  private boolean moveRequiredIsAvailable(final int to, final int from) {

    final int y;
    if (to == -1) {
      y = 1;
    } else {
      y = -1;
    }

    // looping through the moves Left array to check against what they have
    // asked for
    for (final int move : movesLeft.moves) {
      if (move >= distanceBetween(from, to + y)) {
        return true;
      }
    }
    return false;
  }

  /**
   * returns true if the player is trying to bear and he can bear at this time
   */
  private boolean tryingAndCanBear(final int to, final int from, final Board liveBoard) {
    return (to == -1 || to == 26) && liveBoard.canPlayerBear(liveBoard.Points[from].getCol());
  }

  /**
   * The players turn.
   */
  void turn(final Board liveBoard) {

    turnOver = false;

    logger.trace("------------Your Turn!-----------------");

    // Rolling the dice
    movesLeft.setTo(dice.RollTwoDice());

    logger.trace("Player has : " + movesLeft.toString());

    // ASKING WHAT TO DO
    while (!turnOver) {

      logger.trace("What do you want to do?, " + movesLeft.size() + " moves left");

      logger.trace("1) Move a piece");
      logger.trace("2) Bear off a piece");
      logger.trace("3) Skip or Finish go");
      logger.trace("4) Concede");

      final Scanner Scanner = new Scanner(System.in);

      final int option = Scanner.nextInt();

      // MOVE A piece
      if (option == 1) {
        logger.trace("from which point (1,2,3 etc): ");
        final int from = Scanner.nextInt();
        logger.trace("to which point (1,2,3,bear=-1 etc)");
        final int to = Scanner.nextInt();

        // IF ITS POSSIBLE
        if (isMovePossible(from, to, liveBoard)) {
          // MOVE THE PIECE
          movePiece(from, to, liveBoard);
          // REMOVE THE "MOVE"
          movesLeft.remove(distanceBetween(from, to));

          if (movesLeft.size() == 0) {
            turnOver = true;
          }
        } else {
          logger.trace("invalid move");
        }
        // SKIP GO
      } else if (option == 2) {
        if (liveBoard.canPlayerBear(black)) {

          logger.trace("Bear which piece?");

          final int bearPiece = Scanner.nextInt();
          if (isMovePossible(bearPiece, -1, liveBoard)) {
            liveBoard.bearPiece(bearPiece, black);
          }
        } else {
          logger.trace("Can not bear pieces yet");
        }
      } else if (option == 3) {
        turnOver = true;
        // CONCEDING
      } else if (option == 4) {
        turnOver = true;

      }
    }
  }

  /**
   * returns true if destination isn't filled or no more than 1 chip
   */
  private boolean validDestination(final int to, final Board liveBoard) {
    return liveBoard.Points[to].getCol() == black
        || liveBoard.Points[to].getCol() != black && liveBoard.Points[to].numEither() <= 1;
  }

  /**
   * returns if its within set array bounds
   */
  private boolean withinArrayBounds(final int to, final int from) {
    return from >= 0 && from <= 25 && to <= 26 && to >= -1;
  }
}
