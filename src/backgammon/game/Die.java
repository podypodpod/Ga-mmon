/**
 * 	GNU General Public License
 * 
 *  This file is part of GA-mmon.
 *  
 *  GA-mmon is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  GA-mmon is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with GA-mmon.  If not, see <http://www.gnu.org/licenses/>.
*/

package backgammon.game;

/**
 * The Class Die.
 * 
 * Simply a dice roll class.
 * 
 * @author David Lomas - 11035527
 */
public class Die {
	
	/** The value. */
	private int value;
	
	/**
	 * Instantiates a new die.
	 */
	public Die(){
		
	}

	/**
	 * Roll die.
	 *
	 * @return the int
	 */
	public int RollDie(){

		value = (int)(Math.random()*6)+1;
		return value;
	}
	
	/**
	 * Gets the die value.
	 *
	 * @return the die value
	 */
	public int getDieValue(){
		return value;
	}
	
	/**
	 * Sets the die value.
	 *
	 * @param x the new die value
	 */
	public void setDieValue(int x){
		value = x;
	}
}