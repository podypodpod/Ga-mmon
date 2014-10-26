package Game;

// TODO: Auto-generated Javadoc
/**
 * The Class Point.
 */
public class Point {

	/** The black count. */
	private int blackCount;
	
	/** The red count. */
	private int redCount;
	
	/**
	 * Instantiates a new point.
	 */
	public Point(){
		blackCount = 0;
		redCount = 0;
	}
	
	//setBlackCount
	/**
	 * Sets the black count.
	 *
	 * @param bc the new black count
	 */
	public void setBlackCount(int bc){
		blackCount = bc;
	}
	//setRedCount
	/**
	 * Sets the red count.
	 *
	 * @param rc the new red count
	 */
	public void setRedCount(int rc){
		redCount = rc;
	}
	//removeBlackPeice
	/**
	 * Removes the black peice.
	 */
	public void removeBlackPeice(){
		blackCount--;
	}
	//removeRedPeice
	/**
	 * Removes the red peice.
	 */
	public void removeRedPeice(){
		redCount--;
	}
	//addBlackPeice
	/**
	 * Adds the black peice.
	 */
	public void addBlackPeice(){
		blackCount++;
	}
	//addRedPeice
	/**
	 * Adds the red peice.
	 */
	public void addRedPeice(){
		redCount++;
	}
	//getBlackCount
	/**
	 * Gets the black count.
	 *
	 * @return the black count
	 */
	public int getBlackCount(){
		return blackCount;
	}
	//getRedCount
	/**
	 * Gets the red count.
	 *
	 * @return the red count
	 */
	public int getRedCount(){
		return redCount;
	}
	
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty(){
		if(blackCount ==0 && redCount ==0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Num of either value.
	 *
	 * @return the int
	 */
	public int numEither(){
		if(blackCount>redCount){
			return blackCount;
		}else{
			return redCount;
		}
	}
	
	public boolean getCol(){
		if(blackCount>redCount){
			return false;
		}else{
			return true;
		}
	}

	public void removePiece(Boolean black) {
		if(black){
			removeBlackPeice();
		}else{
			removeRedPeice();
		}
		
	}

	public void addPiece(Boolean black) {
		if(black){
			addBlackPeice();
		}else{
			addRedPeice();
		}
	}
	
	
}
