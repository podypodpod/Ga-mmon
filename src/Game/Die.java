package Game;
/**
 * The Class Die.
 */
public class Die {
	
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
	
	
	public int getDieValue(){
		return value;
	}
	
	public void setDieValue(int x){
		value = x;
	}
	
}
