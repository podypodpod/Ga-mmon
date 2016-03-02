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

import backgammon.game.AIPlayer;
import backgammon.game.Board;
import backgammon.game.MoveGenerator;

public class MoveGeneratorJUnitTest {
	
	Board theBoard;
	AIPlayer p1, p2;
	MoveGenerator mg;
	
	@Before
	public void setUp() throws Exception {

		theBoard = new Board();
		
		p1 = new AIPlayer(true, null);
		p2 = new AIPlayer(false, null);
		
		mg = new MoveGenerator(null);
		
		
	}
	
	
	@Test
	public void testGenMoveActuallyGeneratesBoardsBlack() {
		//Given
		theBoard.setStartPosition();
		p1.movesLeft.add(3);
		p1.movesLeft.add(5);
		
		//When
		mg.generateMoves(theBoard, p1);
		
		//Then
		assertTrue(mg.boardList.hasContents());
	}
	
	@Test
	public void testGenMoveActuallyGeneratesBoardsRed() {
		//Given
		theBoard.setStartPosition();
		p2.movesLeft.add(1);
		
		//When
		mg.generateMoves(theBoard, p2);
		
		//Then
		assertTrue(mg.boardList.hasContents());
	}
	
	@Test
	public void testMoveGenWithNoMoves() {
		//Given
		theBoard.setStartPosition();
		p2.movesLeft.clear();
		
		//When
		mg.generateMoves(theBoard, p2);
		
		//Then
		assertFalse(mg.boardList.hasContents());
	}
}
