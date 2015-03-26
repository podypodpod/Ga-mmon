package backgammon.game;

import backgammon.genes.Individual;

/**
 * GameStats
 * 
 * The game stats class is returned after a finished game and supplies the winning player as well as a score for the game.
 * 
 * @author David Lomas - 11035527
 */
public class GameStats {
	
	Individual gameVictor = null;
	
	double playerOneScore = 0;
	double playerTwoScore = 0;
	
	/**
	 * GameStats default constructor
	 * 
	 * @param winner
	 * @param p1score
	 * @param p2score
	 */
	public GameStats(Individual winner, double p1score, double p2score){
		gameVictor = winner;
		playerOneScore = p1score;
		playerOneScore = p2score;
	}

	/**
	 * getVictor
	 * 
	 * @return individual
	 */
	public Individual getVictor() {
		return gameVictor;
	}
	
	/**
	 * getPlayerOneScore
	 * 
	 * @return individual
	 */
	public double getPlayerOneScore(){
		return playerOneScore;
	}
}