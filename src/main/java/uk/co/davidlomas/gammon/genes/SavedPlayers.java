package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.genes.SavedAttempts.Attempt;
import uk.co.davidlomas.gammon.genes.SearchProgress.GenerationResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The players earlier searches have saved. Fitness only says how a player did against its own population, so it can't
 * compare players from different searches. Their points against the reference player can, as that's the same opponent
 * and the same dice every time, so that's what best goes by. It's only measured every few generations, so only those
 * players can be picked.
 */
public final class SavedPlayers {

	/**
	 * What the initial population is called in the stats
	 */
	public static final String INITIAL = "initial";

	/**
	 * A player from file
	 *
	 * @param name                   where it came from, such as "Attempt 3, generation 40"
	 * @param pointsAgainstReference its average points a game against the reference player
	 */
	public record SavedPlayer(String name, Individual player, double pointsAgainstReference) {
	}

	private record Measured(Attempt attempt, String generation, double points) {
	}

	private SavedPlayers() {
	}

	/**
	 * @return the best players on file, best first, fewer than count if there aren't that many. Players with the same
	 * genes only count once, which is common as the best of one generation is carried over to the next
	 */
	public static List<SavedPlayer> best(final int count) {
		return best(new File(Individual.PLAYERS_DIR), count);
	}

	static List<SavedPlayer> best(final File folder, final int count) {
		final List<Measured> measured = new ArrayList<>();
		for (final Attempt attempt : SavedAttempts.all(folder)) {
			for (final GenerationResult generation : attempt.generations()) {
				if (generation.pointsAgainstReference() != null) {
					measured.add(new Measured(attempt, generation.generation(), generation.pointsAgainstReference()));
				}
			}
		}
		// by attempt and then file order within the same points, so the same files always give the same answer
		measured.sort(Comparator.comparingDouble(Measured::points).reversed()
				.thenComparingInt(m -> m.attempt().number()));

		final List<SavedPlayer> best = new ArrayList<>();
		final Set<String> genes = new HashSet<>();
		for (final Measured candidate : measured) {
			if (best.size() == count) {
				break;
			}
			final Individual player = candidate.attempt().player(candidate.generation());
			if (player != null && genes.add(String.valueOf(player.getChromosome()))) {
				best.add(new SavedPlayer(name(candidate.attempt().number(), candidate.generation()), player,
						candidate.points()));
			}
		}
		return best;
	}

	/**
	 * @return where a player came from, such as "Attempt 3, generation 40"
	 */
	public static String name(final int attempt, final String generation) {
		return "Attempt " + attempt + ", " + generationName(generation);
	}

	/**
	 * @return a generation as said in words, such as "generation 40" or "initial population"
	 */
	public static String generationName(final String generation) {
		return INITIAL.equals(generation) ? "initial population" : "generation " + generation;
	}
}
