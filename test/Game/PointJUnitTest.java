package Game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PointJUnitTest {

	
	private Point testSubject;
	
	@Before
	public void setup(){
		testSubject = new Point();
	}
	
	
	@Test
	public void shouldTakeOnePeiceFromBlackCount(){
		//Given
		testSubject.setBlackCount(5);
		
		//When
		testSubject.removeBlackPeice();
		
		//Then
		assertEquals(4, testSubject.getBlackCount());
	}
	
	@Test
	public void shouldTakeOnePeiceFromRedCount(){
		//Given
		testSubject.setRedCount(5);
		//When
		testSubject.removeRedPeice();
		//Then
		assertEquals(4,testSubject.getRedCount());
	}
	
	@Test
	public void shouldReturnTrueWhenBothRedAndBlackAreEmpty(){
		//When
		boolean b = testSubject.isEmpty();
		
		//Then
		assertTrue(b);
	}
	
	@Test
	public void shouldReturnFalseWhenBothRedAndBlackAreNotEmpty(){
		
		//Given
		testSubject.setBlackCount(3);
		testSubject.setRedCount(45);
		
		
		//When
		boolean b = testSubject.isEmpty();
		
		//Then
		assertFalse(b);
	}
	
	@Test
	public void shouldReturnAValueOfBlackCountIfGreaterThanRed(){
		
		//Given
		testSubject.setBlackCount(3);
		testSubject.setRedCount(0);
		
		
		//When
		int bbc = testSubject.numEither();
		
		//Then
		assertEquals(3,bbc);
	}
	
}
