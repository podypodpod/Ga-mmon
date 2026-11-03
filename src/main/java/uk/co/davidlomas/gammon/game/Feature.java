package uk.co.davidlomas.gammon.game;

/**
 * Things about a board that a player weighs up when choosing a move, measured from that player's side.
 * <p>
 * Each one is worked out in the player's own numbering: their home quarter is 1 to 6, their bar is 25, and they move
 * down towards 0. For black that's the board's own numbering, and for red it's turned round, point p being 25 - p.
 * The opponent moves the other way, up from their bar at 0.
 * <p>
 * Each feature is divided by its scale, so they're all roughly between -1 and 1 and a weight means much the same
 * for each of them. Some only matter while the players can still hit each other, and aren't weighed in a race.
 */
public enum Feature {

	/**
	 * How many pips the opponent needs to bear off, less how many the player needs
	 */
	PIP_LEAD("pipLead", 100, true) {
		@Override
		int measure(final Board board, final boolean black) {
			int mine = 0;
			int theirs = 0;
			for (int point = 0; point <= 25; point++) {
				mine += point * mine(board, black, point);
				theirs += (25 - point) * theirs(board, black, point);
			}
			return theirs - mine;
		}
	},

	/**
	 * The player's points with just one piece on, which the opponent can hit
	 */
	BLOTS("blots", 5, false) {
		@Override
		int measure(final Board board, final boolean black) {
			int blots = 0;
			for (int point = 1; point <= 24; point++) {
				if (mine(board, black, point) == 1) {
					blots++;
				}
			}
			return blots;
		}
	},

	/**
	 * How many of the opponent's 36 possible rolls would hit at least one of the player's blots, counting shots
	 * that need both dice when the point between isn't blocked. While the opponent has a piece on the bar it has to
	 * come in first, so only shots it leaves the dice for count then
	 */
	HIT_ROLLS("hitRolls", 36, false) {
		@Override
		int measure(final Board board, final boolean black) {
			int count = 0;
			for (int roll = 0; roll < 36; roll++) {
				if (rollHits(board, black, roll / 6 + 1, roll % 6 + 1)) {
					count++;
				}
			}
			return count;
		}
	},

	/**
	 * The player's points with two or more pieces on, which the opponent can't land on
	 */
	POINTS_MADE("pointsMade", 7, false) {
		@Override
		int measure(final Board board, final boolean black) {
			return pointsMade(board, black, 1, 24);
		}
	},

	/**
	 * Points made in the player's home quarter, where a hit opponent has to come back in
	 */
	HOME_POINTS("homePoints", 6, true) {
		@Override
		int measure(final Board board, final boolean black) {
			return pointsMade(board, black, 1, 6);
		}
	},

	/**
	 * The longest run of points made next to each other, which blocks the opponent's pieces behind it
	 */
	LONGEST_PRIME("longestPrime", 6, false) {
		@Override
		int measure(final Board board, final boolean black) {
			int longest = 0;
			int run = 0;
			for (int point = 1; point <= 24; point++) {
				run = mine(board, black, point) >= 2 ? run + 1 : 0;
				longest = Math.max(longest, run);
			}
			return longest;
		}
	},

	/**
	 * Points made in the opponent's home quarter, safe places to wait for a shot
	 */
	ANCHORS("anchors", 3, false) {
		@Override
		int measure(final Board board, final boolean black) {
			return pointsMade(board, black, 19, 24);
		}
	},

	/**
	 * The player's pieces still in the opponent's home quarter or on the bar, furthest from home
	 */
	PIECES_BACK("piecesBack", 5, true) {
		@Override
		int measure(final Board board, final boolean black) {
			int back = 0;
			for (int point = 19; point <= 25; point++) {
				back += mine(board, black, point);
			}
			return back;
		}
	},

	/**
	 * The opponent's pieces on the bar, hit and not yet back in
	 */
	OPPONENT_ON_BAR("opponentOnBar", 2, false) {
		@Override
		int measure(final Board board, final boolean black) {
			return theirs(board, black, 0);
		}
	},

	/**
	 * The player's pieces borne off
	 */
	BORNE_OFF("borneOff", 15, true) {
		@Override
		int measure(final Board board, final boolean black) {
			return board.howManyHasPlayerBore(black);
		}
	},

	/**
	 * Pieces beyond three on a point, which are doing little there
	 */
	STACKING("stacking", 10, true) {
		@Override
		int measure(final Board board, final boolean black) {
			int extra = 0;
			for (int point = 1; point <= 24; point++) {
				extra += Math.max(0, mine(board, black, point) - 3);
			}
			return extra;
		}
	};

	private final String label;
	private final double scale;
	private final boolean inRace;

	Feature(final String label, final double scale, final boolean inRace) {
		this.label = label;
		this.scale = scale;
		this.inRace = inRace;
	}

	/**
	 * @return whether it can still change once the players have passed each other, so is worth weighing in a race
	 */
	public boolean isInRace() {
		return inRace;
	}

	/**
	 * @return the name used in the genes' names, such as "pipLead"
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * The feature, unscaled
	 *
	 * @param board the board after the move
	 * @param black the player who moved
	 *
	 * @return the count or difference it measures
	 */
	abstract int measure(Board board, boolean black);

	/**
	 * @return the feature divided by its scale, roughly between -1 and 1
	 */
	double scaled(final Board board, final boolean black) {
		return measure(board, black) / scale;
	}

	/**
	 * Whether the two sides can still hit each other: some piece of each hasn't yet passed all of the other's.
	 * Once they can't, the game is a race, which plays very differently.
	 */
	static boolean isContact(final Board board) {
		// black's furthest back piece, and red's, in the board's numbering: black moves down and red up
		int blackFurthestBack = -1;
		int redFurthestBack = 26;
		for (int point = 0; point <= 25; point++) {
			if (board.Points[point].getBlackCount() > 0) {
				blackFurthestBack = point;
			}
			if (board.Points[25 - point].getRedCount() > 0) {
				redFurthestBack = 25 - point;
			}
		}
		return blackFurthestBack > redFurthestBack;
	}

	/**
	 * The player's pieces on a point, in their own numbering
	 */
	static int mine(final Board board, final boolean black, final int point) {
		return black ? board.Points[point].getBlackCount() : board.Points[25 - point].getRedCount();
	}

	/**
	 * The opponent's pieces on a point, in the player's numbering, where the opponent's bar is 0
	 */
	static int theirs(final Board board, final boolean black, final int point) {
		return black ? board.Points[point].getRedCount() : board.Points[25 - point].getBlackCount();
	}

	private static int pointsMade(final Board board, final boolean black, final int from, final int to) {
		int made = 0;
		for (int point = from; point <= to; point++) {
			if (mine(board, black, point) >= 2) {
				made++;
			}
		}
		return made;
	}

	/**
	 * Whether the opponent's roll hits any of the player's blots. With pieces on the bar, the opponent has to bring
	 * them in before moving anything else: one can hit on the way in or carry on to a blot with the moves the others
	 * don't need, and another piece can hit with whatever's left of the roll once they're all in
	 */
	private static boolean rollHits(final Board board, final boolean black, final int die1, final int die2) {
		final int onBar = theirs(board, black, 0);
		for (int blot = 1; blot <= 24; blot++) {
			if (mine(board, black, blot) != 1) {
				continue;
			}
			if (onBar == 0) {
				for (int from = 1; from < blot; from++) {
					if (theirs(board, black, from) > 0 && canHit(board, black, from, blot - from, die1, die2)) {
						return true;
					}
				}
				continue;
			}
			// a bar piece can hit as it comes in, and go on with the moves the other bar pieces don't need to come in
			final boolean barPieceHits = die1 == die2
					? canHit(board, black, 0, blot, die1, die2, Math.max(1, 5 - onBar))
					: onBar == 1 ? canHit(board, black, 0, blot, die1, die2) : blot == die1 || blot == die2;
			if (barPieceHits) {
				return true;
			}
			if (hitsAfterComingIn(board, black, blot, onBar, die1, die2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Whether the opponent's pieces on the bar can all come in with part of the roll and another piece then hit the
	 * blot with the rest: one die each to come in, so with two different dice only one piece can be on the bar
	 */
	private static boolean hitsAfterComingIn(final Board board, final boolean black, final int blot, final int onBar,
											 final int die1, final int die2) {
		if (die1 != die2) {
			return onBar == 1 && (!blocked(board, black, die1) && opponentOn(board, black, blot - die2)
					|| !blocked(board, black, die2) && opponentOn(board, black, blot - die1));
		}
		if (blocked(board, black, die1)) {
			return false;
		}
		// a double: one move for each piece on the bar to come in, the rest for another piece
		for (int steps = 1; steps <= 4 - onBar; steps++) {
			final int from = blot - die1 * steps;
			if (from < 1) {
				return false;
			}
			if (opponentOn(board, black, from)) {
				boolean open = true;
				for (int step = 1; step < steps; step++) {
					open &= !blocked(board, black, from + die1 * step);
				}
				if (open) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean opponentOn(final Board board, final boolean black, final int point) {
		return point >= 1 && point <= 24 && theirs(board, black, point) > 0;
	}

	/**
	 * Whether an opponent's piece on from, distance short of a blot, can reach it with this roll. A double moves
	 * the same die up to four times, and two different dice can be used either way round; each point landed on on the
	 * way has to be open, not one the player has made
	 */
	private static boolean canHit(final Board board, final boolean black, final int from, final int distance,
								  final int die1, final int die2) {
		return canHit(board, black, from, distance, die1, die2, 4);
	}

	/**
	 * As canHit, with a double only able to move the piece up to maxSteps times
	 */
	private static boolean canHit(final Board board, final boolean black, final int from, final int distance,
								  final int die1, final int die2, final int maxSteps) {
		if (die1 == die2) {
			for (int steps = 1; steps <= maxSteps; steps++) {
				if (die1 * steps == distance) {
					return true;
				}
				if (blocked(board, black, from + die1 * steps)) {
					return false;
				}
			}
			return false;
		}
		if (distance == die1 || distance == die2) {
			return true;
		}
		return distance == die1 + die2
				&& (!blocked(board, black, from + die1) || !blocked(board, black, from + die2));
	}

	private static boolean blocked(final Board board, final boolean black, final int point) {
		return point >= 1 && point <= 24 && mine(board, black, point) >= 2;
	}
}
