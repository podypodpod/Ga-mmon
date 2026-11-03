package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * How an AI player chooses its move: it scores the board each legal move leads to by its features, each multiplied by
 * the weight its genes give that feature, and plays the highest scoring one.
 * <p>
 * There are two sets of weights, one for while the two sides can still hit each other and one for the race after,
 * as what matters changes. Each gene's value, 0 to 100, gives a weight from -1 to 1, so a player can learn that a
 * feature is bad as well as good.
 */
public final class MoveChooser {

	private MoveChooser() {
	}

	/**
	 * The names of the genes, in order: every feature's weight for contact, then for the race
	 */
	public static String[] geneNames() {
		final Feature[] features = Feature.values();
		final String[] names = new String[2 * features.length];
		for (final Feature feature : features) {
			names[feature.ordinal()] = "contact" + capitalised(feature.getLabel());
			names[features.length + feature.ordinal()] = "race" + capitalised(feature.getLabel());
		}
		return names;
	}

	/**
	 * Picks the move to play: one that wins the game if there is one, otherwise the highest scoring, with ties broken
	 * at random
	 *
	 * @param boards     the boards the legal moves lead to
	 * @param black      the player choosing
	 * @param individual their genes
	 *
	 * @return the board to play, or null if there are no moves
	 */
	static Board choose(final List<Board> boards, final boolean black, final Individual individual) {
		final List<Board> best = new ArrayList<>();
		double bestScore = Double.NEGATIVE_INFINITY;
		for (final Board board : boards) {
			if (board.hasPlayerWon(black)) {
				return board;
			}
			final double score = score(board, black, individual);
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
	 * How good the board looks to the player, by their weights for the stage the game is at
	 */
	static double score(final Board board, final boolean black, final Individual individual) {
		final Feature[] features = Feature.values();
		final int firstGene = Feature.isContact(board) ? 0 : features.length;
		double score = 0;
		for (final Feature feature : features) {
			score += weight(individual, firstGene + feature.ordinal()) * feature.scaled(board, black);
		}
		return score;
	}

	/**
	 * A gene's value, 0 to 100, as a weight from -1 to 1
	 */
	static double weight(final Individual individual, final int gene) {
		return (individual.getAttribute(gene).getValue() - 50) / 50.0;
	}

	private static String capitalised(final String label) {
		return Character.toUpperCase(label.charAt(0)) + label.substring(1);
	}
}
