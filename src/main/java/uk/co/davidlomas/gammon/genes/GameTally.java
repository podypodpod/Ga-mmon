package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.game.WinType;

/**
 * What happened in a set of games: how many there were, how long they took and how they ended
 */
final class GameTally {

	private int games = 0;
	private long turns = 0;
	private final int[] endings = new int[WinType.values().length];

	void add(final GameStats game) {
		games++;
		turns += game.getTurns();
		endings[game.getWinType().ordinal()]++;
	}

	int getGames() {
		return games;
	}

	/**
	 * @return the average turns in a game, 0 if there were no games
	 */
	double turnsPerGame() {
		return games == 0 ? 0 : (double) turns / games;
	}

	/**
	 * @return the share of the games, 0 to 1, that ended this way, 0 if there were no games
	 */
	double rate(final WinType ending) {
		return games == 0 ? 0 : (double) endings[ending.ordinal()] / games;
	}
}
