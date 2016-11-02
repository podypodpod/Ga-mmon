/**
 * GNU General Public License
 *
 * This file is part of GA-mmon.
 *
 * GA-mmon is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.test.board;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.co.davidlomas.gammon.game.AIPlayer;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.MoveGenerator;

public class MoveGeneratorJUnitTest {

	Board board;
	AIPlayer player1, player2;
	MoveGenerator moveGenerator;

	@Before
	public void setUp() throws Exception {

		board = new Board();

		player1 = new AIPlayer(true, null);
		player2 = new AIPlayer(false, null);

		moveGenerator = new MoveGenerator(null);
	}

	@Test
	public void testGenMoveActuallyGeneratesBoardsBlack() {
		// Given
		board.setStartPosition();
		player1.movesLeft.add(3);
		player1.movesLeft.add(5);

		// When
		moveGenerator.generateMoves(board, player1);

		// Then
		assertTrue(moveGenerator.getBoardList().hasContents());
	}

	@Test
	public void testGenMoveActuallyGeneratesBoardsRed() {
		// Given
		board.setStartPosition();
		player2.movesLeft.add(1);

		// When
		moveGenerator.generateMoves(board, player2);

		// Then
		assertTrue(moveGenerator.getBoardList().hasContents());
	}

	@Test
	public void testMoveGenWithNoMoves() {
		// Given
		board.setStartPosition();
		player2.movesLeft.clear();

		// When
		moveGenerator.generateMoves(board, player2);

		// Then
		assertFalse(moveGenerator.getBoardList().hasContents());
	}
}
