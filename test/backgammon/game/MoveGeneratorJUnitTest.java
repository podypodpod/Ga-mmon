package backgammon.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import backgammon.game.AIPlayer;
import backgammon.game.Board;
import backgammon.game.MoveGenerator;

public class MoveGeneratorJUnitTest {
	
	Board theBoard;
	AIPlayer p;
	MoveGenerator mg;
	
	@Before
	public void setUp() throws Exception {

		theBoard = new Board();
		
		p = new AIPlayer(true);
		
		mg = new MoveGenerator();
		
		
	}
	
	
	@Test
	public void x() {
		
		//Given
		//standard board is set up
		
		//When
		
		//Then
		assertEquals(1,1);
	}
	
	
}
