package uk.co.davidlomas.gammon.game;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
	private static Scanner console;
	private Scanner input;
	private PrintStream output = System.out;
	private boolean conceded;

	/**
	 * Instantiates a new player.
	 */
	public Player(final boolean black) {
		this(black, new DiceList());
	}

	/**
	 * A player using these dice, for copies of a player made while trying out moves, which don't need their own
	 */
	Player(final boolean black, final DiceList dice) {
		this(black, dice, new MovesList());
	}

	/**
	 * A player using these dice with these moves left
	 */
	Player(final boolean black, final DiceList dice, final MovesList movesLeft) {
		this.black = black;
		this.movesLeft = movesLeft;
		this.dice = dice;
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
				if (liveBoard.getBlackCount(point) > 0) {
					return false;
				}
			}
		} else {
			for (int point = 19; point < from; point++) {
				if (liveBoard.getRedCount(point) > 0) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidLength(final int from, final int to) {
		return movesLeft.contains(distanceBetween(from, to));
	}

	/**
	 * returns the distance between the two points
	 */
	public int distanceBetween(final int a, final int b) {
		return Math.abs(a - b);
	}

	/**
	 * returns true if start position has a piece on it
	 */
	private boolean hasAPieceAtStart(final int from, final Board liveBoard) {
		return liveBoard.numEither(from) > 0 && liveBoard.getCol(from) == black;
	}

	/**
	 * Move piece.
	 */
	public void movePiece(final int from, final int to, final Board liveBoard) {

		// if not bearing positions
		if (!isBearingOff(to)) {

			// if there is an opposing piece there
			if (liveBoard.getCol(to) != black && liveBoard.numEither(to) == 1) {

				liveBoard.removePiece(from, black);
				liveBoard.removePiece(to, !black);
				liveBoard.addPiece(to, black);

				if (black) {
					liveBoard.addPiece(0, false);
				} else {
					liveBoard.addPiece(25, true);
				}

			} else {// if its empty
				liveBoard.removePiece(from, black);
				liveBoard.addPiece(to, black);

			}
		} else {// ELSE WE ARE BEARING THE PIECE OFF
			liveBoard.removePiece(from, black);
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
	 * A person's turn, played on the console: rolls the dice, then asks what to do with them
	 */
	void turn(final Board liveBoard) {
		turn(liveBoard, dice.RollTwoDice());
	}

	/**
	 * A person's turn with dice already rolled, such as the opening move, which uses the dice from the opening roll
	 */
	void turn(final Board liveBoard, final List<Integer> roll) {
		movesLeft.setTo(roll);
		playRoll(liveBoard);
	}

	/**
	 * Plays the dice already in movesLeft, asking the person which piece to move with which die until none of them
	 * can be used, or they concede. Bearing off is a move like any other: a piece moved past the end of the board
	 * comes off, if the rules allow it.
	 */
	void playRoll(final Board liveBoard) {
		output.println();
		output.println("Your turn, you're player 2, " + getColourString() + ". " + (black
				? "Black moves from 24 down to 1, its bar is 25, and pieces come off past 1."
				: "Red moves from 1 up to 24, its bar is 0, and pieces come off past 24."));
		liveBoard.printBoard(output);
		// the boards the rules allow the person to finish this roll on
		final List<Integer> roll = new ArrayList<>(movesLeft.moves);
		final BoardList legalMoves = MoveGenerator.legalMoves(liveBoard, black, roll);

		while (true) {
			if (!hasAMove(liveBoard)) {
				output.println(movesLeft.size() == 0 ? "That's all your dice." : "None of your dice can be played.");
				return;
			}
			output.println("Dice left: " + movesLeft.moves);
			final Integer option = readNumber("1) Move a piece  2) Concede the game: ");
			if (option == null) {
				concede("The input has ended, so you've conceded.");
				return;
			}

			if (option == 1) {
				final Integer from = readNumber("Move the piece on which point? ");
				final Integer die = from == null ? null : readNumber("Using which die? ");
				if (die == null) {
					concede("The input has ended, so you've conceded.");
					return;
				}
				final int to = black ? from - die : from + die;
				// isMovePossible also checks they have that die, as the move is exactly that far
				if (!isMovePossible(from, to, liveBoard)) {
					output.println("That move isn't allowed.");
				} else {
					final String reason = whyTheRulesRefuse(from, die, liveBoard, roll, legalMoves);
					if (reason != null) {
						output.println(reason);
					} else {
						movePiece(from, to, liveBoard);
						movesLeft.remove(die);
						liveBoard.printBoard(output);
					}
				}
			} else if (option == 2) {
				concede("You conceded.");
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
	 * Checks a move against the rules for using the dice: a player has to use as many of them as they can, and when
	 * only one of two different dice can be used, it has to be the larger if that can be played. A move can be allowed
	 * on its own and still break these, by leaving a die that could have been used with nothing to move.
	 * <p>
	 * The rules themselves are applied by MoveGenerator, the same as for the AI. A move is fine if the person can
	 * still finish the turn on one of the boards it allows.
	 *
	 * @param roll       the dice rolled this turn
	 * @param legalMoves the boards the rules allow the turn to finish on, from the start of the roll
	 *
	 * @return why the move breaks them, to tell the person, or null if it doesn't
	 */
	private String whyTheRulesRefuse(final int from, final int die, final Board liveBoard, final List<Integer> roll,
									 final BoardList legalMoves) {
		final Board after = new Board(liveBoard);
		movePiece(from, black ? from - die : from + die, after);
		final List<Integer> rest = new ArrayList<>(movesLeft.moves);
		rest.remove(Integer.valueOf(die));

		final Set<Board> allowed = legalMoves.boardSet();
		if (allowed.contains(after) || !Collections.disjoint(MoveGenerator.reachableBoards(after, black, rest), allowed)) {
			return null;
		}
		final int most = legalMoves.mostDiceUsed();
		if (most == 1 && roll.size() == 2 && !roll.get(0).equals(roll.get(1))) {
			return "Only one of your dice can be used, so it has to be the larger one, the "
					+ Math.max(roll.get(0), roll.get(1)) + ".";
		}
		return "You can use " + most + " of your dice if you move another way, and you have to use as many as you can.";
	}

	private void concede(final String message) {
		conceded = true;
		output.println(message);
	}

	/**
	 * Shows the person a message, such as who won
	 */
	void tell(final String message) {
		output.println(message);
	}

	/**
	 * Shows the person the board
	 */
	void showBoard(final Board board) {
		board.printBoard(output);
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
	 * The one reader for the console, shared by every player. Each game makes a new player 2, and a reader of its
	 * own would lose whatever the last one had already read ahead
	 */
	private static synchronized Scanner console() {
		if (console == null) {
			console = new Scanner(System.in);
		}
		return console;
	}

	/**
	 * Asks until the person types a whole number
	 *
	 * @return the number, or null if the input has ended, such as Ctrl+D or Ctrl+Z on the console
	 */
	private Integer readNumber(final String prompt) {
		if (input == null) {
			input = console();
		}
		while (true) {
			output.print(prompt);
			output.flush();
			if (!input.hasNextLine()) {
				output.println();
				return null;
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
		return liveBoard.getCol(to) == black || liveBoard.numEither(to) <= 1;
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
