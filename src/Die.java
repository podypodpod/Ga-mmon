/**
 * The Class Die.
 */
public class Die {
	
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
		return (int)(Math.random()*6)+1;
	}
	
}
