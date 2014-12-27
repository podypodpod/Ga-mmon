package backgammon.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import backgammon.game.Point;

public class PointJUnitTest {

	
	private Point testSubject;
	
	@Before
	public void setup(){
		testSubject = new Point();
	}
	
	
	@Test
	public void shouldTakeOnePieceFromBlackCount(){
		//Given
		testSubject.setBlackCount(5);
		
		//When
		testSubject.removeBlackPiece();
		
		//Then
		assertEquals(4, testSubject.getBlackCount());
	}
	
	@Test
	public void shouldTakeOnePieceFromRedCount(){
		//Given
		testSubject.setRedCount(5);
		//When
		testSubject.removeRedPiece();
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
	public void shouldAddOnePieceToBlackCount(){
		//Given
		testSubject.setBlackCount(5);
		
		//When
		testSubject.addBlackPiece();
		
		//Then
		assertEquals(6, testSubject.getBlackCount());
	}
	
	@Test
	public void shouldAddOnePieceToRedCount(){
		//Given
		testSubject.setRedCount(3);
		
		//When
		testSubject.addRedPiece();
		
		//Then
		assertEquals(4, testSubject.getRedCount());
	}
	
	@Test
	public void shouldRemoveOnePieceFromRedCount(){
		//Given
		testSubject.setRedCount(4);
		
		//When
		testSubject.removePiece(false);
		
		//Then
		assertEquals(3, testSubject.getRedCount());
	}
	
	@Test
	public void shouldRemoveOnePieceFromBlackCountWhenTrueIsSentIn(){
		//Given
		testSubject.setBlackCount(4);
		
		//When
		testSubject.removePiece(true);
		
		//Then
		assertEquals(3, testSubject.getBlackCount());
	}
	
	@Test
	public void shouldAddOnePieceToBlackCountWhenTrueIsSentIn(){
		//Given
		testSubject.setBlackCount(4);
		
		//When
		testSubject.addPiece(true);
		
		//Then
		assertEquals(5, testSubject.getBlackCount());
	}
	
	@Test
	public void shouldAddOnePieceToRedCountWhenFalseIsSentIn(){
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
		assertEquals(true, x);
	}
	
	
	@Test
	public void shouldReturnBooleanTrueWhenColRedIsPresent(){
		//Given
		testSubject.setRedCount(1);
		testSubject.setBlackCount(0);
		
		//When
		Boolean x = testSubject.getCol();
		
		//Then
		assertEquals(false, x);
	}
	
	@Test
	public void shouldCloneThePoint(){
		//Given
		testSubject.setRedCount(7);
		testSubject.setBlackCount(0);
		
		Point newPoint;
		
		//When
		newPoint = new Point(testSubject);
		
		//Then
		assertEquals(7, newPoint.getRedCount());
	}
	
	@Test
	public void shouldReturnTrueWhenPassedTwoIdenticalPoints(){
		//Given
		testSubject.setBlackCount(4);

		Point OtherPoint = new Point();
		OtherPoint.setBlackCount(4);
		
		//When
		boolean x = testSubject.equals(OtherPoint);
		
		//Then
		assertTrue(x);
	}
	

	@Test
	public void shouldReturnFalseWhenPassedTwoDifferentPointsDifCol(){
		//Given
		testSubject.setBlackCount(3);

		Point OtherPoint = new Point();
		OtherPoint.setRedCount(4);
		
		//When
		boolean x = testSubject.equals(OtherPoint);
		
		//Then
		assertFalse(x);
	}
	
	@Test
	public void shouldReturnFalseWhenPassedTwoDifferentPointsDifNums(){
		//Given
		testSubject.setBlackCount(3);

		Point OtherPoint = new Point();
		OtherPoint.setBlackCount(4);
		
		//When
		boolean x = testSubject.equals(OtherPoint);
		
		//Then
		assertFalse(x);
	}
}
