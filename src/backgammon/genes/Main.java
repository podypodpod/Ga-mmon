package backgammon.genes;

import backgammon.settings.GenAlgSettings;

public class Main {

	public static void main(String[] args) {
		Population pop = new Population(GenAlgSettings.getPopulationSize(), true);
		System.out.println("Initial Population created, size: "+GenAlgSettings.getPopulationSize());
		
		//save initial fittest
		pop.getFittest().saveToFile("PlayerFromGen");
		
        //System.out.println("------------Fittest Of Initial population: " + pop.getFittest().toString()+"-----------");
		
        //evolve once
		pop = GeneticAlgorithm.evolvePopulation(pop);
		pop.getFittest().saveToFile("PlayerFromGen"+"0");
		System.out.println("--------Evolved!--------- this was population 0");
		
		
		for(int x = 0; x<GenAlgSettings.getGenerations()-1;x++){
			pop = GeneticAlgorithm.evolvePopulation(pop);
			System.out.println("--------Evolved!--------- this was population "+(x+1));
			pop.getFittest().saveToFile("PlayerFromGen"+x);
		}
		
        System.out.println("------------Finished-------");
        //System.out.println("Solution:");
        //System.out.println(pop.getFittest().toString());
	}
}
