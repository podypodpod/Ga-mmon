package backgammon.settings;

/**
 * The Class GenAlgSettings.
 */
public class GenAlgSettings {

	/** The Constant uniformRate. */
    private static final double uniformRate = 0.5;
    
    /** The Constant mutationRate. */
    private static final double mutationRate = 0.015;
    
    /** The Constant tournamentSize. */
    private static final int tournamentSize = 2;
    
    /** The Constant elitism. */
    private static final boolean elitism = true;
    
    /** The Constant generations. */
    private static final int generations = 2;
    
    /** the constant population size */
    private static final int populationSize = 5;
    
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
	

}
