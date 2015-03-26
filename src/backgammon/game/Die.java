package backgammon.game;

/**
 * The Class Die.
 * 
 * Simply a dice roll class.
 * 
 * @author David Lomas - 11035527
 */
public class Die {
	
	/** The value. */
	private int value;
	
	/**
	 * Instantiates a new die.
	 */
	public Die(){
		
	}

	/**
	 * Roll die.
	 *
	 * @return the int
	 */
	public int RollDie(){

		value = (int)(Math.random()*6)+1;
		return value;
	}
	
	/**
	 * Gets the die value.
	 *
	 * @return the die value
	 */
	public int getDieValue(){
		return value;
	}
	
	/**
	 * Sets the die value.
	 *
	 * @param x the new die value
	 */
	public void setDieValue(int x){
		value = x;
	}
}