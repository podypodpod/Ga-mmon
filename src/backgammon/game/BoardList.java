package backgammon.game;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class BoardList.
 */
public class BoardList {
	
	/** The all possible moves. */
	private List<Board> boardList;

	/**
	 * BoardList
	 * 
	 * Instantiates a new board list.
	 */
	public BoardList(){
		boardList = new ArrayList<Board>();
	}
	
	/**
	 * clearList
	 * 
	 * Emptys the list.
	 */
	public void clearList(){
		boardList.clear();
	}
	
	/**
	 * addBoard
	 * Adds the board passed in.
	 *
	 * @param b the board
	 */
	public void addBoard(Board b){
		boardList.add(b);
	}
	
	/**
	 * addBoard
	 * adds the board passed in with the move also passed in applied to it first.
	 * It then checks if any more moves can be made
	 *
	 * @param b the board passed in
	 * @param to the destination of move
	 * @param from the start of the move
	 * @param p1 the player object
	 * @param mg the move generator
	 */
	public void addBoard(Board b, int to, int from, AIPlayer p1, MoveGenerator mg){
		
		//move the piece on this new board
		p1.movePiece(from, to, b);
		
		if(p1.movesLeft.size()>1){
			p1.movesLeft.movesLeft.remove(Integer.valueOf(p1.distanceBetween(from, to)));
		}else{
			p1.movesLeft.movesLeft.remove(0);
			p1.movesLeft.movesLeft.add(Integer.valueOf(0));
		}
		
		//check if any further moves can be made
		if(!((p1.movesLeft.size()==1 && p1.movesLeft.getNext()==0)||(p1.movesLeft.size()==0))){
			mg.generateMoves(b, p1);
		}

		//add the board to the list if there is not a duplicate
		if(!(thereIsADuplicate(b))){
			addBoard(b);
		}
	}
	
	
	/**
	 * There is a duplicate.
	 * checks if there is already a duplicate of the board passed in already present in the list
	 *
	 * @param b the board it is checking against
	 * @return true, if there is a duplicate
	 */
	public boolean thereIsADuplicate(Board b){
		for(Board x: boardList){
			if(x.equals(b)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Select board.
	 * This will select the best board from the list for the player to use
	 *
	 * @return the board that has been selected
	 */
	public Board selectBoard(){
		
		//create a new board
		Board chosenBoard;
		
		if(boardList.size()!=0){
			
			//rate them all on the the play style etc
			rankBoards();
			
			//pass them to the GA to decide which it will use
			//
			//currently random
			int x = (int)(Math.random()*(boardList.size()));

			System.out.println("board list size: "+boardList.size());
			
			chosenBoard = boardList.get(x);
			return chosenBoard;
		}else
		{
			//if there are no possible new boards(no possible moves) return null
			return null;
		}
		
	}
	
	/**
	 * Prints the board list.
	 */
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
	
	/**
	 * Checks for contents.
	 *
	 * @return true, if successful
	 */
	public boolean hasContents(){
		if(boardList.size()>0){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Rank Boards.
	 * 
	 * Method will order them to allow the GA to later decide which it picks
	 */
	public void rankBoards() {
		
		
	}
}