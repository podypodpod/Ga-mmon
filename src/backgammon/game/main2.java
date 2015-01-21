package backgammon.game;

import backgammon.genes.Individual;


public class main2 {

	public static void main(String[] args) {
		
		/*
		Population pop = new Population(GenAlgSettings.getPopulationSize(), true);
		
		Individual winner;
		
		System.out.println("STARTing tournament --- ");
		winner = GeneticAlgorithm.tournamentSelection(pop);
		System.out.println("---  tournament finished  --- ");
		
		System.out.println(winner.toString());
		*/
		
		Individual I1 = new Individual();
		I1.loadFromFile("PlayerFromGen0");
	}
}