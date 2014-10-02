
// TODO: Auto-generated Javadoc
/**
 * The Class Point.
 */
public class Point {

	/** The black count. */
	int blackCount;
	
	/** The red count. */
	int redCount;
	
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
}
