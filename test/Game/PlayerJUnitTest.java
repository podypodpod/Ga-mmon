package Game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PlayerJUnitTest {

	Player p1;
	Board b1;
	
	
	@Before
	public void setUp() throws Exception {
		
	//RED PLAYER	
	p1 = new Player(false);
	
	b1 = new Board();
	}
	
	
	@Test
	public void shouldReturnTheDifferenceBetweenThe2NumbersaGTb() {
		
		//Given
		int a = 4;
		int b = 10;
		
		//When
		int c = p1.distanceBetween(a,b);
		
		//Then
		assertEquals(6,c);
		
	}
	
	@Test
	public void shouldReturnTheDifferenceBetweenThe2NumbersbGTa() {
		
		//Given
		int a = 23;
		int b = 5;
		
		//When
		int c = p1.distanceBetween(a,b);
		
		//Then
		assertEquals(18,c);
		
	}

	@Test
	public void ShouldReturnTrueWhenIImputACorrectMove() {
		
		
		//Given
		p1.die1.setDieValue(1);
		p1.die2.setDieValue(3);
		
		p1.movesLeft = new MovesLeft();
		
		p1.movesLeft.add(p1.die1.getDieValue());
		p1.movesLeft.add(p1.die2.getDieValue());
		
		//When
		
		//Then

		assertTrue(p1.movepiecePoss(1,4,b1));
		
	}
	

	@Test
	public void testingMovePeiceFromDefaultBoardMovingToEmptySpace() {
		
		
		//Given
		b1.setStartPosition();
		
		//When
		p1.movepiece(1, 2, b1);
		
		//Then

		assertEquals(1, b1.Points[2].getRedCount());
		
	}
	
	@Test
	public void testingMovePeiceFromBoardToANonEmptySpaceTakingBlack() {
		
		Player p2 = new Player(true);
		
		//Given
		b1.setStartPosition();
		p2.movepiece(6, 2, b1);
		
		//When
		p1.movepiece(1, 2, b1);
		
		//Then

		assertEquals(1, b1.Points[2].getRedCount());
		assertEquals(1, b1.Points[25].getBlackCount());
		
	}

	@Test
	public void testingMovePeiceFromBoardToANonEmptySpaceTakingRed() {
		
		Player p2 = new Player(true);
		
		//Given
		b1.setStartPosition();
		p1.movepiece(1, 2, b1);
		
		//When
		p2.movepiece(6, 2, b1);
		
		//Then

		assertEquals(1, b1.Points[2].getBlackCount());
		assertEquals(1, b1.Points[0].getRedCount());
		
	}
	
}
