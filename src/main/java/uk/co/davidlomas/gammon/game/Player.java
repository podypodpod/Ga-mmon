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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * The Class Player. the base class for players and contains code that only
 * covers human players.
 * <p>
 * two players will always be made, the AI will extend this to use AIPlayers
 * though
 *
 * @author David Lomas
 */
public class Player {

	public MovesList movesLeft;
	protected Boolean black;
	final DiceList dice;

	// where a person's moves are read from and the prompts written to: the console, unless a test swaps them.
	// The input is only opened when a person plays, so AI players never touch System.in
	private Scanner input;
	private PrintStream output = System.out;
	private boolean conceded;

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

		if (!isValidLength(from, to)) {
			return false;
		}

		if (isBearingOff(to)) {
			return canBearOff(from, to, liveBoard);
		}
		return validDestination(to, liveBoard);
	}

	/**
	 * returns true if the move takes the piece off the board: past 1 for black, past 24 for red
	 */
	private boolean isBearingOff(final int to) {
		return black && to <= 0 || !black && to >= 25;
	}

	/**
	 * returns true if the piece can be borne off. Every piece has to be home, and a roll bigger than the
	 * piece needs can only be used on the piece furthest from being borne off
	 */
	private boolean canBearOff(final int from, final int to, final Board liveBoard) {
		if (!liveBoard.canPlayerBear(black)) {
			return false;
		}
		// the exact roll, landing on the edge of the board
		if (to == 0 || to == 25) {
			return true;
		}
		if (black) {
			for (int point = from + 1; point <= 6; point++) {
				if (liveBoard.Points[point].getBlackCount() > 0) {
					return false;
				}
			}
		} else {
			for (int point = 19; point < from; point++) {
				if (liveBoard.Points[point].getRedCount() > 0) {
					return false;
				}
			}
		}
		return true;
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
		if (!isBearingOff(to)) {

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
	/**
	 * A person's turn, played on the console: rolls the dice, then asks what to do with them
	 */
	void turn(final Board liveBoard) {
		movesLeft.setTo(dice.RollTwoDice());
		playRoll(liveBoard);
	}

	/**
	 * Plays the dice already in movesLeft, asking the person which piece to move with which die until none of them
	 * can be used, or they concede. Bearing off is a move like any other: a piece moved past the end of the board
	 * comes off, if the rules allow it.
	 */
	void playRoll(final Board liveBoard) {
		output.println();
		output.println("Your turn, you're " + getColourString() + ". " + (black
				? "Black moves from 24 down to 1, its bar is 25, and pieces come off past 1."
				: "Red moves from 1 up to 24, its bar is 0, and pieces come off past 24."));
		liveBoard.printBoard(output);

		while (true) {
			if (!hasAMove(liveBoard)) {
				output.println(movesLeft.size() == 0 ? "That's all your dice." : "None of your dice can be played.");
				return;
			}
			output.println("Dice left: " + movesLeft.moves);
			final int option = readNumber("1) Move a piece  2) Concede the game: ");

			if (option == 1) {
				final int from = readNumber("Move the piece on which point? ");
				final int die = readNumber("Using which die? ");
				final int to = black ? from - die : from + die;
				if (movesLeft.contains(die) && isMovePossible(from, to, liveBoard)) {
					movePiece(from, to, liveBoard);
					movesLeft.remove(die);
					liveBoard.printBoard(output);
				} else {
					output.println("That move isn't allowed.");
				}
			} else if (option == 2) {
				conceded = true;
				output.println("You conceded.");
				return;
			} else {
				output.println("Type 1 or 2.");
			}
		}
	}

	/**
	 * returns true if any of the dice left can move any of the player's pieces
	 */
	boolean hasAMove(final Board liveBoard) {
		for (final int die : movesLeft.moves) {
			for (int from = 0; from <= 25; from++) {
				if (isMovePossible(from, black ? from - die : from + die, liveBoard)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Shows the person a message, such as who won
	 */
	void tell(final String message) {
		output.println(message);
	}

	/**
	 * Has the person conceded the game
	 */
	boolean hasConceded() {
		return conceded;
	}

	/**
	 * Reads moves from in and writes prompts to out, instead of the console, so tests can play a turn
	 */
	void useConsole(final InputStream in, final PrintStream out) {
		input = new Scanner(in);
		output = out;
	}

	/**
	 * Asks until the person types a whole number
	 */
	private int readNumber(final String prompt) {
		if (input == null) {
			input = new Scanner(System.in);
		}
		while (true) {
			output.print(prompt);
			output.flush();
			if (!input.hasNextLine()) {
				throw new IllegalStateException("The input ended part way through a turn");
			}
			final String line = input.nextLine().trim();
			try {
				return Integer.parseInt(line);
			} catch (final NumberFormatException e) {
				output.println("Type a number.");
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
	 * returns if its within set array bounds. A piece borne off with a 6 can land up to 5 points past the edge
	 */
	private boolean withinArrayBounds(final int to, final int from) {
		return from >= 0 && from <= 25 && to <= 30 && to >= -5;
	}

	/**
	 * Returns the colour of the player in string format
	 *
	 * @return the colour of the player
	 */
	public String getColourString() {
		if (black) {
			return "black";
		}
		return "red";
	}
}
