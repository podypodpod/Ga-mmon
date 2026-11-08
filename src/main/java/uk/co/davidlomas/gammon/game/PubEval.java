package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Gerry Tesauro's pubeval, the benchmark player backgammon programs have been measured against since he posted it in
 * 1993: a weighted sum over a raw encoding of the board, with one set of weights for contact and one for a race. It's
 * a stronger yardstick than ReferencePlayer, and results against it can be compared with published ones.
 * <p>
 * This follows his pubeval.c line for line, with his weights, in src/main/resources/pubeval. Its genes aren't used.
 * Don't change how it plays, or results against it stop being comparable.
 */
public final class PubEval extends Individual implements ChoosesOwnMoves {

	private static final int INPUTS = 122;
	private static final float[] RACE = weights("WT.race");
	private static final float[] CONTACT = weights("WT.cntc");
	// as pubeval.c returns for all men off, which as a float is 1e8
	static final float ALL_OFF = 99999999.0f;

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
		return MoveChooser.highestScoring(boards, board -> score(board, black, race));
	}

	/**
	 * pubeval picks its weights by the position before the move, as pubeval.c says to
	 */
	@Override
	public Board chooseMove(final List<Board> boards, final boolean black, final Board before) {
		return choose(boards, black, !Feature.isContact(before));
	}

	/**
	 * pubeval(race, pos): the board's score for the player who has just moved. It's worked out in float, as in
	 * pubeval.c, so boards that score almost the same are ordered as they are there
	 */
	static float score(final Board board, final boolean black, final boolean race) {
		final int[] pos = position(board, black);
		if (pos[26] == 15) {
			// all men off, best possible move
			return ALL_OFF;
		}
		final float[] x = inputs(pos);
		final float[] w = race ? RACE : CONTACT;
		float score = 0.0f;
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
		// pubeval.c defines this, the opponent's men off, but never reads it, so it doesn't change the score
		pos[27] = -board.howManyHasPlayerBore(!black);
		return pos;
	}

	/**
	 * setx(pos): the 122 inputs, five for each point and two more
	 */
	static float[] inputs(final int[] pos) {
		final float[] x = new float[INPUTS];
		// first encode board locations 24-1
		for (int j = 1; j <= 24; ++j) {
			final int jm1 = j - 1;
			final int n = pos[25 - j];
			if (n != 0) {
				if (n == -1) {
					x[5 * jm1] = 1.0f;
				}
				if (n == 1) {
					x[5 * jm1 + 1] = 1.0f;
				}
				if (n >= 2) {
					x[5 * jm1 + 2] = 1.0f;
				}
				if (n == 3) {
					x[5 * jm1 + 3] = 1.0f;
				}
				if (n >= 4) {
					x[5 * jm1 + 4] = (float) ((n - 3) / 2.0);
				}
			}
		}
		// encode opponent barmen
		x[120] = (float) (-pos[0] / 2.0);
		// encode computer's menoff
		x[121] = (float) (pos[26] / 15.0);
		return x;
	}

	/**
	 * @throws UncheckedIOException if the weights aren't there, or aren't 122 numbers
	 */
	private static float[] weights(final String file) {
		try (InputStream in = PubEval.class.getResourceAsStream("/pubeval/" + file)) {
			if (in == null) {
				throw new IOException("missing");
			}
			final String[] numbers = new String(in.readAllBytes(), StandardCharsets.US_ASCII).trim().split("\\s+");
			if (numbers.length != INPUTS) {
				throw new IOException("has " + numbers.length + " numbers, not " + INPUTS);
			}
			final float[] weights = new float[INPUTS];
			for (int i = 0; i < INPUTS; i++) {
				weights[i] = Float.parseFloat(numbers[i]);
			}
			return weights;
		} catch (final IOException | NumberFormatException e) {
			throw new UncheckedIOException(new IOException("Couldn't read pubeval's weights in " + file, e));
		}
	}
}
