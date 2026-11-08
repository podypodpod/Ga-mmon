package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerry Tesauro's pubeval, the benchmark player backgammon programs have been measured against since he posted it in
 * 1993: a weighted sum over a raw encoding of the board, with one set of weights for contact and one for a race. It's
 * a stronger yardstick than ReferencePlayer, and results against it can be compared with published ones.
 * <p>
 * This follows his pubeval.c line for line, with his weights, in src/main/resources/pubeval. Its genes aren't used.
 * Don't change how it plays, or results against it stop being comparable.
 */
public final class PubEval extends Individual {

	private static final int INPUTS = 122;
	private static final double[] RACE = weights("WT.race");
	private static final double[] CONTACT = weights("WT.cntc");

	/**
	 * Picks the move to play: the highest scoring, with ties broken at random
	 *
	 * @param boards the boards the legal moves lead to
	 * @param black  the player choosing
	 * @param race   whether the position before the move was a race, which picks the weights
	 *
	 * @return the board to play, or null if there are no moves
	 */
	static Board choose(final List<Board> boards, final boolean black, final boolean race) {
		final List<Board> best = new ArrayList<>();
		double bestScore = Double.NEGATIVE_INFINITY;
		for (final Board board : boards) {
			final double score = score(board, black, race);
			if (score > bestScore) {
				best.clear();
				bestScore = score;
			}
			if (score == bestScore) {
				best.add(board);
			}
		}
		if (best.isEmpty()) {
			return null;
		}
		return best.size() == 1 ? best.get(0) : best.get(RandomSource.nextInt(best.size()));
	}

	/**
	 * pubeval(race, pos): the board's score for the player who has just moved
	 */
	static double score(final Board board, final boolean black, final boolean race) {
		final int[] pos = position(board, black);
		if (pos[26] == 15) {
			// all men off, best possible move
			return 99999999.0;
		}
		final double[] x = inputs(pos);
		final double[] w = race ? RACE : CONTACT;
		double score = 0;
		for (int i = 0; i < INPUTS; i++) {
			score += w[i] * x[i];
		}
		return score;
	}

	/**
	 * The board as pubeval.c wants it, from the mover's point of view: 1 to 24 are the points, the mover's men
	 * positive and moving down from 24 to 1, the opponent's negative. 25 is the mover's bar, 0 the opponent's, 26 the
	 * mover's men off and 27 the opponent's, negative
	 */
	static int[] position(final Board board, final boolean black) {
		final Feature.Side side = new Feature.Side(board, black);
		final int[] pos = new int[28];
		for (int point = 1; point <= 24; point++) {
			pos[point] = side.mine(point) - side.theirs(point);
		}
		pos[25] = side.mine(25);
		pos[0] = -side.theirs(0);
		pos[26] = side.borneOff;
		pos[27] = -board.howManyHasPlayerBore(!black);
		return pos;
	}

	/**
	 * setx(pos): the 122 inputs, five for each point and two more
	 */
	static double[] inputs(final int[] pos) {
		final double[] x = new double[INPUTS];
		// first encode board locations 24-1
		for (int j = 1; j <= 24; ++j) {
			final int jm1 = j - 1;
			final int n = pos[25 - j];
			if (n != 0) {
				if (n == -1) {
					x[5 * jm1] = 1.0;
				}
				if (n == 1) {
					x[5 * jm1 + 1] = 1.0;
				}
				if (n >= 2) {
					x[5 * jm1 + 2] = 1.0;
				}
				if (n == 3) {
					x[5 * jm1 + 3] = 1.0;
				}
				if (n >= 4) {
					x[5 * jm1 + 4] = (n - 3) / 2.0;
				}
			}
		}
		// encode opponent barmen
		x[120] = -pos[0] / 2.0;
		// encode computer's menoff
		x[121] = pos[26] / 15.0;
		return x;
	}

	/**
	 * @throws UncheckedIOException if the weights aren't there, or aren't 122 numbers
	 */
	private static double[] weights(final String file) {
		try (InputStream in = PubEval.class.getResourceAsStream("/pubeval/" + file)) {
			if (in == null) {
				throw new IOException("missing");
			}
			final String[] numbers = new String(in.readAllBytes(), StandardCharsets.US_ASCII).trim().split("\\s+");
			if (numbers.length != INPUTS) {
				throw new IOException("has " + numbers.length + " numbers, not " + INPUTS);
			}
			final double[] weights = new double[INPUTS];
			for (int i = 0; i < INPUTS; i++) {
				// stored as floats in pubeval.c
				weights[i] = Float.parseFloat(numbers[i]);
			}
			return weights;
		} catch (final IOException | NumberFormatException e) {
			throw new UncheckedIOException(new IOException("Couldn't read pubeval's weights in " + file, e));
		}
	}
}
