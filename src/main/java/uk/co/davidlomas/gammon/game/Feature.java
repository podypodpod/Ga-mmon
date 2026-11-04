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
		int measure(final Side side) {
			int mine = 0;
			int theirs = 0;
			for (int point = 0; point < Board.POINTS; point++) {
				mine += point * side.mine(point);
				theirs += (25 - point) * side.theirs(point);
			}
			return theirs - mine;
		}
	},

	/**
	 * The player's points with just one piece on, which the opponent can hit
	 */
	BLOTS("blots", 5, false) {
		@Override
		int measure(final Side side) {
			int blots = 0;
			for (int point = 1; point <= 24; point++) {
				if (side.mine(point) == 1) {
					blots++;
				}
			}
			return blots;
		}
	},

	/**
	 * How many pips the player would lose if all their exposed blots were hit: those that an opponent's piece, or one
	 * on their bar, could hit with a single die, within six points in front of it. The further a blot has come, the
	 * more it has to lose, as a hit piece goes back to the bar at 25
	 */
	PIPS_AT_RISK("pipsAtRisk", 40, false) {
		@Override
		int measure(final Side side) {
			int pips = 0;
			for (int blot = 1; blot <= 24; blot++) {
				if (side.mine(blot) == 1 && opponentWithinSix(side, blot)) {
					pips += 25 - blot;
				}
			}
			return pips;
		}
	},

	/**
	 * The player's points with two or more pieces on, which the opponent can't land on
	 */
	POINTS_MADE("pointsMade", 7, false) {
		@Override
		int measure(final Side side) {
			return pointsMade(side, 1, 24);
		}
	},

	/**
	 * Points made in the player's home quarter, where a hit opponent has to come back in
	 */
	HOME_POINTS("homePoints", 6, true) {
		@Override
		int measure(final Side side) {
			return pointsMade(side, 1, 6);
		}
	},

	/**
	 * The longest run of points made next to each other, which blocks the opponent's pieces behind it
	 */
	LONGEST_PRIME("longestPrime", 6, false) {
		@Override
		int measure(final Side side) {
			int longest = 0;
			int run = 0;
			for (int point = 1; point <= 24; point++) {
				run = side.mine(point) >= 2 ? run + 1 : 0;
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
		int measure(final Side side) {
			return pointsMade(side, 19, 24);
		}
	},

	/**
	 * The player's pieces still in the opponent's home quarter or on the bar, furthest from home
	 */
	PIECES_BACK("piecesBack", 5, true) {
		@Override
		int measure(final Side side) {
			int back = 0;
			for (int point = 19; point <= 25; point++) {
				back += side.mine(point);
			}
			return back;
		}
	},

	/**
	 * The opponent's pieces on the bar, hit and not yet back in
	 */
	OPPONENT_ON_BAR("opponentOnBar", 2, false) {
		@Override
		int measure(final Side side) {
			return side.theirs(0);
		}
	},

	/**
	 * The player's pieces borne off
	 */
	BORNE_OFF("borneOff", 15, true) {
		@Override
		int measure(final Side side) {
			return side.borneOff;
		}
	},

	/**
	 * Pieces beyond three on a point, which are doing little there
	 */
	STACKING("stacking", 10, true) {
		@Override
		int measure(final Side side) {
			int extra = 0;
			for (int point = 1; point <= 24; point++) {
				extra += Math.max(0, side.mine(point) - 3);
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
	int measure(final Board board, final boolean black) {
		return measure(new Side(board, black));
	}

	/**
	 * The feature, unscaled, from the board as one player sees it
	 */
	abstract int measure(Side side);

	/**
	 * @return the feature divided by its scale, roughly between -1 and 1
	 */
	double scaled(final Side side) {
		return measure(side) / scale;
	}

	/**
	 * A board as one player sees it: the pieces on each point in their own numbering, read from the board once, as
	 * every feature looks at them many times. Reading the board each time instead made the benchmark's games about 5%
	 * slower.
	 */
	static final class Side {
		private final int[] mine = new int[Board.POINTS];
		private final int[] theirs = new int[Board.POINTS];
		final int borneOff;

		Side(final Board board, final boolean black) {
			for (int point = 0; point < Board.POINTS; point++) {
				final int onBoard = black ? point : 25 - point;
				mine[point] = board.getCount(onBoard, black);
				theirs[point] = board.getCount(onBoard, !black);
			}
			borneOff = board.howManyHasPlayerBore(black);
		}

		/**
		 * The player's pieces on a point, in their own numbering
		 */
		int mine(final int point) {
			return mine[point];
		}

		/**
		 * The opponent's pieces on a point, in the player's numbering, where the opponent's bar is 0
		 */
		int theirs(final int point) {
			return theirs[point];
		}
	}

	/**
	 * Whether the two sides can still hit each other: some piece of each hasn't yet passed all of the other's.
	 * Once they can't, the game is a race, which plays very differently.
	 */
	static boolean isContact(final Board board) {
		// black's furthest back piece, and red's, in the board's numbering: black moves down and red up
		int blackFurthestBack = -1;
		int redFurthestBack = 26;
		for (int point = 0; point < Board.POINTS; point++) {
			if (board.getBlackCount(point) > 0) {
				blackFurthestBack = point;
			}
			if (board.getRedCount(25 - point) > 0) {
				redFurthestBack = 25 - point;
			}
		}
		return blackFurthestBack > redFurthestBack;
	}

	private static int pointsMade(final Side side, final int from, final int to) {
		int made = 0;
		for (int point = from; point <= to; point++) {
			if (side.mine(point) >= 2) {
				made++;
			}
		}
		return made;
	}

	/**
	 * Whether an opponent's piece, which moves up towards 25, is one to six points short of the point, counting
	 * their bar as 0
	 */
	private static boolean opponentWithinSix(final Side side, final int point) {
		for (int from = Math.max(0, point - 6); from < point; from++) {
			if (side.theirs(from) > 0) {
				return true;
			}
		}
		return false;
	}
}
