package Game;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class BoardList.
 */
public class BoardList {
	
	/** The all pos moves. */
	List<Board> allPosBoards;

	/**
	 * Instantiates a new board list.
	 */
	public BoardList(){
		
		allPosBoards = new ArrayList<Board>();
		
	}
	
	public void addBoard(Board b){
		allPosBoards.add(b);
		
	}
	


}