package backgammon.game;


public class MoveGenerator {
	
	BoardList boardList;
	
	public MoveGenerator(){
		
		boardList = new BoardList();
		
	}
	
	public void setBoard(){
	}
	
	public Board getNextMoveBoard(Board cb, AIPlayer p){
		
		//Clear the list
		boardList.clearList();
		
		//generate all possible moves
		generateMoves(cb, p);
		
		//select the best board and return it, if no possible next moves it will return null and carry on using the same board
		return boardList.selectBoard();
	}

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
