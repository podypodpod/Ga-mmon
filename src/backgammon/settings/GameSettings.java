package backgammon.settings;

// TODO: Auto-generated Javadoc
/**
 * GameSettings class
 * 
 * Only created this to simplify changing the settings of the game
 * 
 * Puts all settings for the actual game in one place..e.g. timeDelay for if it actually needs to be seen
 */
public class GameSettings {
	
	/** if player 1 is black, and therefore player 2 will be red. */
	private static boolean isP1Black = true;

	/** if both players are QI or one human. */
	private static boolean areBothAIs = true;

	/** The time delay on the gui printing, 0 time delay wil not display the game well. */
	private static int timeDelay = 0;
	
	/**  whether there actually is a GUI not. */
	private static boolean displayGUI = false;
	
	/** whether to disply console prints or not. */
	private static boolean displayConsole = false;
	
	
	/**
	 * Gets the time delay.
	 *
	 * @return the time delay
	 */
	public static int getTimeDelay(){
		return timeDelay;
	}
	
	/**
	 * Gets the are both ai.
	 *
	 * @return the are both ai
	 */
	public static boolean getAreBothAI(){
		return areBothAIs;
	}
	
	/**
	 * Checks if is p1 black.
	 *
	 * @return true, if is p1 black
	 */
	public static boolean isP1Black(){
		return isP1Black;
	}

	/**
	 * Gets the non gui mode.
	 *
	 * @return the non gui mode
	 */
	public static boolean getDisplayGUI() {
		return displayGUI;
	}
	
	/**
	 * Gets the display console.
	 *
	 * @return the display console
	 */
	public static boolean getDisplayConsole(){
		return displayConsole;
	}
	
}