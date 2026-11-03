package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.settings.GameSettings;

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
		final Individual ai = new Individual();
		if (args.length > 0) {
			ai.loadFromFile(args[0]);
		}

		// player 2 is played on the console, not by its genes
		GameSettings.setAreBothAIsCache(false);
		final GameStats result = new GameManager().playIndividualsVsEachOther(ai, new Individual());

		System.out.println(result.getVictor() == ai
				? "The AI won " + result.getWinType().describe() + "."
				: "You won " + result.getWinType().describe() + ".");
	}
}
