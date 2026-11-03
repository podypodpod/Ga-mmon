package uk.co.davidlomas.gammon.game;

/**
 * Things about a board that a player weighs up when choosing a move, measured from that player's side.
 * <p>
 * Each one is worked out in the player's own numbering: their home quarter is 1 to 6, their bar is 25, and they move
 * down towards 0. For black that's the board's own numbering, and for red it's turned round, point p being 25 - p.
 * The opponent moves the other way, up from their bar at 0.
 * <p>
 * Each feature is divided by its scale, so they're all roughly between -1 and 1 and a weight means much the same
 * for each of them.
 */
public enum Feature {

	/**
	 * How many pips the opponent needs to bear off, less how many the player needs
	 */
	PIP_LEAD("pipLead", 100) {
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
	BLOTS("blots", 5) {
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
	 * The player's blots that an opponent's piece, or one on their bar, could hit with a single die
	 */
	EXPOSED_BLOTS("exposedBlots", 5) {
		@Override
		int measure(final Board board, final boolean black) {
			int exposed = 0;
			for (int point = 1; point <= 24; point++) {
				if (mine(board, black, point) == 1 && opponentWithinSix(board, black, point)) {
					exposed++;
				}
			}
			return exposed;
		}
	},

	/**
	 * The player's points with two or more pieces on, which the opponent can't land on
	 */
	POINTS_MADE("pointsMade", 7) {
		@Override
		int measure(final Board board, final boolean black) {
			return pointsMade(board, black, 1, 24);
		}
	},

	/**
	 * Points made in the player's home quarter, where a hit opponent has to come back in
	 */
	HOME_POINTS("homePoints", 6) {
		@Override
		int measure(final Board board, final boolean black) {
			return pointsMade(board, black, 1, 6);
		}
	},

	/**
	 * The longest run of points made next to each other, which blocks the opponent's pieces behind it
	 */
	LONGEST_PRIME("longestPrime", 6) {
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
	ANCHORS("anchors", 3) {
		@Override
		int measure(final Board board, final boolean black) {
			return pointsMade(board, black, 19, 24);
		}
	},

	/**
	 * The player's pieces still in the opponent's home quarter or on the bar, furthest from home
	 */
	PIECES_BACK("piecesBack", 5) {
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
	OPPONENT_ON_BAR("opponentOnBar", 2) {
		@Override
		int measure(final Board board, final boolean black) {
			return theirs(board, black, 0);
		}
	},

	/**
	 * The player's pieces borne off
	 */
	BORNE_OFF("borneOff", 15) {
		@Override
		int measure(final Board board, final boolean black) {
			return board.howManyHasPlayerBore(black);
		}
	},

	/**
	 * Pieces beyond three on a point, which are doing little there
	 */
	STACKING("stacking", 10) {
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

	Feature(final String label, final double scale) {
		this.label = label;
		this.scale = scale;
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
	 * Whether an opponent's piece is 1 to 6 pips short of the point, the way the opponent moves
	 */
	private static boolean opponentWithinSix(final Board board, final boolean black, final int point) {
		for (int from = Math.max(0, point - 6); from < point; from++) {
			if (theirs(board, black, from) > 0) {
				return true;
			}
		}
		return false;
	}
}
