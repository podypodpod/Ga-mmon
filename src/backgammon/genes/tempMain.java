package backgammon.genes;

import backgammon.settings.GenAlgSettings;

public class tempMain {

	public static void main(String[] args) {

		Population pop = new Population(GenAlgSettings.getPopulationSize(), true);

		
        System.out.println("Initial population: " + pop.getFittest().toString());
		
		pop = GeneticAlgorithm.evolvePopulation(pop);
		System.out.println("--------Evolved!---------:"+pop.getFittest().toString()+" this was population 0");
		for(int x = 0; x<GenAlgSettings.getGenerations()-1;x++){
			pop = GeneticAlgorithm.evolvePopulation(pop);
			System.out.println("--------Evolved!---------:"+pop.getFittest().toString()+" this was population "+(x+1));
		}
		
        System.out.println("Finished");
        System.out.println("Solution:");
        System.out.println(pop.getFittest().toString());
	}
}
