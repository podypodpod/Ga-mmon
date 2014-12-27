package backgammon.game;

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
	 * default constructor
	 * Instantiates a new point.
	 */
	public Point(){
		blackCount = 0;
		redCount = 0;
	}
	
	/**
	 * clone constructor
	 * Instantiates a new point.
	 *
	 * @param p the point to copy
	 */
	public Point(Point p){
		this.blackCount = p.blackCount;
		this.redCount = p.redCount;
	}
	
	/**
	 * Sets the black count.
	 *
	 * @param bc the new black count
	 */
	public void setBlackCount(int bc){
		blackCount = bc;
	}
	
	/**
	 * Sets the red count.
	 *
	 * @param rc the new red count
	 */
	public void setRedCount(int rc){
		redCount = rc;
	}

	/**
	 * Removes the black piece.
	 */
	public void removeBlackPiece(){
		blackCount--;
	}

	/**
	 * Removes the red piece.
	 */
	public void removeRedPiece(){
		redCount--;
	}

	/**
	 * Adds the black piece.
	 */
	public void addBlackPiece(){
		blackCount++;
	}

	/**
	 * Adds the red piece.
	 */
	public void addRedPiece(){
		redCount++;
	}
	
	/**
	 * Gets the black count.
	 *
	 * @return the black count
	 */
	public int getBlackCount(){
		return blackCount;
	}
	
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
	
	/**
	 * Gets the col.
	 *
	 * @return the col
	 */
	public boolean getCol(){
		if(blackCount>redCount){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Removes the piece.
	 *
	 * @param black the black
	 */
	public void removePiece(Boolean black) {
		if(black){
			removeBlackPiece();
		}else{
			removeRedPiece();
		}
		
	}

	/**
	 * Adds the piece.
	 *
	 * @param black the black
	 */
	public void addPiece(Boolean black) {
		if(black){
			addBlackPiece();
		}else{
			addRedPiece();
		}
	}
	
	/**
	 * Equals.
	 *
	 * @param p the point
	 * @return true, if successful
	 */
	public boolean equals(Point p){
		boolean theSame = true;
		
		if(this.redCount != p.redCount){
			theSame=false;
		}else if(this.blackCount != p.blackCount){
			theSame=false;
		}
		
		return theSame;
	}
	
	
}
