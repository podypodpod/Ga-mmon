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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GameJUnitTest {
	
	Game game;
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void checkingGameISActive() {
		//Given
		game = new Game(null, null);
		
		//When
		//Then

		assertTrue(game.isGameActive());
	}
}
