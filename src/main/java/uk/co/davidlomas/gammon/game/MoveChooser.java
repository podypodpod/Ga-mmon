package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * How an AI player chooses its move: it scores the board each legal move leads to by its features, each multiplied by
 * the weight its genes give that feature, and plays the highest scoring one.
 * <p>
 * There are two sets of weights, one for while the two sides can still hit each other and one for the race after,
 * as what matters changes. The race set only has the features that can still change in a race. Each gene's value,
 * 0 to 100, gives a weight from -1 to 1, so a player can learn that a feature is bad as well as good.
 */
public final class MoveChooser {

	private static final Feature[] CONTACT_FEATURES = Feature.values();
	private static final Feature[] RACE_FEATURES =
			Arrays.stream(Feature.values()).filter(Feature::isInRace).toArray(Feature[]::new);

	private MoveChooser() {
	}

	/**
	 * The names of the genes, in order: every feature's weight for contact, then the race features' weights
	 */
	public static String[] geneNames() {
		final String[] names = new String[CONTACT_FEATURES.length + RACE_FEATURES.length];
		for (int i = 0; i < CONTACT_FEATURES.length; i++) {
			names[i] = "contact" + capitalised(CONTACT_FEATURES[i].getLabel());
		}
		for (int i = 0; i < RACE_FEATURES.length; i++) {
			names[CONTACT_FEATURES.length + i] = "race" + capitalised(RACE_FEATURES[i].getLabel());
		}
		return names;
	}

	/**
	 * @return the gene holding a feature's weight while the players can hit each other
	 */
	static int contactGene(final Feature feature) {
		return feature.ordinal();
	}

	/**
	 * @return the gene holding a feature's weight in a race
	 * @throws IllegalArgumentException if the feature isn't weighed in a race
	 */
	static int raceGene(final Feature feature) {
		final int index = Arrays.asList(RACE_FEATURES).indexOf(feature);
		if (index < 0) {
			throw new IllegalArgumentException(feature + " isn't weighed in a race");
		}
		return CONTACT_FEATURES.length + index;
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
		// its own loop rather than highestScoring's, as every evolved player's every move comes through here, and
		// calling a scorer for each board could slow the GA
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
	 * @return the first of the boards on which the player has won, null if there isn't one
	 */
	static Board winning(final List<Board> boards, final boolean black) {
		for (final Board board : boards) {
			if (board.hasPlayerWon(black)) {
				return board;
			}
		}
		return null;
	}

	/**
	 * @return the highest scoring board, with ties broken at random, or null if there are none. The same as choose
	 * does, for the fixed opponents, who score boards their own way
	 */
	static Board highestScoring(final List<Board> boards, final ToDoubleFunction<Board> scorer) {
		final List<Board> best = new ArrayList<>();
		double bestScore = Double.NEGATIVE_INFINITY;
		for (final Board board : boards) {
			final double score = scorer.applyAsDouble(board);
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
		final Feature.Side side = new Feature.Side(board, black);
		double score = 0;
		if (Feature.isContact(board)) {
			for (int i = 0; i < CONTACT_FEATURES.length; i++) {
				score += weight(individual, i) * CONTACT_FEATURES[i].scaled(side);
			}
		} else {
			for (int i = 0; i < RACE_FEATURES.length; i++) {
				score += weight(individual, CONTACT_FEATURES.length + i) * RACE_FEATURES[i].scaled(side);
			}
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
