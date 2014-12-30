package backgammon.genes;

import backgammon.settings.GenAlgSettings;

public class GeneticAlgorithm {

    /* GA parameters */
    private static final double uniformRate =GenAlgSettings.getUniformRate();
    private static final double mutationRate =GenAlgSettings.getMutationRate();
    private static final int tournamentSize = GenAlgSettings.getTournamentSize();
    private static final boolean elitism = GenAlgSettings.isElitism();
	
	public static Population evolvePopulation(Population pop) {
		
		Population newPopulation = new Population(pop.size(), false);
		
		// Keep our best individual
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        
        // Loop over the population size and create new individuals with
        // crossover
        for (int i = elitismOffset; i < pop.size(); i++) {
        	
            Individual indiv1 = tournamentSelection(pop);
            Individual indiv2 = tournamentSelection(pop);
            
            Individual newIndiv = crossover(indiv1, indiv2);
            
            newPopulation.saveIndividual(i, newIndiv);
        }

        // Mutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
	}
	
	
    private static Individual crossover(Individual indiv1, Individual indiv2) {
    	
        Individual newSol = new Individual();
        
        // Crossover
        if (Math.random() <= uniformRate) {
            newSol.setAgressionChance(indiv1.getAgressionChance());
        } else {
            newSol.setAgressionChance(indiv2.getAgressionChance());
        }
        
        if (Math.random() <= uniformRate) {
            newSol.setDefensiveChance(indiv1.getDefensiveChance());
        } else {
            newSol.setDefensiveChance(indiv2.getDefensiveChance());
        }
        
        return newSol;
    }

    // Mutate an individual
    private static void mutate(Individual indiv) {
        // Loop through genes
        if (Math.random() <= mutationRate) {
            indiv.setAgressionChance(Math.random());
        }
        if (Math.random() <= mutationRate) {
            indiv.setDefensiveChance(Math.random());
        }
    }

    // Select individuals for crossover
    private static Individual tournamentSelection(Population pop) {
    	
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false);
        
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
        	
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        
        // Get the fittest
        Individual fittest = tournament.getFittest();
        
        return fittest;
    }
}
