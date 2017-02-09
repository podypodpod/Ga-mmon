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

import java.lang.reflect.MalformedParametersException;
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
   * The board Evaluator.
   */
  private final BoardEvaluator boardEvaluator;

  /**
   * BoardList
   *
   * Instantiates a new board list.
   *
   * @param individual the individual
   */
  public BoardList(final Individual individual) {
    this.individual = individual;
    boardList = new ArrayList<>();
    boardEvaluator = new BoardEvaluator();
  }

  /**
   * addBoardWithNextMove Adds the board passed in.
   *
   * @param b the board
   */
  public void addBoardWithNextMove(final Board b) {
    boardList.add(b);
  }

  /**
   * addBoardWithNextMove adds the board passed in with the move also passed in applied to
   * it first. It then checks if any more moves can be made
   *
   * @param board the board passed in
   * @param to the destination of move
   * @param from the start of the move
   * @param player the player object
   * @param moveGenerator the move generator
   */
  public void addBoardWithNextMove(final Board board, final int to, final int from, final AiPlayer player, final MoveGenerator moveGenerator) {

    if (player.movesLeft.moves.size() < 1) {
      throw new MalformedParametersException("Player has no moves left");

    } else {
      // move the piece on this new board
      player.movePiece(from, to, board);

      if (player.movesLeft.size() > 1) {
        player.movesLeft.moves.remove(Integer.valueOf(player.distanceBetween(from, to)));
      } else {
        player.movesLeft.moves.remove(0);
        player.movesLeft.moves.add(0);
      }

      // check if any further moves can be made
      if (!(player.movesLeft.size() == 1 && player.movesLeft.getNext() == 0 || player.movesLeft.size() == 0)) {
        moveGenerator.generateMoves(board, player);
      }

      // add the board to the list if there is not a duplicate
      if (!thereIsADuplicate(board)) {
        addBoardWithNextMove(board);
      }

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

  /**
   * Check that the board evaluators player has a move that fits
   *
   * @param move the move
   * @return the result
   */
  private boolean hasDiceRoll(final int move) {
    return boardEvaluator.currentPlayer.movesLeft.contains(move);
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
   * @param player the player
   * @return the board chosen
   */
  private Board individualDecision(final Board currentBoard, final AiPlayer player) {

    // if returned null it doesn't change
    Board chosenBoard = null;

    // Giving the board evaluator the info it needs
    boardEvaluator.setBoard(currentBoard);
    boardEvaluator.setPlayer(player);

    logger.trace("board list size: " + boardList.size());

    // END GAME
    if (currentBoard.canPlayerBear(player.black)) {

      // Attribute 0 = bear a piece
      if (player.getIndividual().getAttribute(0).getValue() > (int) (Math.random() * 100)) {
        // try to bear
        for (final Board board : boardList) {
          if (boardEvaluator.hasAPieceBeenBore(board)) {
            chosenBoard = board;
            break;
          }
        }
        // Attribute 5 = spread pieces
      } else if (player.getIndividual().getAttribute(5).getValue() > (int) (Math.random() * 100)) {
        // try to spread a piece
        for (final Board board : boardList) {
          if (boardEvaluator.hasAPieceBeenSpread(board)) {
            chosenBoard = board;
            break;
          }
        }
      }
      // MID/EARLY GAME
    } else if (chosenBoard == null && !currentBoard.isInitialMove) {

      // Attribute 1 = take a piece
      if (player.getIndividual().getAttribute(1).getValue() > (int) (Math.random() * 100)) {
        // try to take a piece
        for (final Board board : boardList) {
          if (boardEvaluator.hasAPieceBeenTaken(board)) {
            chosenBoard = board;
            break;
          }
        }

        // Attribute 2 = doubleUpAPiece
      } else if (player.getIndividual().getAttribute(2).getValue() > (int) (Math.random() * 100)) {
        // try to double up a piece
        for (final Board board : boardList) {
          if (boardEvaluator.hasABlotBeenDoubled(board)) {
            chosenBoard = board;
            break;
          }
        }

        // Attribute 3 = blockAnOpponent
      } else if (player.getIndividual().getAttribute(3).getValue() > (int) (Math.random() * 100)) {
        // try to blockAnOpponent
        for (final Board board : boardList) {
          if (boardEvaluator.hasTheOpponentBeenBlocked(board)) {
            chosenBoard = board;
            break;
          }
        }
        // Attribute 4 = movingAPieceSolo
      } else if (player.getIndividual().getAttribute(4).getValue() > (int) (Math.random() * 100)) {
        // try to move a piece solo
        for (final Board board : boardList) {
          if (boardEvaluator.hasAPieceBeenMovedSolo(board)) {
            chosenBoard = board;
            break;
          }
        }

        // Attribute 6 = addACheckerToAStack
      } else if (player.getIndividual().getAttribute(6).getValue() > (int) (Math.random() * 100)) {
        // try to add a checker to a stack
        for (final Board board : boardList) {
          if (boardEvaluator.hasAStackBeenAddedTo(board)) {
            chosenBoard = board;
            break;
          }
        }
      }
      // FIRST MOVE OF THE GAME
    } else if (chosenBoard == null && currentBoard.isInitialMove) {

      // has a roll of TWO and ONE
      if (hasDiceRoll(2) && hasDiceRoll(1)) {

        // ATTRIBUTE 7 = twoOneSplitPlayInitialMove
        if (player.getIndividual().getAttribute(7).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isTwoOneSplitPlayInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
          // ATTRIBUTE 8 = twoOneSlotPlayInitialMove
        } else if (player.getIndividual().getAttribute(8).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isTwoOneSlotPlayInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of THREE and ONE
      } else if (hasDiceRoll(3) && hasDiceRoll(1)) {

        // ATTRIBUTE 9 = threeOneInitialMove
        if (player.getIndividual().getAttribute(9).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isThreeOneInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
        }

        // A Roll of THREE and a TWO
      } else if (hasDiceRoll(3) && hasDiceRoll(1)) {

        // ATTRIBUTE 10 = threeTwoSplitInitialMove
        if (player.getIndividual().getAttribute(10).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isThreeTwoSplitInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
          // ATTRIBUTE 11 = threeTwoOffenceInitialMove
        } else if (player.getIndividual().getAttribute(11).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isThreeTwoOffenceInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
        }

        // A Roll of FOUR and a ONE
      } else if (hasDiceRoll(4) && hasDiceRoll(1)) {

        // ATTRIBUTE 12 = fourOneInitialMove
        if (player.getIndividual().getAttribute(12).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFourOneInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }

          // ATTRIBUTE 13 = fourOneInitialMove
        } else if (player.getIndividual().getAttribute(13).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFourOneInitialMoveAlt(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of FOUR and TWO
      } else if (hasDiceRoll(4) && hasDiceRoll(2)) {

        // ATTRIBUTE 14 = fourTwoInitialMove
        if (player.getIndividual().getAttribute(14).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFourTwoInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of FOUR and THREE
      } else if (hasDiceRoll(4) && hasDiceRoll(3)) {

        // ATTRIBUTE 15 = fourThreeInitialMoveSplit
        if (player.getIndividual().getAttribute(15).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFourThreeInitialMoveSplit(board)) {
              chosenBoard = board;
              break;
            }
          }
          // ATTRIBUTE 16 = fourThreeInitialMoveBlock
        } else if (player.getIndividual().getAttribute(16).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFourThreeInitialMoveBlock(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of FIVE and ONE
      } else if (hasDiceRoll(5) && hasDiceRoll(1)) {

        // ATTRIBUTE 17 = fiveOneInitialMove
        if (player.getIndividual().getAttribute(17).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFiveOneInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
          // ATTRIBUTE 18 = fiveOneInitialMoveAlt
        } else if (player.getIndividual().getAttribute(18).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFiveOneInitialMoveAlt(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of FIVE and TWO
      } else if (hasDiceRoll(5) && hasDiceRoll(2)) {

        // ATTRIBUTE 19 = fiveTwoInitialMove
        if (player.getIndividual().getAttribute(19).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFiveTwoInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
          // ATTRIBUTE 20 = fiveTwoInitialMoveRisk
        } else if (player.getIndividual().getAttribute(20).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFiveTwoInitialMoveRisk(board)) {
              chosenBoard = board;
              break;
            }
          }
        }

        // A Roll of FIVE and THREE
      } else if (hasDiceRoll(5) && hasDiceRoll(3)) {

        // ATTRIBUTE 21 = fiveTwoInitialMove
        if (player.getIndividual().getAttribute(21).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFiveThreeInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of FIVE and THREE
      } else if (hasDiceRoll(5) && hasDiceRoll(4)) {

        // ATTRIBUTE 21 = fiveTwoInitialMove
        if (player.getIndividual().getAttribute(21).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFiveThreeInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of FIVE and FOUR
      } else if (hasDiceRoll(5) && hasDiceRoll(4)) {

        // ATTRIBUTE 22 = fiveFourInitialMoveAgr
        if (player.getIndividual().getAttribute(22).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFiveFourInitialMoveAgr(board)) {
              chosenBoard = board;
              break;
            }
          }
          // ATTRIBUTE 23 = fiveFourInitialMoveBal
        } else if (player.getIndividual().getAttribute(23).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isFiveFourInitialMoveBal(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of SIX and ONE
      } else if (hasDiceRoll(6) && hasDiceRoll(1)) {

        // ATTRIBUTE 24 = sixOneInitialMove
        if (player.getIndividual().getAttribute(24).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isSixOneInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of SIX and TWO
      } else if (hasDiceRoll(6) && hasDiceRoll(2)) {

        // ATTRIBUTE 25 = sixTwoInitialMove
        if (player.getIndividual().getAttribute(25).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isSixTwoInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
          // ATTRIBUTE 26 = sixTwoInitialMoveAgr
        } else if (player.getIndividual().getAttribute(26).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isSixTwoInitialMoveAgr(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of SIX and THREE
      } else if (hasDiceRoll(6) && hasDiceRoll(3)) {

        // ATTRIBUTE 27 = sixThreeInitialMove
        if (player.getIndividual().getAttribute(27).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isSixThreeInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
          // ATTRIBUTE 28 = sixThreeInitialMoveSplit
        } else if (player.getIndividual().getAttribute(28).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isSixThreeInitialMoveSplit(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
        // A Roll of SIX and FOUR
      } else if (hasDiceRoll(6) && hasDiceRoll(4)) {

        // ATTRIBUTE 29 = sixFourInitialMove
        if (player.getIndividual().getAttribute(29).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isSixFourInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
          // ATTRIBUTE 30 = sixFourInitialMoveSplit
        } else if (player.getIndividual().getAttribute(30).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isSixFourInitialMoveSplit(board)) {
              chosenBoard = board;
              break;
            }
          }
        }

        // A Roll of SIX and FIVE
      } else if (hasDiceRoll(6) && hasDiceRoll(5)) {

        // ATTRIBUTE 31 = sixFiveInitialMove
        if (player.getIndividual().getAttribute(31).getValue() > (int) (Math.random() * 100)) {
          for (final Board board : boardList) {
            if (boardEvaluator.isSixFiveInitialMove(board)) {
              chosenBoard = board;
              break;
            }
          }
        }
      }
    }

    // if no specific move has been found - pick a random available one
    if (chosenBoard == null) {
      final int randomNum = (int) (Math.random() * boardList.size());
      chosenBoard = boardList.get(randomNum);
      logger.trace("Random move chosen");
    }
    return chosenBoard;
  }

  /**
   * Select board. This will select the best board from the list for the
   * player to use
   *
   * @param currentBoard the current board
   * @param player the player
   * @return the board that has been selected
   */
  Board selectBoard(final Board currentBoard, final AiPlayer player) {

    if (boardList.size() > 0) {

      // Use the individual to decide what to do next
      if (individual != null) {
        // use the method individual decision to decide which to pick
        return individualDecision(currentBoard, player);

      } else {
        // if the player personality = null then just pick at random, as
        // this means its the basic opposition to test against
        final int randomNumber = (int) (Math.random() * boardList.size());
        logger.trace("board list size: {} ", boardList.size());

        return boardList.get(randomNumber);

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
   * @param boardInQuestion the board it is checking against
   * @return true, if there is a duplicate
   */
  private boolean thereIsADuplicate(final Board boardInQuestion) {
    for (final Board board : boardList) {
      if (board.equals(boardInQuestion)) {
        return true;
      }
    }
    return false;
  }
}