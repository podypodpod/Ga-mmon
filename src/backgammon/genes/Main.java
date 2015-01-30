package backgammon.genes;

import backgammon.settings.GenAlgSettings;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("Program running!");

		Population pop = new Population(GenAlgSettings.getPopulationSize(), true);
		System.out.println("Initial Population created, size: "+GenAlgSettings.getPopulationSize());
		
		//save initial fittest
		System.out.println("Calculating fittest of initial pop");
		Individual fittest = pop.getFittest();


		
		fittest.saveToFile("PlayerFromGen");
				
        //evolve once
		pop = GeneticAlgorithm.evolvePopulation(pop);
		System.out.println("Calculating fitness");
		fittest = pop.getFittest();
		fittest.saveToFile("PlayerFromGen"+0);
		System.out.println("--------Evolved!--------- this was population 0");
		
		
		for(int x = 0; x<GenAlgSettings.getGenerations()-1;x++){
			pop = GeneticAlgorithm.evolvePopulation(pop);
			System.out.println("--------Evolved!--------- this was population "+(x+1));
			System.out.println("Calculating fitness");
			fittest = pop.getFittest();
			fittest.saveToFile("PlayerFromGen"+x);
		}
		
        System.out.println("------------Finished-------");
        //System.out.println("Solution:");
        //System.out.println(pop.getFittest().toString());
	}
}
