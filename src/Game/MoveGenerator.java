package Game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MoveGenerator {
	
	Board currentBoard;
	BoardList BoardList;
	
	public MoveGenerator(){
		
		BoardList = new BoardList();
		
	}
	
	public void setBoard(Board cb){
		currentBoard = cb;
	}

	public void generateMoves(Board cb, AIPlayer p){
		setBoard(cb);
		
		//Iterator<Integer> moves = p.movesLeft.iterator();
		
		//Loop all moves
		
		
		for(int currentMove: p.movesLeft){
		//while(moves.hasNext()){
			//int currentMove = moves.next();
			
			if(currentMove!= 0){
				
				System.out.println("Move: "+currentMove);
				
				//loop all points
				for(int point = 0; point < cb.Points.length; point++){
					
					System.out.println(point);
					
					//if the current point can move the current move, create new board of it
					if(p.movepiecePoss(point, point-currentMove, cb)){
						
						System.out.println("-New Board created: " + point + " to " + (point-currentMove) + ", Using move "+currentMove+". "+((p.movesLeft.size())-1)+" further moves left.");
						
						//adding the new board to the list
						BoardList.addBoard(cb, (point-currentMove), point, new AIPlayer(p), this);
	
					}
				}
			}else{
				break;
			}
		}
	}

}
