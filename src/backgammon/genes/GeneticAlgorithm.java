package backgammon.genes;

import backgammon.settings.GenAlgSettings;

// TODO: Auto-generated Javadoc
/**
 * The Class GeneticAlgorithm.
 */
public class GeneticAlgorithm {

    /* GA parameters */
    /** The Constant uniformRate. */
    private static final double uniformRate =GenAlgSettings.getUniformRate();
    
    /** The Constant mutationRate. */
    private static final double mutationRate =GenAlgSettings.getMutationRate();
    
    /** The Constant tournamentSize. */
    private static final int tournamentSize = GenAlgSettings.getTournamentSize();
    
    /** The Constant elitism. */
    private static final boolean elitism = GenAlgSettings.isElitism();
	
	/**
	 * Evolve population.
	 *
	 * @param pop the pop
	 * @return the population
	 */
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
	
	
    /**
     * Crossover.
     * 
     * 
     *
     * @param indiv1 the indiv1
     * @param indiv2 the indiv2
     * @return the individual
     */
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

    
    /**
     * Mutate the individual.
     *
     * gets a random num, if its <= to the mutation rate then switch the bit
     *
     * @param indiv the individual
     */
    public static void mutate(Individual indiv) {
    	
        // get the indivs string
    	char[] indivStr = indiv.getPersonalityString();
    	
    	//loop through all bits
    	for(int x=0;x<indivStr.length;x++){
    		//random chance
    		if(Math.random() <= mutationRate){
    			//swap the bit over
    			if(indivStr[x]=='0'){
    				indivStr[x]='1';
    			}else{
    				indivStr[x]='0';
    			}
    		}
    	}
    }

    // Select individuals for crossover
    /**
     * Tournament selection.
     *
     * @param pop the pop
     * @return the individual
     */
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
