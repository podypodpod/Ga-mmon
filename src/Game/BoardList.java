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
		
		//move the piece on this new board
		p1.movepiece(from, to, b);
		if(p1.movesLeft.size()>1){
			p1.movesLeft.remove(Integer.valueOf(p1.distanceBetween(from, to)));
		}else{
			p1.movesLeft.remove(0);
			p1.movesLeft.add(Integer.valueOf(0));
		}

		
		//check if any further moves can be made
		if(p1.movesLeft.size()!=0){
			mg.generateMoves(b, p1);
		}

		
		//add the board to the list
		addBoard(b);
		
		
	}
	
	
	public void removeClones(){

	}
	
	public void chooseBoard(){
		
		Board x = boardList.get(0);
		
		
	}
	
	public void printBoardList(){
		int counter = 0;
		for(Board x: boardList){
			counter++;
			System.out.println("----------------------------");
			System.out.println("Board num "+ counter);
			x.printBoard();
			System.out.println("----------------------------");
		}
	}
}