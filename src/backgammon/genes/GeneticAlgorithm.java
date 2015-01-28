package backgammon.genes;

import backgammon.settings.GenAlgSettings;

/**
 * The Class GeneticAlgorithm.
 */
public class GeneticAlgorithm {

	/**
	 * Evolve population.
	 *
	 * @param pop the pop
	 * @return the population
	 */
	public static Population evolvePopulation(Population pop) {

		if(GenAlgSettings.isDisplayconsole()){System.out.println("------EVOLVE POPULATION-------------------");}
		
		Population newPopulation = new Population(pop.size(), false);

		// Keep our best individual
		if (GenAlgSettings.isElitism()) {
			newPopulation.saveIndividual(0, pop.getFittest());
		}

		// Crossover population
		int elitismOffset;
		if (GenAlgSettings.isElitism()) {
			elitismOffset = 1;
		} else {
			elitismOffset = 0;
		}

		// Loop over the population size and create new individuals with the crossover function
		// the two individuals that are crossed over are the winners of the tournament selection (should provide some of the best players)
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
	 * Randomly takes one side or the others attributes according to the uniform rate
	 *
	 * @param indiv1 the indiv1
	 * @param indiv2 the indiv2
	 * @return the individual
	 */
	private static Individual crossover(Individual indiv1, Individual indiv2) {

		if(GenAlgSettings.isDisplayconsole()){System.out.println("------CROSSOVER-------------------");}
		
		Individual newIndiv = new Individual();
		// Crossover
		
		for(int x = 0; x<indiv1.getNumOfAttributes(); x++){
			if (Math.random() <= GenAlgSettings.getUniformRate()) {
				newIndiv.setAttribute(x, indiv1.getAttribute(x));
			} else {
				newIndiv.setAttribute(x, indiv2.getAttribute(x));
			}
		}
		return newIndiv;
	}


	/**
	 * Mutate the individual.
	 *
	 * gets a random num, if its <= to the mutation rate then switch the bit
	 *
	 * @param indiv the individual
	 */
	public static void mutate(Individual indiv) {

		if(GenAlgSettings.isDisplayconsole()){System.out.println("------MUTATING-------------------");}
		
		// get the indivs string
		char[] indivStr = indiv.getChromosome();

		//loop through all bits
		for(int x=0;x<indivStr.length;x++){
			//random chance
			if(Math.random() <= GenAlgSettings.getMutationRate()){
				//swap the bit over
				if(indivStr[x]=='0'){
					indivStr[x]='1';
				}else{
					indivStr[x]='0';
				}
			}
		}
	}

	/**
	 * Tournament selection.
	 * 
	 * Selects the individuals for crossover
	 * it does this by selecting a random population and playing them against each other until there is one victor
	 *
	 * @param pop the pop
	 * @return the individual
	 */
	public static Individual tournamentSelection(Population pop) {
		
		if(GenAlgSettings.isDisplayconsole()){System.out.println("------TOURNAMENT SELECTION-------------------");}

		// Create a tournament population
		Population tournament = new Population(GenAlgSettings.getTournamentSize(), false);

		// For each place in the tournament get a random individual
		for (int i = 0; i < GenAlgSettings.getTournamentSize(); i++) {

			int randomId = (int) (Math.random() * pop.size());
			tournament.saveIndividual(i, pop.getIndividual(randomId));
		}

		//Get the fittest via battling against each other
		Individual tournamentVictor = null;

		//until there is a winner
		while(tournamentVictor==null){

			//create a population for the winner of the round
			Individual[] winnersOfRound = new Individual[tournament.size()/2];

			Individual winnerOfRound = null;
			int counter = 0;
			for(int i = 0; i < tournament.size(); i+=2){
				winnerOfRound = FitnessCalculator.getWinnerOf(tournament.getIndividual(i), tournament.getIndividual(i+1));
				winnersOfRound[counter++]=winnerOfRound;
			}

			//if there is only 1 left, then it is the winner!
			if(winnersOfRound.length==1){
				tournamentVictor = winnersOfRound[0];
			}
			//setting the tournament array to the winners of the round, so now only has the winners in
			else{
				tournament.setArray(winnersOfRound);
			}
		}
		return tournamentVictor;
	}
}
