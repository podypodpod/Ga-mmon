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
		
		//select the best move and return it
		return boardList.selectBoard();
	}

	public void generateMoves(Board cb, AIPlayer p){
		
		//Loop all moves
		for(int currentMove: p.movesLeft.movesLeft){
			
			//check the current move doesnt = 0, this would mean it has been removed and a new board has been created
			if(currentMove!= 0){
				
				//System.out.println("Move: "+currentMove);
				
				//loop all points
				for(int point = 0; point < cb.Points.length; point++){
					
					//System.out.println("P"+point + ", MovesLeft: " + p.movesLeft.movesLeft.size() + "current Move: "+ currentMove);
					
					//if the current point can move the current move, create new board of it
					if(p.movepiecePoss(point, point-currentMove, cb)){
						
						//System.out.println("-New Board created: " + point + " to " + (point-currentMove) + ", Using move "+currentMove+". "+((p.movesLeft.size())-1)+" further moves left.");
						
						//adding the new board to the list
						boardList.addBoard(new Board(cb), (point-currentMove), point, new AIPlayer(p), this);
	
					}
				}
			}else{
				break;
			}
		}
	}

}
