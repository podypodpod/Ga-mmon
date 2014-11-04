package Game;

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
	
	public Point(Point p){
		this.blackCount = p.blackCount;
		this.redCount = p.redCount;
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
	//removeBlackpiece
	/**
	 * Removes the black piece.
	 */
	public void removeBlackPiece(){
		blackCount--;
	}
	//removeRedpiece
	/**
	 * Removes the red piece.
	 */
	public void removeRedPiece(){
		redCount--;
	}
	//addBlackpiece
	/**
	 * Adds the black piece.
	 */
	public void addBlackPiece(){
		blackCount++;
	}
	//addRedpiece
	/**
	 * Adds the red piece.
	 */
	public void addRedPiece(){
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
			return true;
		}else{
			return false;
		}
	}

	public void removePiece(Boolean black) {
		if(black){
			removeBlackPiece();
		}else{
			removeRedPiece();
		}
		
	}

	public void addPiece(Boolean black) {
		if(black){
			addBlackPiece();
		}else{
			addRedPiece();
		}
	}
	
	
}
