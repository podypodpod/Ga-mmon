package Game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DieJUnitTest {
	
	Die d1;

	@Before
	public void setUp() throws Exception {
		d1 = new Die();
	}

	@Test
	public void RollingShouldProduceAnIntBetween1And6() {
		//Given
		int x = d1.RollDie();
		boolean y;
		
		//When
		//Then
		
		if(x>0.99 && x<6.01){
			y = true;
		}else{
			y=false;
		}
		assertTrue(y);
	}

}
