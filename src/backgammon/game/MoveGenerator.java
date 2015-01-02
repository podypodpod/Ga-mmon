package backgammon.game;

import backgammon.genes.Individual;

/**
 * The Class MoveGenerator.
 */
public class MoveGenerator {
	
	/** The board list. */
	BoardList boardList;
	
	Individual playerPersonality;
	
	/**
	 * Instantiates a new move generator.
	 */
	public MoveGenerator(Individual pp){
		playerPersonality = pp;
		boardList = new BoardList(playerPersonality);
	}
	
	/**
	 * Gets the next move board.
	 *
	 * @param cb the current board
	 * @param p the player
	 * @return the next move board
	 */
	public Board getNextMoveBoard(Board cb, AIPlayer p){
		
		//Clear the list
		boardList.clearList();
		
		//generate all possible moves
		generateMoves(cb, p);
		
		//select the best board and return it, if no possible next moves it will return null and carry on using the same board
		return boardList.selectBoard(cb, p);
	}

	/**
	 * Generate all possible moves.
	 *
	 * @param cb the current board
	 * @param p the player
	 */
	public void generateMoves(Board cb, AIPlayer p){
		
		//Loop all moves
		for(int currentMove: p.movesLeft.movesLeft){
			
			//check the current move doesn't = 0, this would mean it has been removed and a new board has been created
			if(currentMove!= 0){
				
				//loop all points
				for(int point = 0; point < cb.Points.length; point++){
						
					if(p.black){

						//if the current point can move the current move, create new board of it
						if(p.movePiecePoss(point, point-currentMove, cb)){
							
							//adding the new board to the list
							boardList.addBoard(new Board(cb), (point-currentMove), point, new AIPlayer(p), this);
		
						}
					}else if(!p.black){
						
					}
						//if the current point can move the current move, create new board of it
						if(p.movePiecePoss(point, point+currentMove, cb)){
						
							//adding the new board to the list
							boardList.addBoard(new Board(cb), (point+currentMove), point, new AIPlayer(p), this);
					}
				}
			}else{
				break;
			}
		}
	}
}