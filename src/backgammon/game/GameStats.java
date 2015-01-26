package backgammon.game;

import backgammon.genes.Individual;

public class GameStats {
	
	Individual gameVictor = null;
	
	double playerOneScore = 0;
	double playerTwoScore = 0;
	
	Board liveBoard = null;
	
	public GameStats(Game g){
		
		liveBoard = g.getLiveBoard();
		
		if(liveBoard.hasPlayerWon(g.Player1.black)){
			double reductions = liveBoard.howManyHasPlayerBore(g.Player2.black)*0.04;
			if(g.getIndiv1()!=null){playerOneScore=1-reductions;}
			if(g.getIndiv2()!=null){playerTwoScore = reductions;}
			
			gameVictor = g.getIndiv1();
			
		}else if(liveBoard.hasPlayerWon(g.Player2.black)){
			double reductions = liveBoard.howManyHasPlayerBore(g.Player1.black)*0.04;
			if(g.getIndiv2()!=null){playerTwoScore=1-reductions;}
			if(g.getIndiv1()!=null){playerOneScore = reductions;}
			
			gameVictor = g.getIndiv2();
		}
	}

	public Individual getVictor() {
		return gameVictor;
	}
	
	public double getPlayerOneScore(){
		return playerOneScore;
	}

}
