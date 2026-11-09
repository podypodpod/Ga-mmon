package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.game.ReferencePlayer;

import java.io.File;
import java.util.Locale;

/** Measures saved players against the reference player over fresh meetings: args are meetings, then attempt:gen pairs */
public class Measure {
	public static void main(final String[] args) {
		final int meetings = Integer.parseInt(args[0]);
		final long[] seeds = new long[meetings];
		for (int i = 0; i < meetings; i++) {
			seeds[i] = 1_000_000 + i;
		}
		final ReferencePlayer reference = new ReferencePlayer();
		for (int a = 1; a < args.length; a++) {
			final String[] parts = args[a].split(":");
			final Individual player = SavedAttempts.load(new File("savedPlayers/Attempt - " + parts[0]), parts[1]);
			final double points = FitnessCalculator.averagePoints(player, reference, seeds, null);
			System.out.println(String.format(Locale.ROOT, "RESULT attempt %s gen %s: %+.3f", parts[0], parts[1], points));
		}
	}
}
