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


	private int redBore, blackBore;

	public final Point[] Points;

	public Board() {
		Points = new Point[26];

		for (int x = 0; x < Points.length; x++) {
			Points[x] = new Point();
		}

		setStartPosition();

		redBore = 0;
		blackBore = 0;
	}

	public Board(final Board copy) {
		// 26 new points
		Points = new Point[26];

		// copying each one over in a loop
		for (int i = 0; i < copy.Points.length; i++) {
			// copy
			final Point x = new Point(copy.Points[i]);
			// add
			Points[i] = x;
		}

		// copying bored
		redBore = copy.redBore;
		blackBore = copy.blackBore;
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
		Points[bearPiece].removePiece(black);
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
				if (Points[x].getBlackCount() > 0) {
					return false;
				}
			}

		} else {
			// looping all areas apart from the players own quarter (19 to 24), including the bar on 0
			for (int x = 0; x <= 18; x++) {
				// if any of them contain a red piece
				if (Points[x].getRedCount() > 0) {
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
		return redBore == b.redBore && blackBore == b.blackBore && Arrays.equals(Points, b.Points);
	}

	/**
	 * Hash of the piece positions, so boards can go in a HashSet. Don't change a board while it's in one.
	 */
	@Override
	public int hashCode() {
		return 31 * (31 * redBore + blackBore) + Arrays.hashCode(Points);
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
			if ((loserBlack ? Points[point].getBlackCount() : Points[point].getRedCount()) > 0) {
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
			return Points[25].getBlackCount() > 0;
		} else {
			return Points[0].getRedCount() > 0;
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
		out.println(String.format("| Black's bar (25): %-2d  Borne off: %-2d   |", Points[25].numEither(), blackBore));
		out.println(line);
		printRow(out, 1, 12);
		out.println(line);
		printRow(out, 13, 24);
		out.println(line);
		out.println(String.format("| Red's bar (0):    %-2d  Borne off: %-2d   |", Points[0].numEither(), redBore));
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
			red.append(String.format("|%2d", Points[x].getRedCount()));
			black.append(String.format("|%2d", Points[x].getBlackCount()));
		}
		out.println(numbers.append('|'));
		out.println(red.append('|'));
		out.println(black.append('|'));
	}

	void printBoardGUI() {
		if (GameSettings.getDisplayGUI()) {
			GameManager.boardContainerFrame.bp.printCheckers(Points, redBore, blackBore);
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
		for (final Point point : Points) {
			point.setBlackCount(0);
			point.setRedCount(0);
		}
		// completing the starting position of the checkers, the rest left at 0
		// Red Checkers
		Points[1].setRedCount(2);
		Points[12].setRedCount(5);
		Points[17].setRedCount(3);
		Points[19].setRedCount(5);
		// Black Checkers
		Points[6].setBlackCount(5);
		Points[8].setBlackCount(3);
		Points[13].setBlackCount(5);
		Points[24].setBlackCount(2);
	}

}
