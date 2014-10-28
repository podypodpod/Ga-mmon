package Game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MovesLeftJUnitTest {

	MovesLeft testSubject;
	
	@Before
	public void setUp() throws Exception {
		
		testSubject = new MovesLeft();
	}
	
	@Test
	public void testingMovesLeftAddIntFucntion() {
		//Given
		
		
		//When
		testSubject.add(5);
		
		//Then
		assertEquals(5,testSubject.getNext());
		
	}
	
	@Test
	public void testingCloneConstructor() {
		//Given
		this.testSubject.add(4);
		this.testSubject.add(9);
		this.testSubject.add(3);
		
		MovesLeft newMovesLeft;
		
		//When
		newMovesLeft = new MovesLeft(testSubject);
		
		//Then
		assertEquals(4, newMovesLeft.getNext());
		
	}
	
	
	@Test
	public void testingSizeMethod() {
		//Given
		this.testSubject.add(4);
		this.testSubject.add(9);
		this.testSubject.add(3);
		
		
		//When
		int x = testSubject.size();
		
		//Then
		assertEquals(3, x);
		
	}
	
	@Test
	public void testingRemoveMethodRemovingAnItemFromTheArray() {
		
		//Given
		this.testSubject.add(4);
		this.testSubject.add(9);	
		
		//When
		testSubject.remove(4);
		int x = testSubject.getNext();
		
		//Then
		assertEquals(9, x);
		
	}
	
	
	
}