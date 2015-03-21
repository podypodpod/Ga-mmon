package backgammon.game;

import backgammon.genes.Individual;


public class main2 {

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
		
		System.out.println("---------INDIV 1----------");
		Individual x = new Individual();
		System.out.println(x.toString());
		
		System.out.println("----------INDIV 2---------");
		Individual x2 = new Individual();
		System.out.println(x2.toString());
		
		System.out.println("--------------------------");
		System.out.println(x.toString());
		x.saveToFile("WIZZLELELE");
		
		Individual newGuy = new Individual();
		newGuy.loadFromFile("WIZZLELELE");
		System.out.println(newGuy.toString());
		System.out.println("--------------------------");
		GameStats gs = gn.playIndividualsVsEachOther(x, x2);
		System.out.println("--------------------------");
		System.out.println("VICTOR! = "+gs.getVictor());
		
	}
}