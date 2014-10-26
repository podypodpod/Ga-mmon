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
		int x = testSubject.numEither();
		
		//Then
		assertEquals(3,x);
	}
	
	@Test
	public void shouldReturnAValueOfRedCountIfGreaterThanBlac(){
		
		//Given
		testSubject.setBlackCount(0);
		testSubject.setRedCount(5);
		
		
		//When
		int x = testSubject.numEither();
		
		//Then
		assertEquals(5,x);
	}
	
	@Test
	public void shouldAddOnePeiceToBlackCount(){
		//Given
		testSubject.setBlackCount(5);
		
		//When
		testSubject.addBlackPeice();
		
		//Then
		assertEquals(6, testSubject.getBlackCount());
	}
	
	@Test
	public void shouldAddOnePeiceToRedCount(){
		//Given
		testSubject.setRedCount(3);
		
		//When
		testSubject.addRedPeice();
		
		//Then
		assertEquals(4, testSubject.getRedCount());
	}
	
	@Test
	public void shouldRemoveOnePeiceFromRedCount(){
		//Given
		testSubject.setRedCount(4);
		
		//When
		testSubject.removePiece(false);
		
		//Then
		assertEquals(3, testSubject.getRedCount());
	}
	
	@Test
	public void shouldRemoveOnePeiceFromBlackCountWhenTrueIsSentIn(){
		//Given
		testSubject.setBlackCount(4);
		
		//When
		testSubject.removePiece(true);
		
		//Then
		assertEquals(3, testSubject.getBlackCount());
	}
	
	@Test
	public void shouldAddOnePeiceToBlackCountWhenTrueIsSentIn(){
		//Given
		testSubject.setBlackCount(4);
		
		//When
		testSubject.addPiece(true);
		
		//Then
		assertEquals(5, testSubject.getBlackCount());
	}
	
	@Test
	public void shouldAddOnePeiceToRedCountWhenFalseIsSentIn(){
		//Given
		testSubject.setRedCount(4);
		
		//When
		testSubject.addPiece(false);
		
		//Then
		assertEquals(5, testSubject.getRedCount());
	}
	
	
	@Test
	public void shouldReturnBooleanFalseWhenColBlackIsPresent(){
		//Given
		testSubject.setRedCount(0);
		testSubject.setBlackCount(1);
		
		//When
		Boolean x = testSubject.getCol();
		
		//Then
		assertEquals(false, x);
	}
	
	
	@Test
	public void shouldReturnBooleanTrueWhenColRedIsPresent(){
		//Given
		testSubject.setRedCount(1);
		testSubject.setBlackCount(0);
		
		//When
		Boolean x = testSubject.getCol();
		
		//Then
		assertEquals(true, x);
	}
	
}
