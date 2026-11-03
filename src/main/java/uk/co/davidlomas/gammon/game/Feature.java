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
			for (int point = 0; point <= 25; point++) {
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
	 * How many of the opponent's 36 possible rolls would hit at least one of the player's blots, counting shots
	 * that need both dice when the point between isn't blocked. While the opponent has a piece on the bar it has to
	 * come in first, so only shots it leaves the dice for count then
	 */
	HIT_ROLLS("hitRolls", 36, false) {
		@Override
		int measure(final Side side) {
			if (side.theirs(0) == 0) {
				return Long.bitCount(hitMask(side));
			}
			// with the opponent on the bar, roll by roll. 3-1 and 1-3 hit the same blots, so each pair of different
			// dice is tried once and counted twice
			int count = 0;
			for (int die1 = 1; die1 <= 6; die1++) {
				for (int die2 = die1; die2 <= 6; die2++) {
					if (rollHits(side, die1, die2)) {
						count += die1 == die2 ? 1 : 2;
					}
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
	 * every feature looks at them many times
	 */
	static final class Side {
		private final int[] mine = new int[26];
		private final int[] theirs = new int[26];
		final int borneOff;

		Side(final Board board, final boolean black) {
			for (int point = 0; point <= 25; point++) {
				final Point onBoard = board.Points[black ? point : 25 - point];
				mine[point] = black ? onBoard.getBlackCount() : onBoard.getRedCount();
				theirs[point] = black ? onBoard.getRedCount() : onBoard.getBlackCount();
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
	 * The rolls, as bits of a 36-bit mask, that would hit at least one of the player's blots, when the opponent has
	 * nothing on the bar. It gives the same rolls as rollHits, worked out for each opponent piece and blot at once
	 * rather than roll by roll
	 */
	private static long hitMask(final Side side) {
		long hits = 0;
		for (int blot = 1; blot <= 24; blot++) {
			if (side.mine(blot) != 1) {
				continue;
			}
			for (int from = Math.max(1, blot - 24); from < blot; from++) {
				if (side.theirs(from) > 0) {
					hits |= hitMask(side, from, blot - from);
				}
			}
		}
		return hits;
	}

	/**
	 * The rolls that take a piece on from the distance to a blot: any roll with that number on a die, two different
	 * dice adding up to it with one of the points between open, or a double whose points on the way are open
	 */
	private static long hitMask(final Side side, final int from, final int distance) {
		long mask = DIRECT_HITS[distance];
		for (final int[] dice : TWO_DICE_HITS[distance]) {
			if (!blocked(side, from + dice[0]) || !blocked(side, from + dice[1])) {
				mask |= rollBit(dice[0], dice[1]) | rollBit(dice[1], dice[0]);
			}
		}
		for (final int[] dieAndSteps : DOUBLE_HITS[distance]) {
			if (openOnTheWay(side, from, dieAndSteps[0], dieAndSteps[1])) {
				mask |= rollBit(dieAndSteps[0], dieAndSteps[0]);
			}
		}
		return mask;
	}

	// For each distance from 1 to 24, worked out once: the rolls with that number on a die, the pairs of different
	// dice that add up to it, and the doubles that reach it in two to four steps
	private static final long[] DIRECT_HITS = new long[25];
	private static final int[][][] TWO_DICE_HITS = new int[25][][];
	private static final int[][][] DOUBLE_HITS = new int[25][][];

	static {
		for (int distance = 1; distance <= 24; distance++) {
			final java.util.List<int[]> twoDice = new java.util.ArrayList<>();
			final java.util.List<int[]> doubles = new java.util.ArrayList<>();
			for (int die1 = 1; die1 <= 6; die1++) {
				for (int die2 = 1; die2 <= 6; die2++) {
					if (die1 == distance || die2 == distance) {
						DIRECT_HITS[distance] |= rollBit(die1, die2);
					}
				}
				for (int die2 = die1 + 1; die2 <= 6; die2++) {
					if (die1 + die2 == distance && die1 != distance && die2 != distance) {
						twoDice.add(new int[]{die1, die2});
					}
				}
				for (int steps = 2; steps <= 4; steps++) {
					if (die1 * steps == distance) {
						doubles.add(new int[]{die1, steps});
					}
				}
			}
			TWO_DICE_HITS[distance] = twoDice.toArray(new int[0][]);
			DOUBLE_HITS[distance] = doubles.toArray(new int[0][]);
		}
	}

	private static long rollBit(final int die1, final int die2) {
		return 1L << ((die1 - 1) * 6 + die2 - 1);
	}

	/**
	 * Whether the points a double lands on before its last step are all open
	 */
	private static boolean openOnTheWay(final Side side, final int from, final int die, final int steps) {
		for (int step = 1; step < steps; step++) {
			if (blocked(side, from + die * step)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Whether the opponent's roll hits any of the player's blots. With pieces on the bar, the opponent has to bring
	 * them in before moving anything else: one can hit on the way in or carry on to a blot with the moves the others
	 * don't need, and another piece can hit with whatever's left of the roll once they're all in
	 */
	private static boolean rollHits(final Side side, final int die1, final int die2) {
		final int onBar = side.theirs(0);
		for (int blot = 1; blot <= 24; blot++) {
			if (side.mine(blot) != 1) {
				continue;
			}
			if (onBar == 0) {
				for (int from = 1; from < blot; from++) {
					if (side.theirs(from) > 0 && canHit(side, from, blot - from, die1, die2)) {
						return true;
					}
				}
				continue;
			}
			// a bar piece can hit as it comes in, and go on with the moves the other bar pieces don't need to come in
			final boolean barPieceHits = die1 == die2
					? canHit(side, 0, blot, die1, die2, Math.max(1, 5 - onBar))
					: onBar == 1 ? canHit(side, 0, blot, die1, die2) : blot == die1 || blot == die2;
			if (barPieceHits) {
				return true;
			}
			if (hitsAfterComingIn(side, blot, onBar, die1, die2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Whether the opponent's pieces on the bar can all come in with part of the roll and another piece then hit the
	 * blot with the rest: one die each to come in, so with two different dice only one piece can be on the bar
	 */
	private static boolean hitsAfterComingIn(final Side side, final int blot, final int onBar,
											 final int die1, final int die2) {
		if (die1 != die2) {
			return onBar == 1 && (!blocked(side, die1) && opponentOn(side, blot - die2)
					|| !blocked(side, die2) && opponentOn(side, blot - die1));
		}
		if (blocked(side, die1)) {
			return false;
		}
		// a double: one move for each piece on the bar to come in, the rest for another piece
		for (int steps = 1; steps <= 4 - onBar; steps++) {
			final int from = blot - die1 * steps;
			if (from < 1) {
				return false;
			}
			if (opponentOn(side, from)) {
				boolean open = true;
				for (int step = 1; step < steps; step++) {
					open &= !blocked(side, from + die1 * step);
				}
				if (open) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean opponentOn(final Side side, final int point) {
		return point >= 1 && point <= 24 && side.theirs(point) > 0;
	}

	/**
	 * Whether an opponent's piece on from, distance short of a blot, can reach it with this roll. A double moves
	 * the same die up to four times, and two different dice can be used either way round; each point landed on on the
	 * way has to be open, not one the player has made
	 */
	private static boolean canHit(final Side side, final int from, final int distance,
								  final int die1, final int die2) {
		return canHit(side, from, distance, die1, die2, 4);
	}

	/**
	 * As canHit, with a double only able to move the piece up to maxSteps times
	 */
	private static boolean canHit(final Side side, final int from, final int distance,
								  final int die1, final int die2, final int maxSteps) {
		if (die1 == die2) {
			for (int steps = 1; steps <= maxSteps; steps++) {
				if (die1 * steps == distance) {
					return true;
				}
				if (blocked(side, from + die1 * steps)) {
					return false;
				}
			}
			return false;
		}
		if (distance == die1 || distance == die2) {
			return true;
		}
		return distance == die1 + die2
				&& (!blocked(side, from + die1) || !blocked(side, from + die2));
	}

	private static boolean blocked(final Side side, final int point) {
		return point >= 1 && point <= 24 && side.mine(point) >= 2;
	}
}
