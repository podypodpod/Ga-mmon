package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;

/**
 * Told how a game is going, so it can be watched. It's called on the thread playing the game, once a turn, so it has to
 * be quick, and it mustn't use the game's random numbers, as that would change how the game plays out
 */
public interface GameObserver {

	void gameStarted(Individual playerOne, Individual playerTwo, boolean playerOneIsBlack);

	/**
	 * @param board the board after the turn, which won't change after this
	 * @param turns how many turns have been played so far
	 */
	void turnPlayed(Board board, int turns);

	void gameFinished(GameStats stats);
}
