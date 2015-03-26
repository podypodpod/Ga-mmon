package backgammon.genes;

import backgammon.game.GameManager;
import backgammon.game.GameStats;
import backgammon.settings.GenAlgSettings;

/**
 * FitnessCalculator
 * 
 * Allows the calculation of fitness of an indiviudal or population
 * 
 * @author David Lomas - 11035527
 */
public class FitnessCalculator{
	
	/** Game manager */
	private static GameManager gm;
	
	/**
	 * getWinnerOf
	 * 
	 * Makes the 2 players play against each other and returns the winner
	 * 
	 * @param i1 player 1
	 * @param i2 player 2
	 * @return the winner
	 */
	public static Individual getWinnerOf(Individual i1, Individual i2) {
		gm = new GameManager();
		
		//if(GenAlgSettings.isDisplayconsole()){System.out.println("Playing 2 indivs against each other, Game number: "+count++);}
		
		GameStats gs = gm.playIndividualsVsEachOther(i1, i2);
		
		return gs.getVictor();
	}
	
	
	/**
	 * calculateFitnessOfPopulation
	 * 
	 * calculates the fitness of every individual in the population.
	 * 
	 * requires playing a lot of games ((population-1)^2)
	 * 
	 * @param pop - population we are measuring
	 */
	public static void calculateFitnessOfPopulation(Population pop){
		
		Individual testSubject;
		Individual oponent;
		double tempFitness;
		
		gm = new GameManager();
		
		if(GenAlgSettings.isDisplayconsole()){System.out.println("Round robin started");}
		
		//looping the whole population, x is the one we are measuring
		for(int x = 0; x<pop.individuals.length; x++){
			
			if(GenAlgSettings.isDisplayconsole()){System.out.println("Testing Player: "+ x);}
			 
			//the one we are generating the fitness of
			testSubject = pop.individuals[x];
			
			//will be added to over the course of the games
			tempFitness = 0;
			
			//looping the whole population, y is the one currently playing against x
			for(int y = 0; y<pop.individuals.length; y++){
				//make sure its not playing itself
				if(y!=x){
					if(GenAlgSettings.isDisplayconsole()){System.out.println("\tagainst player: "+ (y+1)+"/"+pop.individuals.length);}
					
					//Opponent individual
					oponent = pop.individuals[y];
					
					//adding the result of the game to the temp fitness, so this will add all the games score together	
					GameStats gs = gm.playIndividualsVsEachOther(testSubject, oponent);
					
					tempFitness+=gs.getPlayerOneScore();
				}
			}
			if(tempFitness!=0){
				//divides by the number of games and therefore gets an average
				tempFitness=tempFitness/(pop.size()-1);
			}
			testSubject.setFitness(tempFitness);
		}
	}
}