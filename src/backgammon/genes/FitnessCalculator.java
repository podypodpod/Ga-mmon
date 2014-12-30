package backgammon.genes;

import backgammon.game.GameManager;

public class FitnessCalculator {
	
	private static GameManager gm;
	private static int count =0;
	/**
	 * measures how close to the target it is
	 * 
	 * @param i the individual you are measuring
	 * @return the fitness
	 */
	public static void getFitnessOf(Individual i1) {		
		gm = new GameManager();
		System.out.println("Playing 2 indivs, Game: "+count++);
		gm.playIndividualsVsEachOther(i1, null);
	}

}
