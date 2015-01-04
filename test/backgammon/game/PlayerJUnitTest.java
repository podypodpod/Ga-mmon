package backgammon.game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import backgammon.game.Board;
import backgammon.game.Player;

public class PlayerJUnitTest {

	Player p1,p2;
	Board b1;
	
	
	@Before
	public void setUp() throws Exception {
		
		p1 = new Player(false);
		p2 = new Player(true);
		
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
	public void ShouldReturnTrueWhenIInputACorrectMoveASingleDiceRoll() {	
		
		//Given
		p1.movesLeft.add(1);
		p1.movesLeft.add(2);
		
		//When
		//Then

		assertTrue(p1.movePiecePoss(1,3,b1));
	}
	
	
	/*
	 * TODO: Should Return True When I Input A Correct Move Using Both Dice Rolls, does not
	 * 
	 * In theory the game should let you do this, but at the moment it does not if I have time adjust this
	 * 
	 * but not a big deal as technically it is 2 moves anyway, just done in one click normally
	 * 
	 * and when using an AI player it will auto do this, when using manual players it will be 2 moves anyway so really this will never be used
	 */
	@Test
	public void ShouldReturnTrueWhenIInputACorrectMoveUsingBothDiceRolls() {	
		//Given
		p1.movesLeft.add(1);
		p1.movesLeft.add(2);
		
		//When
		//Then

		assertTrue(p1.movePiecePoss(1,4,b1));
	}

	@Test
	public void testingMovePeiceFromDefaultBoardMovingToEmptySpace() {
		
		
		//Given
		b1.setStartPosition();
		
		//When
		p1.movePiece(1, 2, b1);
		
		//Then

		assertEquals(1, b1.Points[2].getRedCount());
		
	}
	
	@Test
	public void testingMovePeiceFromBoardToANonEmptySpaceTakingBlack() {
		
		//Given
		b1.setStartPosition();
		p2.movePiece(6, 2, b1);
		
		//When
		p1.movePiece(1, 2, b1);
		
		//Then

		assertEquals(1, b1.Points[2].getRedCount());
		assertEquals(1, b1.Points[25].getBlackCount());
		
	}

	@Test
	public void testingMovePeiceFromBoardToANonEmptySpaceTakingRed() {
		
		//Given
		b1.setStartPosition();
		p1.movePiece(1, 2, b1);
		
		//When
		p2.movePiece(6, 2, b1);
		
		//Then

		assertEquals(1, b1.Points[2].getBlackCount());
		assertEquals(1, b1.Points[0].getRedCount());
		
	}
	
	
	@Test
	public void testingMovePeiceFromRed0PeiceBackOnBoard() {
		
		//Given
		b1.setStartPosition();
		p1.movePiece(19, 0, b1);
		p1.movesLeft.movesLeft.add(2);
		
		//When
		boolean possible = p1.movePiecePoss(0, 2, b1);
		
		//Then
		assertTrue(possible);
		
	}
	
	
	@Test
	public void testingMovePeiceOnBoardWhenThereIsAZeroIIgnore() {
		
		//Given
		b1.setStartPosition();
		p1.movePiece(19, 0, b1);
		p1.movesLeft.movesLeft.add(2);
		
		//When
		boolean possible = p1.movePiecePoss(19, 21, b1);
		
		//Then
		assertFalse(possible);
		
	}
	
	@Test
	public void testingMovePeicPossonBoardWhenTakingAPeice() {
		
		//p2 is black
		
		//Given
		b1.setStartPosition();
		//leaving a peice on its own
		p1.movePiece(12, 11, b1);
		
		p2.movesLeft.movesLeft.add(2);
		
		//When
		boolean possible = p2.movePiecePoss(13, 11, b1);
		
		//Then
		assertTrue(possible);
	}
	
	public void testingMovePeicPossonBoardWhenTakingAPeiceButWithAZero() {
		
		//p2 is black
		
		//Given
		b1.setStartPosition();
		//leaving a peice on its own
		p1.movePiece(12, 11, b1);
		p2.movePiece(13, 25, b1);
		
		p2.movesLeft.movesLeft.add(2);
		
		//When
		boolean possible = p2.movePiecePoss(13, 11, b1);
		
		//Then
		assertFalse(possible);
	}
	
	
	@Test
	public void testingMovePeicPossonBoardWhenTakingAPeiceThatHasMultple() {
		
		//p2 is black
		
		//Given
		b1.setStartPosition();

		p1.movePiece(12, 11, b1);
		p1.movePiece(12, 11, b1);
		
		p2.movesLeft.movesLeft.add(2);
		
		//When
		boolean possible = p2.movePiecePoss(13, 11, b1);
		
		//Then
		assertFalse(possible);
	}
}
