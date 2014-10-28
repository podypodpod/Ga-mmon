package Game;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class BoardList.
 */
public class BoardList {
	
	/** The all pos moves. */
	List<Board> boardList;

	/**
	 * Instantiates a new board list.
	 */
	public BoardList(){
		
		boardList = new ArrayList<Board>();
		
	}
	
	public void addBoard(Board b){
		boardList.add(b);
	}
	
	public void addBoard(Board b, int to, int from, AIPlayer p1, MoveGenerator mg){
		
		//create a copy of the original board and player
		Board bClone = new Board(b);
		//AIPlayer p1Clone = new AIPlayer(p1);
		
		//move the piece on this new board
		p1.movepiece(from, to, bClone);
		if(p1.movesLeft.size()>1){
			p1.movesLeft.remove(Integer.valueOf(p1.distanceBetween(from, to)));
		}else{
			p1.movesLeft.remove(0);
			p1.movesLeft.add(Integer.valueOf(0));
		}

		
		//check if any further moves can be made
		if(p1.movesLeft.size()!=0){
			mg.generateMoves(bClone, p1);
		}

		
		//add the board to the list
		addBoard(bClone);
		
		
	}


}