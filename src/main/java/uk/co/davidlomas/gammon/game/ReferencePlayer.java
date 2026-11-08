package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;

import java.util.List;

/**
 * A fixed, hand-written opponent for the benchmark to measure evolved players against. Its genes aren't used: it
 * scores each board by a few rules of thumb of its own, with weights picked by hand.
 * <p>
 * It reads the board itself rather than through Feature, so changing the evolved players' features doesn't change
 * it. Don't change how it plays: the benchmark's strength results are only comparable from one commit to the next
 * while it stays the same.
 */
public final class ReferencePlayer extends Individual implements ChoosesOwnMoves {

	/**
	 * Picks the move to play: one that wins the game if there is one, otherwise the highest scoring, with ties broken
	 * at random
	 *
	 * @return the board to play, or null if there are no moves
	 */
	static Board choose(final List<Board> boards, final boolean black) {
		final Board winning = MoveChooser.winning(boards, black);
		return winning != null ? winning : MoveChooser.highestScoring(boards, board -> score(board, black));
	}

	@Override
	public Board chooseMove(final List<Board> boards, final boolean black, final Board before) {
		return choose(boards, black);
	}

	/**
	 * How good the board looks to the player. In its own numbering, as Feature's: the player's home is 1 to 6 and
	 * their bar 25, and the opponent's bar is 0
	 */
	static double score(final Board board, final boolean black) {
		final int[] mine = new int[Board.POINTS];
		final int[] theirs = new int[Board.POINTS];
		for (int point = 0; point < Board.POINTS; point++) {
			final int onBoard = black ? point : 25 - point;
			mine[point] = board.getCount(onBoard, black);
			theirs[point] = board.getCount(onBoard, !black);
		}

		int myPips = 0;
		int theirPips = 0;
		int myFurthestBack = 0;
		int theirFurthestBack = 25;
		for (int point = 0; point < Board.POINTS; point++) {
			myPips += point * mine[point];
			theirPips += (25 - point) * theirs[point];
			if (mine[point] > 0) {
				myFurthestBack = point;
			}
			if (theirs[25 - point] > 0) {
				theirFurthestBack = 25 - point;
			}
		}
		final double pipLead = theirPips - myPips;
		// a race once every piece of the player's is past every one of the opponent's
		if (myFurthestBack < theirFurthestBack) {
			final int borneOff = black ? board.getBlackBore() : board.getRedBore();
			return pipLead + 2 * borneOff;
		}

		int exposedBlots = 0;
		int homePoints = 0;
		int pointsMade = 0;
		for (int point = 1; point <= 24; point++) {
			if (mine[point] >= 2) {
				pointsMade++;
				if (point <= 6) {
					homePoints++;
				}
			}
			if (mine[point] == 1) {
				for (int from = Math.max(0, point - 6); from < point; from++) {
					if (theirs[from] > 0) {
						exposedBlots++;
						break;
					}
				}
			}
		}
		return pipLead - 8 * exposedBlots + 3 * pointsMade + 4 * homePoints + 10 * theirs[0];
	}
}
