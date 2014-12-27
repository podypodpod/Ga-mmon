package backgammon.game;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class DiceList.
 * 
 * Container for the Dice
 */
public class DiceList {
	
	/** The dice. */
	Die d1,d2;
	
	/**
	 * Instantiates a new dice list.
	 */
	public DiceList(){		
		d1 = new Die();
		d2 = new Die();
	}
	
	/**
	 * Roll dice.
	 * Rolls two random dice and returns the results
	 * 
	 * @return list of Dice rolls
	 */
	public List<Integer> RollDice(){
		
		List<Integer> diceRolls = new ArrayList<Integer>();
		
		//roll dice
		diceRolls.add(d1.RollDie());
		diceRolls.add(d2.RollDie());
		
		//add 2 extra if there is a double roll
		if(diceRolls.get(0) == diceRolls.get(1)){
			diceRolls.add(Integer.valueOf(diceRolls.get(0)));
			diceRolls.add(Integer.valueOf(diceRolls.get(0)));
		}
		return diceRolls;
	}
}