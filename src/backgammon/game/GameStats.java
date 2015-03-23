package backgammon.game;

import backgammon.genes.Individual;

public class GameStats {
	
	Individual gameVictor = null;
	
	double playerOneScore = 0;
	double playerTwoScore = 0;
	
	
	public GameStats(Individual winner, double p1score, double p2score){
		gameVictor = winner;
		playerOneScore = p1score;
		playerOneScore = p2score;
	}

	public Individual getVictor() {
		return gameVictor;
	}
	
	public double getPlayerOneScore(){
		return playerOneScore;
	}

}
