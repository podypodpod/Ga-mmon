package backgammon.game;
// TODO: Auto-generated Javadoc
/**
 * The Class Die.
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
		//using Die1
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
