package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.settings.GameSettings;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Plays one game on the console against an AI player: you're player 2, it's player 1.
 * <p>
 * Give it a saved player's file, such as savedPlayers/Attempt - 3/PlayerFromGen49, to play that player, or nothing to
 * play one with random genes.
 */
public final class PlayAgainstAi {

	private PlayAgainstAi() {
	}

	public static void main(final String[] args) {
		play(args, System.in, System.out);
	}

	/**
	 * Plays the game, reading the person's moves from in and writing the board, prompts and result to out
	 */
	static void play(final String[] args, final InputStream in, final PrintStream out) {
		final Individual ai = new Individual();
		if (args.length > 0) {
			final File saved = new File(args[0]);
			if (!saved.isFile()) {
				out.println("There's no saved player at " + saved.getPath() + ".");
				return;
			}
			try {
				ai.loadFromFile(saved.getPath());
			} catch (final IllegalArgumentException e) {
				out.println("That player can't be played: " + e.getMessage() + ".");
				return;
			}
		}

		// player 2 is played on the console, not by its genes
		GameSettings.setAreBothAIsCache(false);
		final GameStats result = new GameManager().playAgainstPerson(ai, in, out);

		out.println(result.getVictor() == ai
				? "The AI won " + result.getWinType().describe() + "."
				: "You won " + result.getWinType().describe() + ".");
	}
}
