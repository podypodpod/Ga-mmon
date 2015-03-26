package backgammon.genes;
/**
 * The Class Population.
 * 
 * collection of individuals for the genetic algorithm to evolve
 * 
 * @author David Lomas - 11035527
 */
public class Population {
	
	/** The individuals. */
	Individual[] individuals;
	
	/**
	 * Instantiates a new population.
	 *
	 * @param populationSize the population size
	 * @param initialise the initialise
	 */
	public Population( int populationSize, boolean initialise){	
		individuals = new Individual[populationSize];
		
		if(initialise){
			for(int x = 0; x<populationSize; x++){
				Individual newIndividual = new Individual();
				
				individuals[x] = newIndividual;
			}
		}
	}
	
	/**
	 * Gets the fittest.
	 *
	 * @return the fittest
	 */
	public Individual getFittest(){
		
		Individual fittest = individuals[0];
		
		for(int x = 1;x<individuals.length; x++){
			if(fittest.getFitness() <= individuals[x].getFitness()){
				fittest=individuals[x];
			}
		}
		return fittest;
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size(){
		return individuals.length;
	}


    /**
     * Save individual.
     *
     * @param index the index
     * @param indiv the indiv
     */
    public void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }


	/**
	 * Gets the individual.
	 *
	 * @param i the i
	 * @return the individual
	 */
	public Individual getIndividual(int i) {
		return individuals[i];
	}
	
	/**
	 * Gets the population.
	 *
	 * @return the population
	 */
	public Individual[] getPopulation(){
		return individuals;
	}
	
	/**
	 * Sets the array.
	 *
	 * @param indivs the new array
	 */
	public void setArray(Individual[] indivs){
		individuals = indivs;
	}
	
	/**
	 * calculates fitness of whole population
	 * 
	 * Uses round robin to play all against each other
	 */
	public void calculateFitness(){
		FitnessCalculator.calculateFitnessOfPopulation(this);
	}
	
	public String toString(){
		return "Population of size: "+size();
	}
}
