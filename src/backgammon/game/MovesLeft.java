package backgammon.game;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class MovesLeft.
 * 
 * used for loops checking which moves the player has left to use
 */
public class MovesLeft {

	/** The moves left. */
	public List<Integer> movesLeft;
	
	/**
	 * MovesLeft default constructor
	 * 
	 * Instantiates a new moves left.
	 */
	public MovesLeft(){
		movesLeft = new ArrayList<Integer>();
	}
	
	/**
	 * Clone constructor
	 * 
	 * Instantiates a new moves left.
	 *
	 * @param movesLeftCopy the moves left copy
	 */
	public MovesLeft(MovesLeft movesLeftCopy) {
		
		//create new list
		this.movesLeft = new ArrayList<Integer>();
		
		//copy old one
		for(int x: movesLeftCopy.movesLeft){
			this.movesLeft.add(x);
		}
	}

	/**
	 * Add
	 * adds the param passed in to the list
	 *
	 * @param value the int to be added
	 */
	void add(int value){
		movesLeft.add(Integer.valueOf(value));
	}
	
	/**
	 * Remove
	 * removes the value passed in
	 *
	 * @param value the value to be removed
	 */
	void remove(int value){
		movesLeft.remove(Integer.valueOf(value));
	}
	
	/**
	 * Size.
	 *
	 * @return the size of list
	 */
	int size(){
		return movesLeft.size();
	}
	
	/**
	 * Gets the next.
	 *
	 * @return the next
	 */
	int getNext(){
		return movesLeft.get(0);
	}
	
	/**
	 * Clear.
	 */
	void clear(){
		movesLeft.clear();
	}
	
	/**
	 * Sets the list to the same as the one passed in
	 *
	 * @param set the current list to contents of dicerolls
	 */
	public void setTo(List<Integer> diceRolls){
		clear();
		for(int x:diceRolls){
			add(x);
		}
	}
	
	/* 
	 * toString method
	 */
	public String toString(){
		String str = size()+" moves Left, with Rolls: ";
		
		for(int x = 0; x<size(); x++){
			str+=movesLeft.get(x)+", ";
		}
		
		return str;
		
	}

}
