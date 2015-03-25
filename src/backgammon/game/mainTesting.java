package backgammon.game;

import backgammon.genes.Individual;


public class mainTesting {

	public static void main(String[] args) {
		/*
		System.out.println("---------INDIV 1----------");
		Individual x = new Individual();
		x.loadFromFile("PlayerFromGen");
		System.out.println(x.toString());

		
		
		System.out.println("----------INDIV 2---------");
		Individual x2 = new Individual();
		x2.loadFromFile("PlayerFromGen3");
		System.out.println(x2.toString());

		
		int x1won = 0;
		int x2won = 0;
		
		GameManager gn = new GameManager();
		
		for(int i = 0; i<20; i++){
			GameStats gs = gn.playIndividualsVsEachOther(x, x2);
			if(gs.getVictor().equals(x)){x1won++;}else{x2won++;};
		}
		
		
		System.out.println("X won: "+x1won);
		System.out.println("X1 won: "+x2won);
		*/
		
		GameManager gn = new GameManager();
		
		Individual x = new Individual();
		x.loadFromFile("PlayerFromGen0");
		System.out.println("Individual from generation 1, stats: "+x.toString());
		
		Individual x2 = new Individual();
		x2.loadFromFile("PlayerFromGen49");
		System.out.println("Individual from generation 50, stats: "+x2.toString());
		
			int num = 0;
			for(int e = 0; e<1000 ; e++){
				GameStats gs = gn.playIndividualsVsEachOther(x, x2);
				if(gs.getVictor().equals(x2)){
					num++;
				}
			}
			System.out.println("Individual from gen 50 won "+ num + "/1000 games against player from generation 1");

	}
}