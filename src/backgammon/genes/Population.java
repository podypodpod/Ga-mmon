package backgammon.genes;

public class Population {
	
	Individual[] individuals;
	
	public Population( int populationSize, boolean initialise){	
		individuals = new Individual[populationSize];
		
		if(initialise){
			for(int x = 0; x<populationSize; x++){
				Individual newIndividual = new Individual();
				
				individuals[x] = newIndividual;
			}
		}
	}
	
	
	public Individual getFittest(){
		Individual fittest = individuals[0];
		
		for(int x = 1;x<individuals.length; x++){
			if(fittest.getFitness() <= individuals[x].getFitness()){
				fittest=individuals[x];
			}
		}
		return fittest;
	}
	
	public int size(){
		return individuals.length;
	}


    public void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }


	public Individual getIndividual(int i) {
		return individuals[i];
	}
	
	public Individual[] getPopulation(){
		return individuals;
	}
}
