package backgammon.game;

import backgammon.genes.GeneticAlgorithm;
import backgammon.genes.IndivAttribute;
import backgammon.genes.Individual;
import backgammon.genes.Population;


public class main2 {

	public static void main(String[] args) {
		
		System.out.println("---------INDIV 1----------");
		Individual x = new Individual();
		System.out.println(x.toString());

		
		
		System.out.println("----------INDIV 2---------");
		Individual x2 = new Individual();
		System.out.println(x2.toString());

		System.out.println("---------------------------");
		Population pop = new Population(2, false);
		Individual[] ar = {x,x2};
		pop.setArray(ar);

		System.out.println(pop.toString());
		GeneticAlgorithm.evolvePopulation(pop);
		
	}
}