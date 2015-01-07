package backgammon.genes;

import backgammon.game.GameManager;

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
	 */
	public static void getFitnessOf(Individual i1) {		
		gm = new GameManager();
		System.out.println("Playing 1 indivs and 1 null against each other, Game: "+count++);
		gm.playIndividualsVsEachOther(i1, null);
	}
	
	/**
	 * battles the populations individuals against each other
	 * 
	 * playing against another AI player
	 * 
	 * @param pop the population you are measuring
	 * @return the fitness
	 */
	public static Individual getFitnessOf(Individual i1, Individual i2) {
		Individual victor = null;
		
		gm = new GameManager();
		
		System.out.println("Playing 2 indivs against each other, Game number: "+count++);
		
		victor = gm.playIndividualsVsEachOther(i1, i2);
		
		return victor;
	}

}
