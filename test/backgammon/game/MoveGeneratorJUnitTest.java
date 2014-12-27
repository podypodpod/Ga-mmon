package backgammon.game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import backgammon.game.AIPlayer;
import backgammon.game.Board;
import backgammon.game.MoveGenerator;

public class MoveGeneratorJUnitTest {
	
	Board theBoard;
	AIPlayer p1, p2;
	MoveGenerator mg;
	
	@Before
	public void setUp() throws Exception {

		theBoard = new Board();
		
		p1 = new AIPlayer(true);
		p2 = new AIPlayer(false);
		
		mg = new MoveGenerator();
		
		
	}
	
	
	@Test
	public void testGenMoveActuallyGeneratesBoardsBlack() {
		//Given
		theBoard.setStartPosition();
		p1.movesLeft.add(3);
		p1.movesLeft.add(5);
		
		//When
		mg.generateMoves(theBoard, p1);
		
		//Then
		assertTrue(mg.boardList.hasContents());
	}
	
	@Test
	public void testGenMoveActuallyGeneratesBoardsRed() {
		//Given
		theBoard.setStartPosition();
		p2.movesLeft.add(1);
		
		//When
		mg.generateMoves(theBoard, p2);
		
		//Then
		assertTrue(mg.boardList.hasContents());
	}
	
	@Test
	public void testMoveGenWithNoMoves() {
		//Given
		theBoard.setStartPosition();
		p2.movesLeft.clear();
		
		//When
		mg.generateMoves(theBoard, p2);
		
		//Then
		assertFalse(mg.boardList.hasContents());
	}
}
