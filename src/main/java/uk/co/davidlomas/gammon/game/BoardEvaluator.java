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

/**
 * The Class BoardEvaluator.
 *
 * Useful methods for picking boards from the board list e.g. pick a board where
 * it will take a piece
 *
 * Allows the AI to loop through all the possible moves and pick a certain one
 *
 * @author David Lomas
 */
public class BoardEvaluator {

  private final static Logger logger = LoggerFactory.getLogger(BoardEvaluator.class);

  private Board liveBoard;
  AiPlayer currentPlayer;
  private int myZero, opponentZero;

  public int getMyZero() {
    return myZero;
  }

  /**
   * hasABlotBeenDoubled
   *
   * Checks to see if the board passed in has had a single piece on its own (a
   * blot) doubled and therefore safe from being taken
   *
   * @param newBoard the board we are checking
   * @return true if it has been doubled
   */
  boolean hasABlotBeenDoubled(final Board newBoard) {
    if (liveBoard.getNumOfBlots(currentPlayer.black) > newBoard.getNumOfBlots(currentPlayer.black)) {
      logger.trace("Blot doubled");
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks for a piece that has been bore.
   *
   * @param newBoard the new board
   * @return true, if successful
   */
  boolean hasAPieceBeenBore(final Board newBoard) {
    if (liveBoard.howManyHasPlayerBore(currentPlayer.black) < newBoard.howManyHasPlayerBore(currentPlayer.black)) {
      logger.trace("Piece has been BORE");
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks for a piece being taken.
   *
   * @param newBoard the new board
   * @return true, if successful
   */
  boolean hasAPieceBeenTaken(final Board newBoard) {
    if (liveBoard.Points[opponentZero].numEither() < newBoard.Points[opponentZero].numEither()) {
      logger.trace("Piece has been taken.");
      return true;
    } else {
      return false;
    }
  }

  /**
   * hasAPieceBeenMovedSolo
   *
   * Checks to see if the board passed in has had a single piece on its own (a
   * blot) created
   *
   * @param newBoard the board we are checking
   * @return true if it has been doubled
   */
  public boolean hasAPieceBeenMovedSolo(final Board newBoard) {
    if (liveBoard.getNumOfBlots(currentPlayer.black) < newBoard.getNumOfBlots(currentPlayer.black)) {
      logger.trace("Piece moved solo");
      return true;
    } else {
      return false;
    }
  }

  /**
   * hasAPieceBeenSpread
   *
   * checks to see if the board passed in has moved a piece to another spot on
   * the home board when bearing is applicable
   *
   * @param newBoard the board we are checking
   * @return true if it has been spread
   */
  public boolean hasAPieceBeenSpread(final Board newBoard) {
    if (liveBoard.getNumOfHomePointsCovered(currentPlayer.black) < newBoard
        .getNumOfHomePointsCovered(currentPlayer.black)) {
      logger.trace("Piece spread");
      return true;
    } else {
      return false;
    }
  }

  /**
   * hasAStackBeenAddedTo
   *
   * checks to see if the board passed in has moved a piece on top of an
   * existing stack
   *
   * @param newBoard the board we are checking
   * @return true if it has been blocked
   */
  public boolean hasAStackBeenAddedTo(final Board newBoard) {
    if (liveBoard.getNumberOfCheckersOnStacks(currentPlayer.black) < newBoard
        .getNumberOfCheckersOnStacks(currentPlayer.black)) {
      logger.trace("Added to stack");
      return true;
    } else {
      return false;
    }
  }

  /**
   * hasTheOpponentBeenBlocked
   *
   * checks to see if the board passed in has moved a piece to block the
   * oponent
   *
   * @param newBoard the board we are checking
   * @return true if it has been blocked
   */
  boolean hasTheOpponentBeenBlocked(final Board newBoard) {
    if (liveBoard.getNumberOfPiecesSurroundOpponent(currentPlayer.black) < newBoard
        .getNumberOfPiecesSurroundOpponent(currentPlayer.black)) {
      logger.trace("Blocking opponent");
      return true;
    } else {
      return false;
    }
  }

  private boolean ifPointHasPieces(final Board newBoard, final int point, final int num) {
    return newBoard.Points[point].numEither() == num;
  }

  /**
   * isFiveFourInitialMoveAgr
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFiveFourInitialMoveAgr(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 13, 3) && ifPointHasPieces(newBoard, 9, 1)
          && ifPointHasPieces(newBoard, 8, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 12, 3) && ifPointHasPieces(newBoard, 16, 1)
          && ifPointHasPieces(newBoard, 17, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isFiveFourInitialMoveBal
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFiveFourInitialMoveBal(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 20, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 8, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 5, 1)
          && ifPointHasPieces(newBoard, 12, 4) && ifPointHasPieces(newBoard, 17, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isFiveOneInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFiveOneInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 23, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 8, 4)) {
        logger.trace("isFiveOneInitialMove");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 2, 1)
          && ifPointHasPieces(newBoard, 12, 4) && ifPointHasPieces(newBoard, 17, 4)) {
        logger.trace("isFiveOneInitialMove");
        return true;
      }
    }
    return false;
  }

  /**
   * isFiveOneInitialMoveAlt
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFiveOneInitialMoveAlt(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 6, 4) && ifPointHasPieces(newBoard, 5, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 8, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 19, 4) && ifPointHasPieces(newBoard, 20, 1)
          && ifPointHasPieces(newBoard, 12, 4) && ifPointHasPieces(newBoard, 17, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isFiveThreeInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFiveThreeInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 8, 2) && ifPointHasPieces(newBoard, 6, 4)
          && ifPointHasPieces(newBoard, 3, 2)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 17, 2) && ifPointHasPieces(newBoard, 19, 4)
          && ifPointHasPieces(newBoard, 22, 2)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isFiveTwoInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFiveTwoInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 11, 1) && ifPointHasPieces(newBoard, 13, 3)
          && ifPointHasPieces(newBoard, 8, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 14, 1) && ifPointHasPieces(newBoard, 12, 3)
          && ifPointHasPieces(newBoard, 17, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isFiveTwoInitialMoveRisk
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFiveTwoInitialMoveRisk(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 22, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 8, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red player
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 3, 1)
          && ifPointHasPieces(newBoard, 12, 4) && ifPointHasPieces(newBoard, 17, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isFourTwoInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFourOneInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 23, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 9, 1)) {
        logger.trace("isFourOneInitialMove");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 2, 1)
          && ifPointHasPieces(newBoard, 12, 4) && ifPointHasPieces(newBoard, 16, 1)) {
        logger.trace("isFourOneInitialMove");
        return true;
      }
    }
    return false;
  }

  /**
   * isFourTwoInitialMoveAlt
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFourOneInitialMoveAlt(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 6, 4) && ifPointHasPieces(newBoard, 5, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 9, 1)) {
        logger.trace("isFourOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 19, 4) && ifPointHasPieces(newBoard, 20, 1)
          && ifPointHasPieces(newBoard, 12, 4) && ifPointHasPieces(newBoard, 16, 1)) {
        logger.trace("isFourOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isFourThreeInitialMoveBlock
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFourThreeInitialMoveBlock(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 13, 3) && ifPointHasPieces(newBoard, 10, 1)
          && ifPointHasPieces(newBoard, 9, 1)) {
        logger.trace("isFourThreeInitialMoveBlock");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 12, 3) && ifPointHasPieces(newBoard, 15, 1)
          && ifPointHasPieces(newBoard, 16, 1)) {
        logger.trace("isFourThreeInitialMoveBlock");
        return true;
      }
    }
    return false;
  }

  /**
   * isFourThreeInitialMoveSplit
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFourThreeInitialMoveSplit(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 21, 1)
          && ifPointHasPieces(newBoard, 6, 4) && ifPointHasPieces(newBoard, 9, 1)) {
        logger.trace("isFourThreeInitialMoveSplit");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 4, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 16, 1)) {
        logger.trace("isFourThreeInitialMoveSplit");
        return true;
      }
    }
    return false;
  }

  /**
   * isFourTwoInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isFourTwoInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 8, 2) && ifPointHasPieces(newBoard, 4, 2)
          && ifPointHasPieces(newBoard, 6, 4)) {
        logger.trace("isFourTwoInitialMove");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 17, 2) && ifPointHasPieces(newBoard, 21, 2)
          && ifPointHasPieces(newBoard, 19, 4)) {
        logger.trace("isFourTwoInitialMove");
        return true;
      }
    }
    return false;
  }

  /**
   * isSixFiveInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isSixFiveInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 13, 6)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 12, 6)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isSixFourInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isSixFourInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 14, 1)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 11, 1)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isSixFourInitialMoveSplit
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isSixFourInitialMoveSplit(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 18, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 9, 1)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 7, 1)
          && ifPointHasPieces(newBoard, 12, 4) && ifPointHasPieces(newBoard, 16, 1)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isSixOneInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isSixOneInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 7, 2) && ifPointHasPieces(newBoard, 8, 2)
          && ifPointHasPieces(newBoard, 13, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 17, 2) && ifPointHasPieces(newBoard, 18, 2)
          && ifPointHasPieces(newBoard, 12, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isSixTwoInitialMoveAgr
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isSixThreeInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 15, 1)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 10, 1)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isSixThreeInitialMoveSplit
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isSixThreeInitialMoveSplit(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 18, 1)
          && ifPointHasPieces(newBoard, 10, 1) && ifPointHasPieces(newBoard, 13, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 7, 1)
          && ifPointHasPieces(newBoard, 15, 1) && ifPointHasPieces(newBoard, 12, 4)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isSixTwoInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isSixTwoInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 18, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 1, 1)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 1, 1) && ifPointHasPieces(newBoard, 7, 1)
          && ifPointHasPieces(newBoard, 12, 4) && ifPointHasPieces(newBoard, 14, 1)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isSixTwoInitialMoveAgr
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isSixTwoInitialMoveAgr(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 5, 1)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 12, 4) && ifPointHasPieces(newBoard, 20, 1)) {
        logger.trace("isFiveOneInitialMoveAlt");
        return true;
      }
    }
    return false;
  }

  /**
   * isThreeOneInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isThreeOneInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 6, 4) && ifPointHasPieces(newBoard, 5, 2)
          && ifPointHasPieces(newBoard, 8, 2)) {
        logger.trace("isThreeOneInitialMove");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 19, 4) && ifPointHasPieces(newBoard, 20, 2)
          && ifPointHasPieces(newBoard, 17, 2)) {
        logger.trace("isThreeOneInitialMove");
        return true;
      }
    }
    return false;
  }

  /**
   * isThreeTwoOffenceInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isThreeTwoOffenceInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 13, 3) && ifPointHasPieces(newBoard, 11, 1)
          && ifPointHasPieces(newBoard, 10, 1)) {
        logger.trace("isThreeTwoOffenceInitialMove");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 12, 3) && ifPointHasPieces(newBoard, 14, 1)
          && ifPointHasPieces(newBoard, 15, 1)) {
        logger.trace("isThreeTwoOffenceInitialMove");
        return true;
      }
    }
    return false;
  }

  /**
   * isThreeTwoSplitInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isThreeTwoSplitInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 4, 1) && ifPointHasPieces(newBoard, 1, 1)
          && ifPointHasPieces(newBoard, 12, 4) && ifPointHasPieces(newBoard, 14, 1)) {
        logger.trace("isThreeTwoSplitInitialMove");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 21, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 11, 1)) {
        logger.trace("isThreeTwoSplitInitialMove");
        return true;
      }
    }
    return false;
  }

  /**
   * isTwoOneSlotPlayInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isTwoOneSlotPlayInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 6, 4) && ifPointHasPieces(newBoard, 5, 1)
          && ifPointHasPieces(newBoard, 14, 1) && ifPointHasPieces(newBoard, 12, 4)) {
        logger.trace("isTwoOneSlotPlayInitialMove");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 19, 4) && ifPointHasPieces(newBoard, 20, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 11, 1)) {
        logger.trace("isTwoOneSlotPlayInitialMove");
        return true;
      }
    }
    return false;
  }

  /**
   * isTwoOneSplitPlayInitialMove
   *
   * checks to see if the board passed in has used this particular initial
   * move (move taken from http://www.bkgm.com/openings.html#opening21)
   *
   * @param newBoard the board we are checking
   * @return true if the move has been made
   */
  boolean isTwoOneSplitPlayInitialMove(final Board newBoard) {
    // black
    if (currentPlayer.black) {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 2, 1) && ifPointHasPieces(newBoard, 1, 1)
          && ifPointHasPieces(newBoard, 14, 1) && ifPointHasPieces(newBoard, 12, 4)) {
        logger.trace("isTwoOneSplitPlayInitialMove");
        return true;
      }
      // red
    } else {
      // if it has performed the move
      if (ifPointHasPieces(newBoard, 24, 1) && ifPointHasPieces(newBoard, 23, 1)
          && ifPointHasPieces(newBoard, 13, 4) && ifPointHasPieces(newBoard, 11, 1)) {
        logger.trace("isTwoOneSplitPlayInitialMove");
        return true;
      }
    }
    return false;
  }

  /**
   * Sets the board.
   *
   * @param cb the new board
   */
  public void setBoard(final Board cb) {
    liveBoard = cb;
  }

  /**
   * setMyZero
   */
  private void setMyZero(final int myZero) {
    this.myZero = myZero;
  }

  /**
   * Sets the player.
   *
   * @param p the new player
   */
  public void setPlayer(final AiPlayer p) {
    currentPlayer = p;
    if (p.black) {
      setMyZero(25);
      opponentZero = 0;
    } else {
      setMyZero(0);
      opponentZero = 25;
    }
  }
}