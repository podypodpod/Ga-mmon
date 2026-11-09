package uk.co.davidlomas.gammon.genes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * args: meetings, then "A vs B" pairs of player files. Genes are read by name, and any the file doesn't have are set to
 * 50, a weight of 0, so a player from before features were added plays as it did
 */
public class HeadToHead {
	public static void main(final String[] args) throws IOException {
		final int meetings = Integer.parseInt(args[0]);
		final long[] seeds = new long[meetings];
		for (int i = 0; i < meetings; i++) {
			seeds[i] = 8_000_000 + i;
		}
		for (int a = 1; a + 1 < args.length; a += 2) {
			final double points = FitnessCalculator.averagePoints(load(args[a]), load(args[a + 1]), seeds, null);
			System.out.println(String.format(Locale.ROOT, "RESULT %s v %s: %+.3f", name(args[a]), name(args[a + 1]), points));
		}
	}

	private static String name(final String path) {
		return path.substring(Math.max(path.lastIndexOf('/'), path.lastIndexOf(java.io.File.separatorChar)) + 1);
	}

	static Individual load(final String path) throws IOException {
		final Properties saved = new Properties();
		try (InputStream in = new FileInputStream(path)) {
			saved.load(in);
		}
		final Individual player = new Individual();
		for (int gene = 0; gene < player.getNumOfAttributes(); gene++) {
			final IndividualAttribute attribute = player.getAttribute(gene);
			final String value = saved.getProperty(attribute.getName());
			player.setAttribute(gene, new IndividualAttribute(attribute.getName(), value == null ? 50 : Integer.parseInt(value.trim())));
		}
		return player;
	}
}
