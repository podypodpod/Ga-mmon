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

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.genes.Individual;

/**
 * The Class BoardList.
 *
 * Holds a collection of board states allowing the AI to then loop through them
 *
 * @author David Lomas
 */
public class BoardList {

  private final static Logger logger = LoggerFactory.getLogger(BoardList.class);

  /**
   * The all possible moves.
   */
  private final List<Board> boardList;

  /**
   * The individual that will make the move.
   */
  private final Individual individual;

  /**
   * The beval.
   */
  private final BoardEvaluator boardEvaluator;

  /**
   * BoardList
   *
   * Instantiates a new board list.
   *
   * @param individual the individual
   */
  BoardList(final Individual individual) {
    boardList = new ArrayList<>();
    this.individual = individual;
    boardEvaluator = new BoardEvaluator();
  }

  /**
   * addBoard Adds the board passed in.
   *
   * @param b the board
   */
  private void addBoard(final Board b) {
    boardList.add(b);
  }

  /**
   * addBoard adds the board passed in with the move also passed in applied to
   * it first. It then checks if any more moves can be made
   *
   * @param b the board passed in
   * @param to the destination of move
   * @param from the start of the move
   * @param p1 the player object
   * @param mg the move generator
   */
  void addBoard(final Board b, final int to, final int from, final AiPlayer p1, final MoveGenerator mg) {

    // move the piece on this new board
    p1.movePiece(from, to, b);

    if (p1.movesLeft.size() > 1) {
      p1.movesLeft.moves.remove(Integer.valueOf(p1.distanceBetween(from, to)));
    } else {
      p1.movesLeft.moves.remove(0);
      p1.movesLeft.moves.add(0);
    }

    // check if any further moves can be made
    if (!(p1.movesLeft.size() == 1 && p1.movesLeft.getNext() == 0 || p1.movesLeft.size() == 0)) {
      mg.generateMoves(b, p1);
    }

    // add the board to the list if there is not a duplicate
    if (!thereIsADuplicate(b)) {
      addBoard(b);
    }
  }

  /**
   * clearList
   *
   * Empties the list.
   */
  void clearList() {
    boardList.clear();
  }

  /**
   * Checks for contents.
   *
   * @return true, if successful
   */
  public boolean hasContents() {
    return boardList.size() > 0;
  }

  private boolean hasDiceRoll(final int x) {
    return boardEvaluator.currentPlayer.movesLeft.contains(x);
  }

  /**
   * Individual decision.
   *
   * Is called by select board and it is where the individuals attributes
   * influence the chosen board
   *
   * e.g. higher aggression chance will produce more pieces being taken
   *
   * @param currentBoard the current board
   * @param p the player
   * @return the board chosen
   */
  private Board individualDecision(final Board currentBoard, final AiPlayer p) {

    // if returned null it doesn't change
    Board chosenBoard = null;

    // Giving the board evaluator the info it needs
    boardEvaluator.setBoard(currentBoard);
    boardEvaluator.setPlayer(p);

    logger.trace("board list size: " + boardList.size());

    // END GAME
    if (currentBoard.canPlayerBear(p.black)) {

      // Attribute 0 = bear a piece
      if (p.getIndividual().getAttribute(0).getValue() > (int) (Math.random() * 100)) {
        // try to bear
        for (final Board x : boardList) {
          if (boardEvaluator.hasAPieceBeenBore(x)) {
            chosenBoard = x;
            break;
          }
        }
        // Attribute 5 = spread pieces
      } else if (p.getIndividual().getAttribute(5).getValue() > (int) (Math.random() * 100)) {
        // try to spread a piece
        for (final Board x : boardList) {
          if (boardEvaluator.hasAPieceBeenSpread(x)) {
            chosenBoard = x;
            break;
          }
        }
      }
      // MID/EARLY GAME
    } else if (chosenBoard == null && !currentBoard.isInitialMove) {

      // Attribute 1 = take a piece
      if (p.getIndividual().getAttribute(1).getValue() > (int) (Math.random() * 100)) {
        // try to take a piece
        for (final Board x : boardList) {
          if (boardEvaluator.hasAPieceBeenTaken(x)) {
            chosenBoard = x;
            break;
          }
        }

        // Attribute 2 = doubleUpAPiece
      } else if (p.getIndividual().getAttribute(2).getValue() > (int) (Math.random() * 100)) {
        // try to double up a piece
        for (final Board x : boardList) {
          if (boardEvaluator.hasABlotBeenDoubled(x)) {
            chosenBoard = x;
            break;
          }
        }

        // Attribute 3 = blockAnOpponent
      } else if (p.getIndividual().getAttribute(3).getValue() > (int) (Math.random() * 100)) {
        // try to blockAnOpponent
        for (final Board x : boardList) {
          if (boardEvaluator.hasTheOpponentBeenBlocked(x)) {
            chosenBoard = x;
            break;
          }
        }
        // Attribute 4 = movingAPieceSolo
      } else if (p.getIndividual().getAttribute(4).getValue() > (int) (Math.random() * 100)) {
        // try to move a piece solo
        for (final Board x : boardList) {
          if (boardEvaluator.hasAPieceBeenMovedSolo(x)) {
            chosenBoard = x;
            break;
          }
        }

        // Attribute 6 = addACheckerToAStack
      } else if (p.getIndividual().getAttribute(6).getValue() > (int) (Math.random() * 100)) {
        // try to add a checker to a stack
        for (final Board x : boardList) {
          if (boardEvaluator.hasAStackBeenAddedTo(x)) {
            chosenBoard = x;
            break;
          }
        }
      }
      // FIRST MOVE OF THE GAME
    } else if (chosenBoard == null && currentBoard.isInitialMove) {

      // has a roll of TWO and ONE
      if (hasDiceRoll(2) && hasDiceRoll(1)) {

        // ATTRIBUTE 7 = twoOneSplitPlayInitialMove
        if (p.getIndividual().getAttribute(7).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isTwoOneSplitPlayInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
          // ATTRIBUTE 8 = twoOneSlotPlayInitialMove
        } else if (p.getIndividual().getAttribute(8).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isTwoOneSlotPlayInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of THREE and ONE
      } else if (hasDiceRoll(3) && hasDiceRoll(1)) {

        // ATTRIBUTE 9 = threeOneInitialMove
        if (p.getIndividual().getAttribute(9).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isThreeOneInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
        }

        // A Roll of THREE and a TWO
      } else if (hasDiceRoll(3) && hasDiceRoll(1)) {

        // ATTRIBUTE 10 = threeTwoSplitInitialMove
        if (p.getIndividual().getAttribute(10).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isThreeTwoSplitInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
          // ATTRIBUTE 11 = threeTwoOffenceInitialMove
        } else if (p.getIndividual().getAttribute(11).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isThreeTwoOffenceInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
        }

        // A Roll of FOUR and a ONE
      } else if (hasDiceRoll(4) && hasDiceRoll(1)) {

        // ATTRIBUTE 12 = fourOneInitialMove
        if (p.getIndividual().getAttribute(12).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFourOneInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }

          // ATTRIBUTE 13 = fourOneInitialMove
        } else if (p.getIndividual().getAttribute(13).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFourOneInitialMoveAlt(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of FOUR and TWO
      } else if (hasDiceRoll(4) && hasDiceRoll(2)) {

        // ATTRIBUTE 14 = fourTwoInitialMove
        if (p.getIndividual().getAttribute(14).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFourTwoInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of FOUR and THREE
      } else if (hasDiceRoll(4) && hasDiceRoll(3)) {

        // ATTRIBUTE 15 = fourThreeInitialMoveSplit
        if (p.getIndividual().getAttribute(15).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFourThreeInitialMoveSplit(x)) {
              chosenBoard = x;
              break;
            }
          }
          // ATTRIBUTE 16 = fourThreeInitialMoveBlock
        } else if (p.getIndividual().getAttribute(16).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFourThreeInitialMoveBlock(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of FIVE and ONE
      } else if (hasDiceRoll(5) && hasDiceRoll(1)) {

        // ATTRIBUTE 17 = fiveOneInitialMove
        if (p.getIndividual().getAttribute(17).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFiveOneInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
          // ATTRIBUTE 18 = fiveOneInitialMoveAlt
        } else if (p.getIndividual().getAttribute(18).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFiveOneInitialMoveAlt(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of FIVE and TWO
      } else if (hasDiceRoll(5) && hasDiceRoll(2)) {

        // ATTRIBUTE 19 = fiveTwoInitialMove
        if (p.getIndividual().getAttribute(19).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFiveTwoInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
          // ATTRIBUTE 20 = fiveTwoInitialMoveRisk
        } else if (p.getIndividual().getAttribute(20).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFiveTwoInitialMoveRisk(x)) {
              chosenBoard = x;
              break;
            }
          }
        }

        // A Roll of FIVE and THREE
      } else if (hasDiceRoll(5) && hasDiceRoll(3)) {

        // ATTRIBUTE 21 = fiveTwoInitialMove
        if (p.getIndividual().getAttribute(21).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFiveThreeInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of FIVE and THREE
      } else if (hasDiceRoll(5) && hasDiceRoll(4)) {

        // ATTRIBUTE 21 = fiveTwoInitialMove
        if (p.getIndividual().getAttribute(21).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFiveThreeInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of FIVE and FOUR
      } else if (hasDiceRoll(5) && hasDiceRoll(4)) {

        // ATTRIBUTE 22 = fiveFourInitialMoveAgr
        if (p.getIndividual().getAttribute(22).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFiveFourInitialMoveAgr(x)) {
              chosenBoard = x;
              break;
            }
          }
          // ATTRIBUTE 23 = fiveFourInitialMoveBal
        } else if (p.getIndividual().getAttribute(23).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isFiveFourInitialMoveBal(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of SIX and ONE
      } else if (hasDiceRoll(6) && hasDiceRoll(1)) {

        // ATTRIBUTE 24 = sixOneInitialMove
        if (p.getIndividual().getAttribute(24).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isSixOneInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of SIX and TWO
      } else if (hasDiceRoll(6) && hasDiceRoll(2)) {

        // ATTRIBUTE 25 = sixTwoInitialMove
        if (p.getIndividual().getAttribute(25).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isSixTwoInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
          // ATTRIBUTE 26 = sixTwoInitialMoveAgr
        } else if (p.getIndividual().getAttribute(26).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isSixTwoInitialMoveAgr(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of SIX and THREE
      } else if (hasDiceRoll(6) && hasDiceRoll(3)) {

        // ATTRIBUTE 27 = sixThreeInitialMove
        if (p.getIndividual().getAttribute(27).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isSixThreeInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
          // ATTRIBUTE 28 = sixThreeInitialMoveSplit
        } else if (p.getIndividual().getAttribute(28).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isSixThreeInitialMoveSplit(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
        // A Roll of SIX and FOUR
      } else if (hasDiceRoll(6) && hasDiceRoll(4)) {

        // ATTRIBUTE 29 = sixFourInitialMove
        if (p.getIndividual().getAttribute(29).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isSixFourInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
          // ATTRIBUTE 30 = sixFourInitialMoveSplit
        } else if (p.getIndividual().getAttribute(30).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isSixFourInitialMoveSplit(x)) {
              chosenBoard = x;
              break;
            }
          }
        }

        // A Roll of SIX and FIVE
      } else if (hasDiceRoll(6) && hasDiceRoll(5)) {

        // ATTRIBUTE 31 = sixFiveInitialMove
        if (p.getIndividual().getAttribute(31).getValue() > (int) (Math.random() * 100)) {
          for (final Board x : boardList) {
            if (boardEvaluator.isSixFiveInitialMove(x)) {
              chosenBoard = x;
              break;
            }
          }
        }
      }
    }

    // if no specific move has been found - pick a random available one
    if (chosenBoard == null) {
      final int x = (int) (Math.random() * boardList.size());
      chosenBoard = boardList.get(x);
      logger.trace("Random move chosen");
    }
    return chosenBoard;
  }

  /**
   * Prints the board list.
   */
  public void printBoardList() {
    int counter = 0;
    for (final Board board : boardList) {
      counter++;
      logger.trace("----------------------------");
      logger.trace("Board num {}", counter);
      board.printBoard();
      logger.trace("----------------------------");
    }
  }

  /**
   * Select board. This will select the best board from the list for the
   * player to use
   *
   * @param currentBoard the current board
   * @param p the p
   * @return the board that has been selected
   */
  Board selectBoard(final Board currentBoard, final AiPlayer p) {

    if (boardList.size() > 0) {

      // Use the individual to decide what to do next
      if (individual != null) {
        // use the method individual decision to decide which to pick
        return individualDecision(currentBoard, p);

      } else {
        // if the player personality = null then just pick at random, as
        // this means its the basic opposition to test against
        final int x = (int) (Math.random() * boardList.size());
        logger.trace("board list size: {} ", boardList.size());
        return boardList.get(x);

      }

    } else {
      // if there are no possible new boards(no possible moves) return
      // null
      logger.trace("No possible moves");
      return null;

    }

  }

  /**
   * There is a duplicate. checks if there is already a duplicate of the board
   * passed in already present in the list
   *
   * @param b the board it is checking against
   * @return true, if there is a duplicate
   */
  private boolean thereIsADuplicate(final Board b) {
    for (final Board board : boardList) {
      if (board.equals(b)) {
        return true;
      }
    }
    return false;
  }
}