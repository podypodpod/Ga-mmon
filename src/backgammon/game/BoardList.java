/**
 * 	GNU General Public License
 * 
 *  This file is part of GA-mmon.
 *  
 *  GA-mmon is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  GA-mmon is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with GA-mmon.  If not, see <http://www.gnu.org/licenses/>.
*/

package backgammon.game;

import java.util.ArrayList;
import java.util.List;

import backgammon.genes.Individual;
import backgammon.settings.GameSettings;

/**
 * The Class BoardList.
 * 
 * Holds a collection of board states allowing the AI to then loop through them
 * 
 * @author David Lomas - 11035527
 */
public class BoardList {

	/** The all possible moves. */
	private List<Board> boardList;

	/** The individual that will make the move. */
	private Individual individual;

	/** The beval. */
	private BoardEvaluator bEval;

	/**
	 * BoardList
	 * 
	 * Instantiates a new board list.
	 *
	 * @param indiv
	 *            the indiv
	 */
	public BoardList(Individual indiv) {

		boardList = new ArrayList<Board>();

		individual = indiv;

		bEval = new BoardEvaluator();
	}

	/**
	 * clearList
	 * 
	 * Emptys the list.
	 */
	public void clearList() {
		boardList.clear();
	}

	/**
	 * addBoard Adds the board passed in.
	 *
	 * @param b
	 *            the board
	 */
	public void addBoard(Board b) {
		boardList.add(b);
	}

	/**
	 * addBoard adds the board passed in with the move also passed in applied to
	 * it first. It then checks if any more moves can be made
	 *
	 * @param b
	 *            the board passed in
	 * @param to
	 *            the destination of move
	 * @param from
	 *            the start of the move
	 * @param p1
	 *            the player object
	 * @param mg
	 *            the move generator
	 */
	public void addBoard(Board b, int to, int from, AIPlayer p1, MoveGenerator mg) {

		// move the piece on this new board
		p1.movePiece(from, to, b);

		if (p1.movesLeft.size() > 1) {
			p1.movesLeft.movesLeft.remove(Integer.valueOf(p1.distanceBetween(from, to)));
		} else {
			p1.movesLeft.movesLeft.remove(0);
			p1.movesLeft.movesLeft.add(Integer.valueOf(0));
		}

		// check if any further moves can be made
		if (!((p1.movesLeft.size() == 1 && p1.movesLeft.getNext() == 0) || (p1.movesLeft.size() == 0))) {
			mg.generateMoves(b, p1);
		}

		// add the board to the list if there is not a duplicate
		if (!(thereIsADuplicate(b))) {
			addBoard(b);
		}
	}

	/**
	 * There is a duplicate. checks if there is already a duplicate of the board
	 * passed in already present in the list
	 *
	 * @param b
	 *            the board it is checking against
	 * @return true, if there is a duplicate
	 */
	public boolean thereIsADuplicate(Board b) {
		for (Board x : boardList) {
			if (x.equals(b)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Select board. This will select the best board from the list for the
	 * player to use
	 *
	 * @param currentBoard
	 *            the current board
	 * @param p
	 *            the p
	 * @return the board that has been selected
	 */
	public Board selectBoard(Board currentBoard, AIPlayer p) {

		// create a new board
		Board chosenBoard = null;

		if (boardList.size() > 0) {

			// Use the individual to decide what to do next
			if (individual != null) {

				// use the method individual decision to decide which to pick
				chosenBoard = individualDecision(currentBoard, p);
				return chosenBoard;

				// if the player personality = null then just pick at random, as
				// this means its the basic opposition to test against
			} else {
				int x = (int) (Math.random() * (boardList.size()));

				if (GameSettings.getDisplayConsole()) {
					System.out.println("board list size: " + boardList.size());
				}

				chosenBoard = boardList.get(x);

				return chosenBoard;
			}
		} else {
			// if there are no possible new boards(no possible moves) return
			// null
			if (GameSettings.getDisplayConsole()) {
				System.out.println("No possible moves");
			}
			return null;
		}

	}

	/**
	 * Prints the board list.
	 */
	public void printBoardList() {
		int counter = 0;
		for (Board x : boardList) {
			counter++;
			System.out.println("----------------------------");
			System.out.println("Board num " + counter);
			x.printBoard();
			System.out.println("----------------------------");
		}
	}

	/**
	 * Checks for contents.
	 *
	 * @return true, if successful
	 */
	public boolean hasContents() {
		if (boardList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Individual decision.
	 * 
	 * Is called by select board and it is where the individuals attributes
	 * influence the chosen board
	 * 
	 * e.g. higher aggression chance will produce more pieces being taken
	 *
	 * @param currentBoard
	 *            the current board
	 * @param p
	 *            the player
	 * @return the board chosen
	 */
	public Board individualDecision(Board currentBoard, AIPlayer p) {

		// if returned null it doesn't change
		Board chosenBoard = null;

		// Giving the board evaluator the info it needs
		bEval.setBoard(currentBoard);
		bEval.setPlayer(p);

		if (GameSettings.getDisplayConsole()) {
			System.out.println("board list size: " + boardList.size());
		}

		// END GAME
		if (currentBoard.canPlayerBear(p.black)) {

			// Attribute 0 = bear a piece
			if (p.getIndividual().getAttribute(0).getValue() > (int) (Math.random() * 100)) {
				// try to bear
				for (Board x : boardList) {
					if (bEval.hasAPeiceBeenBore(x)) {
						chosenBoard = x;
						break;
					}
				}
				// Attribute 5 = spread pieces
			} else if (p.getIndividual().getAttribute(5).getValue() > (int) (Math.random() * 100)) {
				// try to spread a piece
				for (Board x : boardList) {
					if (bEval.hasAPieceBeenSpread(x)) {
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
				for (Board x : boardList) {
					if (bEval.hasAPeiceBeenTaken(x)) {
						chosenBoard = x;
						break;
					}
				}

				// Attribute 2 = doubleUpAPiece
			} else if (p.getIndividual().getAttribute(2).getValue() > (int) (Math.random() * 100)) {
				// try to double up a piece
				for (Board x : boardList) {
					if (bEval.hasABlotBeenDoubled(x)) {
						chosenBoard = x;
						break;
					}
				}

				// Attribute 3 = blockAnOpponent
			} else if (p.getIndividual().getAttribute(3).getValue() > (int) (Math.random() * 100)) {
				// try to blockAnOpponent
				for (Board x : boardList) {
					if (bEval.hasTheOponentBeenBlocked(x)) {
						chosenBoard = x;
						break;
					}
				}
				// Attribute 4 = movingAPieceSolo
			} else if (p.getIndividual().getAttribute(4).getValue() > (int) (Math.random() * 100)) {
				// try to move a piece solo
				for (Board x : boardList) {
					if (bEval.hasAPieceBeenMovedSolo(x)) {
						chosenBoard = x;
						break;
					}
				}

				// Attribute 6 = addACheckerToAStack
			} else if (p.getIndividual().getAttribute(6).getValue() > (int) (Math.random() * 100)) {
				// try to add a checker to a stack
				for (Board x : boardList) {
					if (bEval.hasAStackBeenAddedTo(x)) {
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
					for (Board x : boardList) {
						if (bEval.isTwoOneSplitPlayInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
					// ATTRIBUTE 8 = twoOneSlotPlayInitialMove
				} else if (p.getIndividual().getAttribute(8).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isTwoOneSlotPlayInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of THREE and ONE
			} else if (hasDiceRoll(3) && hasDiceRoll(1)) {

				// ATTRIBUTE 9 = threeOneInitialMove
				if (p.getIndividual().getAttribute(9).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isThreeOneInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
				}

				// A Roll of THREE and a TWO
			} else if (hasDiceRoll(3) && hasDiceRoll(1)) {

				// ATTRIBUTE 10 = threeTwoSplitInitialMove
				if (p.getIndividual().getAttribute(10).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isThreeTwoSplitInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
					// ATTRIBUTE 11 = threeTwoOffenceInitialMove
				} else if (p.getIndividual().getAttribute(11).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isThreeTwoOffenceInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
				}

				// A Roll of FOUR and a ONE
			} else if (hasDiceRoll(4) && hasDiceRoll(1)) {

				// ATTRIBUTE 12 = fourOneInitialMove
				if (p.getIndividual().getAttribute(12).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFourOneInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}

					// ATTRIBUTE 13 = fourOneInitialMove
				} else if (p.getIndividual().getAttribute(13).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFourOneInitialMoveAlt(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of FOUR and TWO
			} else if (hasDiceRoll(4) && hasDiceRoll(2)) {

				// ATTRIBUTE 14 = fourTwoInitialMove
				if (p.getIndividual().getAttribute(14).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFourTwoInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of FOUR and THREE
			} else if (hasDiceRoll(4) && hasDiceRoll(3)) {

				// ATTRIBUTE 15 = fourThreeInitialMoveSplit
				if (p.getIndividual().getAttribute(15).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFourThreeInitialMoveSplit(x)) {
							chosenBoard = x;
							break;
						}
					}
					// ATTRIBUTE 16 = fourThreeInitialMoveBlock
				} else if (p.getIndividual().getAttribute(16).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFourThreeInitialMoveBlock(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of FIVE and ONE
			} else if (hasDiceRoll(5) && hasDiceRoll(1)) {

				// ATTRIBUTE 17 = fiveOneInitialMove
				if (p.getIndividual().getAttribute(17).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFiveOneInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
					// ATTRIBUTE 18 = fiveOneInitialMoveAlt
				} else if (p.getIndividual().getAttribute(18).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFiveOneInitialMoveAlt(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of FIVE and TWO
			} else if (hasDiceRoll(5) && hasDiceRoll(2)) {

				// ATTRIBUTE 19 = fiveTwoInitialMove
				if (p.getIndividual().getAttribute(19).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFiveTwoInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
					// ATTRIBUTE 20 = fiveTwoInitialMoveRisk
				} else if (p.getIndividual().getAttribute(20).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFiveTwoInitialMoveRisk(x)) {
							chosenBoard = x;
							break;
						}
					}
				}

				// A Roll of FIVE and THREE
			} else if (hasDiceRoll(5) && hasDiceRoll(3)) {

				// ATTRIBUTE 21 = fiveTwoInitialMove
				if (p.getIndividual().getAttribute(21).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFiveThreeInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of FIVE and THREE
			} else if (hasDiceRoll(5) && hasDiceRoll(4)) {

				// ATTRIBUTE 21 = fiveTwoInitialMove
				if (p.getIndividual().getAttribute(21).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFiveThreeInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of FIVE and FOUR
			} else if (hasDiceRoll(5) && hasDiceRoll(4)) {

				// ATTRIBUTE 22 = fiveFourInitialMoveAgr
				if (p.getIndividual().getAttribute(22).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFiveFourInitialMoveAgr(x)) {
							chosenBoard = x;
							break;
						}
					}
					// ATTRIBUTE 23 = fiveFourInitialMoveBal
				} else if (p.getIndividual().getAttribute(23).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isFiveFourInitialMoveBal(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of SIX and ONE
			} else if (hasDiceRoll(6) && hasDiceRoll(1)) {

				// ATTRIBUTE 24 = sixOneInitialMove
				if (p.getIndividual().getAttribute(24).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isSixOneInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of SIX and TWO
			} else if (hasDiceRoll(6) && hasDiceRoll(2)) {

				// ATTRIBUTE 25 = sixTwoInitialMove
				if (p.getIndividual().getAttribute(25).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isSixTwoInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
					// ATTRIBUTE 26 = sixTwoInitialMoveAgr
				} else if (p.getIndividual().getAttribute(26).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isSixTwoInitialMoveAgr(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of SIX and THREE
			} else if (hasDiceRoll(6) && hasDiceRoll(3)) {

				// ATTRIBUTE 27 = sixThreeInitialMove
				if (p.getIndividual().getAttribute(27).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isSixThreeInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
					// ATTRIBUTE 28 = sixThreeInitialMoveSplit
				} else if (p.getIndividual().getAttribute(28).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isSixThreeInitialMoveSplit(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
				// A Roll of SIX and FOUR
			} else if (hasDiceRoll(6) && hasDiceRoll(4)) {

				// ATTRIBUTE 29 = sixFourInitialMove
				if (p.getIndividual().getAttribute(29).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isSixFourInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
					// ATTRIBUTE 30 = sixFourInitialMoveSplit
				} else if (p.getIndividual().getAttribute(30).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isSixFourInitialMoveSplit(x)) {
							chosenBoard = x;
							break;
						}
					}
				}

				// A Roll of SIX and FIVE
			} else if (hasDiceRoll(6) && hasDiceRoll(5)) {

				// ATTRIBUTE 31 = sixFiveInitialMove
				if (p.getIndividual().getAttribute(31).getValue() > (int) (Math.random() * 100)) {
					for (Board x : boardList) {
						if (bEval.isSixFiveInitialMove(x)) {
							chosenBoard = x;
							break;
						}
					}
				}
			}
		}

		// if no specific move has been found - pick a random available one
		if (chosenBoard == null) {
			int x = (int) (Math.random() * (boardList.size()));
			chosenBoard = boardList.get(x);
			if (GameSettings.getDisplayConsole()) {
				System.out.println("Random move chosen");
			}
		}
		return chosenBoard;
	}

	public boolean hasDiceRoll(int x) {
		if (bEval.currentPlayer.movesLeft.contains(x)) {
			return true;
		}
		return false;
	}
}