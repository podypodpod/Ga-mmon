package Game;

import java.util.ArrayList;
import java.util.List;

public class DiceList {
	
	private List<Integer> movesLeft;
	Die d1,d2;
	
	public DiceList(){
		movesLeft = new ArrayList<Integer>();
		
		d1 = new Die();
		d2 = new Die();
	}
	
	public void RollDice(){
		
		//roll dice
		movesLeft.add(d1.RollDie());
		movesLeft.add(d2.RollDie());
		
		//add 2 extra if there is a double roll
		if(movesLeft.get(0) == movesLeft.get(1)){
			movesLeft.add(Integer.valueOf(movesLeft.get(0)));
		}
	}
	
	public void remove(int index){
		movesLeft.remove(index);
	}
	
	public void remove(Object o){
		movesLeft.remove(o);
	}

}
