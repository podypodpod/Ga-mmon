package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;

/**
 * Told how a game is going, so it can be watched. It's called on the thread playing the game, once a turn, so the game
 * waits for it: it has to be quick, unless it means to slow the game down. It mustn't use the game's random numbers, as
 * that would change how the game plays out
 */
public interface GameObserver {

	/**
	 * For a game nothing is watching
	 */
	GameObserver NONE = new GameObserver() {
	};

	default void gameStarted(final Individual playerOne, final Individual playerTwo, final boolean playerOneIsBlack) {
	}

	/**
	 * @param board the board after the turn. A person's turn changes it in place, so read what's needed from it now
	 */
	default void turnPlayed(final Board board) {
	}

	default void gameFinished(final GameStats stats) {
	}
}
