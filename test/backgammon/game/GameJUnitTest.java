package backgammon.game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GameJUnitTest {
	
	Game game;
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void checkingGameISActive() {
		//Given
		game = new Game();
		
		//When
		//Then

		assertTrue(game.gameActive);
	}
}
