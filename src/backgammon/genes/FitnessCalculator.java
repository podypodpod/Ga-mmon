package backgammon.genes;

import backgammon.game.GameManager;
import backgammon.game.GameStats;
import backgammon.settings.GenAlgSettings;

public class FitnessCalculator {
	
	private static GameManager gm;
	private static int count =0;
	
	
	/**
	 * measures how close to the target it is                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	 * 
	 * playing against a random picker
	 * 
	 * @param i the individual you are measuring
	 * @return the fitness
	 
	public static void getFitneOf(Individual i1) {		
		gm = new GameManager();
		if(GenAlgSettings.isDisplayconsole()){System.out.println("Playing 1 indivs and 1 null against each other, Game: "+count++);}
		gm.playIndividualsVsEachOther(i1, null);
	}*/
	

	/**
	 * 
	 * Makes the 2 players play against each other and returns the winner
	 * 
	 * @param i1 player 1
	 * @param i2 player 2
	 * @return the winner
	 */
	public static Individual getWinnerOf(Individual i1, Individual i2) {
		
		gm = new GameManager();
		
		if(GenAlgSettings.isDisplayconsole()){System.out.println("Playing 2 indivs against each other, Game number: "+count++);}
		
		GameStats gs = gm.playIndividualsVsEachOther(i1, i2);
		
		return gs.getVictor();
	}
	
	
	/**
	 * 
	 * @param pop
	 */
	public static void calculateFitnessOfPopulation(Population pop){
		
		gm = new GameManager();
		if(GenAlgSettings.isDisplayconsole()){System.out.println("Round robin started");}
		//looping the whole population, x is the one we are measure
		for(int x = 0; x<pop.individuals.length; x++){
			
			if(GenAlgSettings.isDisplayconsole()){System.out.println("Testing Player: "+ x);}
			 
			//the one we are generating the fitness of
			Individual testSubject = pop.individuals[x];
			//will be added to over the course of the games
			double tempFitness = 0;
			
			//looping the whole population, y is the one currently playing against x
			for(int y = 0; y<pop.individuals.length; y++){
				//make sure its not playing itself
				if(y!=x){
					Individual oponent = pop.individuals[y];
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
