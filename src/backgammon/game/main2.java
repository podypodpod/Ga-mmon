package backgammon.game;

import backgammon.genes.GeneticAlgorithm;
import backgammon.genes.Individual;
import backgammon.genes.Population;
import backgammon.settings.GenAlgSettings;

public class main2 {

	public static void main(String[] args) {
		
		Population pop = new Population(GenAlgSettings.getPopulationSize(), true);
		
		Individual winner;
		
		System.out.println("STARTing tournament --- ");
		winner = GeneticAlgorithm.tournamentSelection(pop);
		System.out.println("---  tournament finished  --- ");
		
		System.out.println(winner.toString());
	}
}