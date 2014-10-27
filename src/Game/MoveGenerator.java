package Game;

import java.util.List;

public class MoveGenerator {
	
	Board currentBoard;
	
	public MoveGenerator(){
		
	}
	
	public void setBoard(Board cb){
		currentBoard = cb;
	}

	public void generateMoves(Board cb, List<Integer> movesLeft, AIPlayer p) {
		setBoard(cb);
		
		for(int currentMove: movesLeft){
			for(int point = 0; point < cb.Points.length; point++){
				if(p.movepiecePoss(point, point+currentMove, cb)){
					System.out.println("new Board!! " + point + " to " + currentMove);
				}
			}
		}
	}

}
