package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.game.PubEval;
import uk.co.davidlomas.gammon.game.ReferencePlayer;

import java.util.Locale;

/** args: meetings, then player files (or "reference"): each one's average points a game against pubeval */
public class VsPubEval {
	public static void main(final String[] args) throws Exception {
		final int meetings = Integer.parseInt(args[0]);
		final long[] seeds = new long[meetings];
		for (int i = 0; i < meetings; i++) {
			seeds[i] = 9_000_000 + i;
		}
		final PubEval pubEval = new PubEval();
		for (int a = 1; a < args.length; a++) {
			final Individual player = args[a].equals("reference") ? new ReferencePlayer() : HeadToHead.load(args[a]);
			final double points = FitnessCalculator.averagePoints(player, pubEval, seeds, null);
			System.out.println(String.format(Locale.ROOT, "RESULT %s v pubeval: %+.3f", args[a].substring(args[a].lastIndexOf('/') + 1), points));
		}
	}
}
