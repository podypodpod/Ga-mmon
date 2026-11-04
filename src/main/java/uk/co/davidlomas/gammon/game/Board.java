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

import uk.co.davidlomas.gammon.settings.GameSettings;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * The Class Board.
 * <p>
 * Essentially the board state class
 * <p>
 * Holds all the data on the board, where all the pieces are, if the player is
 * in a position to move etc
 */
public class Board {


	/**
	 * How many points there are, including the bars: red's on 0 and black's on 25
	 */
	public static final int POINTS = 26;

	private int redBore, blackBore;

	/**
	 * How many pieces are on each point, black's then red's for each. Boards are copied, compared and hashed more than
	 * anything else in a game, and in one array that's quick and leaves little to collect. Use index to find a count.
	 */
	private final byte[] counts;

	public Board() {
		counts = new byte[2 * POINTS];

		setStartPosition();

		redBore = 0;
		blackBore = 0;
	}

	public Board(final Board copy) {
		counts = copy.counts.clone();

		// copying bored
		redBore = copy.redBore;
		blackBore = copy.blackBore;
	}

	private static int index(final int point, final boolean black) {
		return 2 * point + (black ? 0 : 1);
	}

	/**
	 * How many of a player's pieces are on a point
	 *
	 * @param point the point
	 * @param black the player color
	 *
	 * @return how many
	 */
	public int getCount(final int point, final boolean black) {
		return counts[index(point, black)];
	}

	public int getBlackCount(final int point) {
		return getCount(point, true);
	}

	public int getRedCount(final int point) {
		return getCount(point, false);
	}

	public void setBlackCount(final int point, final int count) {
		counts[index(point, true)] = (byte) count;
	}

	public void setRedCount(final int point, final int count) {
		counts[index(point, false)] = (byte) count;
	}

	public void addPiece(final int point, final boolean black) {
		counts[index(point, black)]++;
	}

	public void removePiece(final int point, final boolean black) {
		counts[index(point, black)]--;
	}

	/**
	 * Which player a point belongs to
	 *
	 * @param point the point
	 *
	 * @return true for black, which needs more black pieces on it than red, so an empty point counts as red
	 */
	public boolean getCol(final int point) {
		return getBlackCount(point) > getRedCount(point);
	}

	/**
	 * How many pieces the player with the most on a point has there
	 *
	 * @param point the point
	 *
	 * @return how many
	 */
	public int numEither(final int point) {
		return Math.max(getBlackCount(point), getRedCount(point));
	}

	/**
	 * Takes every piece off the points. Pieces already borne off stay borne off.
	 */
	public void removeAllPieces() {
		Arrays.fill(counts, (byte) 0);
	}

	/**
	 * Adds the to bear.
	 *
	 * @param black the player color
	 */
	void addToBear(final boolean black) {
		if (black) {
			blackBore++;
		} else {
			redBore++;
		}
	}

	/**
	 * Bear piece.
	 *
	 * @param bearPiece the position to bear
	 * @param black     the player color
	 */
	void bearPiece(final int bearPiece, final boolean black) {
		removePiece(bearPiece, black);
		addToBear(black);
	}

	/**
	 * Can player bear.
	 * <p>
	 * checks the board positions etc to check if the player can legally bear
	 *
	 * @param black the player color
	 *
	 * @return true, if successful
	 */
	boolean canPlayerBear(final boolean black) {
		if (black) {
			// looping all areas apart from the players own quarter, including the bar on 25
			for (int x = 7; x <= 25; x++) {
				// if any of them contain a black piece
				if (getBlackCount(x) > 0) {
					return false;
				}
			}

		} else {
			// looping all areas apart from the players own quarter (19 to 24), including the bar on 0
			for (int x = 0; x <= 18; x++) {
				// if any of them contain a red piece
				if (getRedCount(x) > 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Equals.
	 * <p>
	 * Board comparison, if they are the same (same piece positions)
	 *
	 * @param o the board
	 *
	 * @return true, if they are the same
	 */
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Board)) {
			return false;
		}
		final Board b = (Board) o;
		return redBore == b.redBore && blackBore == b.blackBore && Arrays.equals(counts, b.counts);
	}

	/**
	 * Hash of the piece positions, so boards can go in a HashSet. Don't change a board while it's in one.
	 */
	@Override
	public int hashCode() {
		return 31 * (31 * redBore + blackBore) + Arrays.hashCode(counts);
	}

	public int getBlackBore() {
		return blackBore;
	}

	public int getRedBore() {
		return redBore;
	}

	/**
	 * Checks for a player winning.
	 *
	 * @param black the player color
	 *
	 * @return true, if they have won
	 */
	boolean hasPlayerWon(final Boolean black) {
		return (black && (blackBore == 15)) || (!black && (redBore == 15));
	}

	/**
	 * How the game was won against this player, from where their pieces are when it ends: a gammon if they haven't
	 * borne off any, a backgammon if they also still have one on the bar or in the winner's home quarter, otherwise a
	 * single game.
	 *
	 * @param loserBlack the player who lost
	 *
	 * @return the kind of win
	 */
	WinType winTypeAgainst(final boolean loserBlack) {
		if (howManyHasPlayerBore(loserBlack) > 0) {
			return WinType.SINGLE;
		}
		// the loser's bar, and the winner's home quarter: red's is 19 to 24, black's is 1 to 6
		final int bar = loserBlack ? 25 : 0;
		final int winnersHomeStart = loserBlack ? 19 : 1;
		for (final int point : new int[]{bar, winnersHomeStart, winnersHomeStart + 1, winnersHomeStart + 2,
				winnersHomeStart + 3, winnersHomeStart + 4, winnersHomeStart + 5}) {
			if (getCount(point, loserBlack) > 0) {
				return WinType.BACKGAMMON;
			}
		}
		return WinType.GAMMON;
	}

	/**
	 * How many has player bore.
	 *
	 * @param black the blackBore
	 *
	 * @return the num of bore
	 */
	int howManyHasPlayerBore(final boolean black) {
		if (black) {
			return blackBore;
		} else {
			return redBore;
		}
	}

	public boolean isThereZero(final boolean black) {
		if (black) {
			return getBlackCount(25) > 0;
		} else {
			return getRedCount(0) > 0;
		}
	}

	public void printBoard() {
		printBoard(System.out);
	}

	/**
	 * Prints the board as text, with the bars (black 25, red 0) and how many each player has borne off
	 *
	 * @param out where to print it
	 */
	public void printBoard(final PrintStream out) {
		final String line = "|---------------------------------------|";
		out.println(line);
		out.println(String.format("| Black's bar (25): %-2d  Borne off: %-2d   |", numEither(25), blackBore));
		out.println(line);
		printRow(out, 1, 12);
		out.println(line);
		printRow(out, 13, 24);
		out.println(line);
		out.println(String.format("| Red's bar (0):    %-2d  Borne off: %-2d   |", numEither(0), redBore));
		out.println(line);
	}

	/**
	 * Prints the numbers of the points from first to last, then how many red and black pieces are on each
	 */
	private void printRow(final PrintStream out, final int first, final int last) {
		final StringBuilder numbers = new StringBuilder("|NUM");
		final StringBuilder red = new StringBuilder("|RED");
		final StringBuilder black = new StringBuilder("|BLK");
		for (int x = first; x <= last; x++) {
			numbers.append(String.format("|%2d", x));
			red.append(String.format("|%2d", getRedCount(x)));
			black.append(String.format("|%2d", getBlackCount(x)));
		}
		out.println(numbers.append('|'));
		out.println(red.append('|'));
		out.println(black.append('|'));
	}

	void printBoardGUI() {
		if (GameSettings.getDisplayGUI()) {
			GameManager.boardContainerFrame.bp.printCheckers(this);
		}
	}

	@SuppressWarnings("SameParameterValue")
	public void setBlackBore(final int blackBore) {
		this.blackBore = blackBore;
	}

	public void setRedBore(final int redBore) {
		this.redBore = redBore;
	}

	public void setStartPosition() {
		// Resetting all to 0
		removeAllPieces();
		// completing the starting position of the checkers, the rest left at 0
		// Red Checkers
		setRedCount(1, 2);
		setRedCount(12, 5);
		setRedCount(17, 3);
		setRedCount(19, 5);
		// Black Checkers
		setBlackCount(6, 5);
		setBlackCount(8, 3);
		setBlackCount(13, 5);
		setBlackCount(24, 2);
	}

}
