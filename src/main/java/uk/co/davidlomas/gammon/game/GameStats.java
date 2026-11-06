package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;

/**
 * GameStats
 * <p>
 * The game stats class is returned after a finished game and supplies the
 * winning player as well as a score for the game.
 *
 * @author David Lomas
 */
public class GameStats {

	private Individual gameVictor = null;
	private WinType winType = null;
	private double playerOneScore = 0;
	private double playerTwoScore = 0;
	private int turns = 0;

	/**
	 * GameStats default constructor
	 *
	 * @param gameVictor     the victor
	 * @param winType        how they won
	 * @param playerOneScore player one's score
	 * @param playerTwoScore player two's score
	 */
	public GameStats(final Individual gameVictor, final WinType winType, final double playerOneScore,
					 final double playerTwoScore) {
		this.gameVictor = gameVictor;
		this.winType = winType;
		this.playerOneScore = playerOneScore;
		this.playerTwoScore = playerTwoScore;
	}

	/**
	 * As the default constructor, for a game between two AIs, whose turns are counted
	 *
	 * @param turns how many turns the two players had between them
	 */
	GameStats(final Individual gameVictor, final WinType winType, final double playerOneScore,
			  final double playerTwoScore, final int turns) {
		this(gameVictor, winType, playerOneScore, playerTwoScore);
		this.turns = turns;
	}

	/**
	 * getPlayerOneScore
	 *
	 * @return the individuals score
	 */
	public double getPlayerOneScore() {
		return playerOneScore;
	}

	/**
	 * getVictor
	 *
	 * @return individual, or null if the game was a draw
	 */
	public Individual getVictor() {
		return gameVictor;
	}

	public double getPlayerTwoScore() {
		return playerTwoScore;
	}

	/**
	 * @return how many turns the two players had between them, if they were both AIs, otherwise 0
	 */
	public int getTurns() {
		return turns;
	}

	/**
	 * @return true if nobody won: the game went on too long and was cut short, so there's no victor
	 */
	public boolean isDraw() {
		return winType == WinType.DRAW;
	}

	/**
	 * @return how the game was won, which sets the points it was worth
	 */
	public WinType getWinType() {
		return winType;
	}

}