package backgammon.settings;

/**
 * The Class GenAlgSettings.
 * 
 * @author David Lomas - 11035527
 */
public class GenAlgSettings {
	
	/** The Constant displayConsole. */
	private static final boolean displayConsole = true;

	/** The Constant uniformRate. */
    private static final double uniformRate = 0.5;
    
    /** The Constant mutationRate. */
    private static final double mutationRate = 0.3;
    
    /** The Constant tournamentSize. Has to be a multiple of 4 for the tournament method to work correctly */
    private static final int tournamentSize = 8;
    
    /** The Constant elitism. */
    private static final boolean elitism = true;
    
    /** The Constant generations. */
    private static final int generations = 50;
    
    /** the constant population size */
    private static final int populationSize = 10;
    
    private static final String savePath = "savedPlayers/Attempt5/";
    
    public static double getUniformrate() {
		return uniformRate;
	}

	public static double getMutationrate() {
		return mutationRate;
	}

	public static int getTournamentsize() {
		return tournamentSize;
	}

	public static int getPopulationsize() {
		return populationSize;
	}

	public static String getSavePath() {
		return savePath;
	}

	/**
     * Gets the uniformrate.
     *
     * @return the uniformrate
     */
    public static double getUniformRate() {
		return uniformRate;
	}

	/**
	 * Gets the mutationrate.
	 *
	 * @return the mutationrate
	 */
	public static double getMutationRate() {
		return mutationRate;
	}

	/**
	 * Gets the tournamentsize.
	 *
	 * @return the tournamentsize
	 */
	public static int getTournamentSize() {
		return tournamentSize;
	}

	/**
	 * Checks if is elitism.
	 *
	 * @return true, if is elitism
	 */
	public static boolean isElitism() {
		return elitism;
	}

	/**
	 * Gets the generations.
	 *
	 * @return the generations
	 */
	public static int getGenerations() {
		return generations;
	}

	public static int getPopulationSize() {
		return populationSize;
	}

	public static boolean isDisplayconsole() {
		return displayConsole;
	}
	

}
