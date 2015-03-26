package backgammon.genes;

import backgammon.settings.GenAlgSettings;

/**
 * Main
 * 
 * The main class to run for the genetic algorithm to run
 * 
 * will run for the size and generations set in backgammon.settings/genalgsettings
 * 
 * @author David Lomas - 11035527
 */
public class Main {

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Program running!");

		Population pop = new Population(GenAlgSettings.getPopulationSize(), true);
		System.out.println("Initial Population created, size: "+GenAlgSettings.getPopulationSize()+", generations: "+GenAlgSettings.getGenerations() );
		
		//save initial fittest
		System.out.println("Calculating fittest of initial pop");
		pop.calculateFitness();
		Individual fittest = pop.getFittest();
		fittest.saveToFile("FittestFromInitialPopulation");
				
        //evolve once
		System.out.println("-------------------------");
		System.out.println("Evolving population");
		pop = GeneticAlgorithm.evolvePopulation(pop);
		fittest = pop.getFittest();
		fittest.saveToFile("PlayerFromGen"+0);
		System.out.println("--------Evolved!--------- this was population 0");
		
		
		for(int x = 0; x<GenAlgSettings.getGenerations()-1;x++){
			System.out.println("-------------------------");
			System.out.println("Evolving population");
			pop = GeneticAlgorithm.evolvePopulation(pop);
			System.out.println("Calculating fitness");
			fittest = pop.getFittest();
			fittest.saveToFile("PlayerFromGen"+(x+1));
			System.out.println("--------Evolved!--------- this was population "+(x+1));
		}
		
        System.out.println("------------Finished-------");
        System.out.println("Solution:");
        System.out.println(pop.getFittest().toString());
	}
}
