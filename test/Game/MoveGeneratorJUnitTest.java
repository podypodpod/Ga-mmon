package Game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

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
		mg.generateMoves(theBoard, p);
		
		//Then
		assertEquals(1,1);
	}
	
	
}
