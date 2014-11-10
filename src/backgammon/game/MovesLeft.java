package backgammon.game;

import java.util.ArrayList;
import java.util.List;

public class MovesLeft {

	public List<Integer> movesLeft;
	
	public MovesLeft(){
		
		movesLeft = new ArrayList<Integer>();
		
	}
	
	public MovesLeft(MovesLeft movesLeftCopy) {
		
		this.movesLeft = new ArrayList<Integer>();
		
		for(int x: movesLeftCopy.movesLeft){
			this.movesLeft.add(x);
		}
		
	}

	void add(int value){
		movesLeft.add(Integer.valueOf(value));
	}
	
	void remove(int value){
		movesLeft.remove(Integer.valueOf(value));
	}
	
	int size(){
		return movesLeft.size();
	}
	
	int getNext(){
		return movesLeft.get(0);
	}
	
	void clear(){
		movesLeft.clear();
	}

}
