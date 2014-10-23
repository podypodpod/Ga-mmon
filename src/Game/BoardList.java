package Game;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class BoardList.
 */
public class BoardList {
	
	/** The all pos moves. */
	static List<Board> allPosMoves;
	
	/** The current board. */
	Board currentBoard;
	
	/**
	 * Instantiates a new board list.
	 */
	public BoardList(){
		
	}
	
	/**
	 * Sets the current board.
	 *
	 * @param cb the new current board
	 */
	public void setCurrentBoard(Board cb){
		currentBoard = cb;
	}

	
	
	/**
	 * New board.
	 * 
	 * method literally just moves a peice from x to y, as the validity of the move has already been checked
	 * it then creates a new board and adds in the the list of allPosMoves
	 *
	 * @param x the x peice
	 * @param y the y peice
	 */
	public void newBoard(int x, int y){
		Board nb = new Board(currentBoard);
		nb.Points[x].removeBlackPeice();
		nb.Points[y].addRedPeice();
		allPosMoves.add(nb);
	}

}