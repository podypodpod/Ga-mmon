package Game;

import static org.junit.Assert.*;

import java.util.ArrayList;

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
		
		p1.movesLeft = new ArrayList<Integer>();
		
		p1.movesLeft.add(p1.die1.getDieValue());
		p1.movesLeft.add(p1.die2.getDieValue());
		
		//When
		
		//Then

		assertTrue(p1.movepiecePoss(1,4,b1));
		
	}
	
	

}
