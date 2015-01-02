package backgammon.game;

import backgammon.genes.Individual;

public class main2 {

	public static void main(String[] args) {
		GameManager Game = new GameManager();
		Game.playIndividualsVsEachOther(new Individual(), new Individual());
	}

}
