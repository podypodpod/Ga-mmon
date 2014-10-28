package Game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BoardJUnitTest {

	Board testSubject;
	
	@Before
	public void setUp() throws Exception {
		
		testSubject = new Board();
	}

	@Test
	public void willReturnABoardInTheStandardBGStartPosition() {
	
		//Given
		//standard BG start is applied
		
		//When
		
			//RED
			Point x1 = testSubject.Points[1];
			Point x12 = testSubject.Points[12];
			Point x17 = testSubject.Points[17];
			Point x19 = testSubject.Points[19];
			//BLACK
			Point x6 = testSubject.Points[6];
			Point x8 = testSubject.Points[8];
			Point x13 = testSubject.Points[13];
			Point x24 = testSubject.Points[24];
		
		//Then
		
			//RED
			assertEquals(2,x1.getRedCount());
			assertEquals(5,x12.getRedCount());
			assertEquals(3,x17.getRedCount());
			assertEquals(5,x19.getRedCount());
			
			//BLACK
			assertEquals(5,x6.getBlackCount());
			assertEquals(3,x8.getBlackCount());
			assertEquals(5,x13.getBlackCount());
			assertEquals(2,x24.getBlackCount());

	}
	
	
	
	@Test
	public void testingCloneBoardConstructor() {
		//Given
		Board newBoard;
		testSubject.blackBore = 10;
		
		//When
		newBoard = new Board(testSubject);
		
		//Then
		assertEquals(10, newBoard.blackBore);
	}
	
	@Test
	public void testingIsThereZeroFunctionWhenSendingInColorBlackTRUEWithDefaultBoard() {
		//When
		Boolean x = testSubject.isthereZero(true);
		
		//Then
		assertEquals(false, x);
	}
	
	@Test
	public void testingIsThereZeroFunctionWhenSendingInColorREDFalseWithDefaultBoard() {
		//When
		Boolean x = testSubject.isthereZero(false);
		
		//Then
		assertEquals(false, x);
	}

	@Test
	public void testingIsThereZeroFunctionWhenSendingInColorBLACKtrueWithAZeroPeice() {
		//Given
		testSubject.Points[25].addBlackPiece();
		
		//When
		Boolean x = testSubject.isthereZero(true);
		
		//Then
		assertEquals(true, x);
	}
	
	@Test
	public void testingIsThereZeroFunctionWhenSendingInColorREDtrueWithAZeroPeice() {
		//Given
		testSubject.Points[0].addRedPiece();
		
		//When
		Boolean x = testSubject.isthereZero(false);
		
		//Then
		assertEquals(true, x);
	}
}
