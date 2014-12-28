package backgammon.genes;

public class Population {
	
	Individual[] individuals;
	
	public Population( int populationSize){	
		individuals = new Individual[populationSize];
		
		for(int x = 0; x<populationSize; x++){
			Individual newIndividual = new Individual();
			
			individuals[x] = newIndividual;
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
}
