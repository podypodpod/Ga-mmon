package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;

import java.util.List;

/**
 * EXPERIMENT: a player whose score is a small network over pubeval's raw board encoding, with real-valued weights.
 * One set of weights for contact and one for a race, picked by the position before the move, as pubeval does.
 * Each set: a weight per input, then for each hidden unit a weight per input, its bias and its weight in the score.
 */
public final class NetPlayer extends Individual implements ChoosesOwnMoves {

	public static final int INPUTS = 122;

	public final int hidden;
	public final float[] contact;
	public final float[] race;

	public NetPlayer(final int hidden, final float[] contact, final float[] race) {
		this.hidden = hidden;
		this.contact = contact;
		this.race = race;
	}

	public static int size(final int hidden) {
		return INPUTS + hidden * (INPUTS + 2);
	}

	@Override
	public Board chooseMove(final List<Board> boards, final boolean black, final Board before) {
		final Board winning = MoveChooser.winning(boards, black);
		if (winning != null) {
			return winning;
		}
		final float[] w = Feature.isContact(before) ? contact : race;
		return MoveChooser.highestScoring(boards, board -> score(board, black, w));
	}

	double score(final Board board, final boolean black, final float[] w) {
		final float[] x = PubEval.inputs(PubEval.position(board, black));
		double score = 0;
		for (int i = 0; i < INPUTS; i++) {
			score += w[i] * x[i];
		}
		for (int h = 0; h < hidden; h++) {
			final int first = INPUTS + h * (INPUTS + 2);
			double sum = w[first + INPUTS];
			for (int i = 0; i < INPUTS; i++) {
				sum += w[first + i] * x[i];
			}
			score += w[first + INPUTS + 1] * Math.tanh(sum);
		}
		return score;
	}
}
