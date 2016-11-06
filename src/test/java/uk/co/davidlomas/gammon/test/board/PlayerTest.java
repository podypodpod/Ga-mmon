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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.Player;

public class PlayerTest {

	Player p1, p2;
	Board b1;

	@Test
	public void boardWhenTakingAPeice_candidateMovePossible() {

		// p2 is black

		// Given
		b1.setStartPosition();

		// leaving a red peice on its own
		p1.movePiece(12, 11, b1);

		p2.movesLeft.movesLeft.add(2);

		// When
		final boolean possible = p2.candidateMovePossible(13, 11, b1);

		// Then
		assertTrue(possible);
	}

	@Test
	public void doesntHavePeiceAtStart_shouldFail_passesBasicChecks() {
		// given
		final int to = 7;
		final int from = 5;
		b1.setStartPosition();
		p1.movesLeft.add(2);

		// then
		Assert.assertFalse(p1.passesBasicChecks(to, from, b1));
	}

	@Test
	public void fromBoardToANonEmptySpaceTakingBlack_movePiece() {

		// Given
		b1.setStartPosition();
		p2.movePiece(6, 2, b1);

		// When
		p1.movePiece(1, 2, b1);

		// Then

		assertEquals(1, b1.Points[2].getRedCount());
		assertEquals(1, b1.Points[25].getBlackCount());
	}

	@Test
	public void fromBoardToANonEmptySpaceTakingRed_movePiece() {

		// Given
		b1.setStartPosition();
		p1.movePiece(1, 2, b1);

		// When
		p2.movePiece(6, 2, b1);

		// Then

		assertEquals(1, b1.Points[2].getBlackCount());
		assertEquals(1, b1.Points[0].getRedCount());

	}

	@Test
	public void fromDefaultBoardMovingToEmptySpace_movePiece() {

		// Given
		b1.setStartPosition();

		// When
		p1.movePiece(1, 2, b1);

		// Then
		assertEquals(1, b1.Points[2].getRedCount());
	}

	@Test
	public void fromRed0PeiceBackOnBoard_movePiece() {

		// Given
		b1.setStartPosition();
		p1.movePiece(19, 0, b1);
		p1.movesLeft.movesLeft.add(2);

		// When
		final boolean possible = p1.candidateMovePossible(0, 2, b1);

		// Then
		assertTrue(possible);

	}

	@Test
	public void movingWrongDirection_shouldFail_passesBasicChecks() {
		// given
		final int to = 10;
		final int from = 12;
		b1.setStartPosition();
		p1.movesLeft.add(2);

		// then
		Assert.assertFalse(p1.passesBasicChecks(to, from, b1));
	}

	public void onBoardWhenTakingAPeiceButWithAZero_candidateMovePossible() {

		// p2 is black

		// Given
		b1.setStartPosition();
		// leaving a peice on its own
		p1.movePiece(12, 11, b1);
		p2.movePiece(13, 25, b1);

		p2.movesLeft.movesLeft.add(2);

		// When
		final boolean possible = p2.candidateMovePossible(13, 11, b1);

		// Then
		assertFalse(possible);
	}

	@Test
	public void onBoardWhenTakingAPeiceWithTwoOpponentsOn_candidateMovePossible() {

		// p2 is black

		// Given
		b1.setStartPosition();

		// putting 2 red peices on their own
		p1.movePiece(12, 11, b1);
		p1.movePiece(12, 11, b1);

		p2.movesLeft.movesLeft.add(2);

		// When
		final boolean possible = p2.candidateMovePossible(13, 11, b1);

		// Then
		assertFalse(possible);
	}

	@Test
	public void onBoardWhenThereIsAZeroIIgnore_movePiece() {

		// Given
		b1.setStartPosition();
		p1.movePiece(19, 0, b1);
		p1.movesLeft.movesLeft.add(2);

		// When
		final boolean possible = p1.candidateMovePossible(19, 21, b1);

		// Then
		assertFalse(possible);

	}

	@Test
	public void outOfArrayBoundsFrom_shouldFail_passesBasicChecks() {
		// given
		final int to = 3;
		final int from = -5;
		b1.setStartPosition();
		p1.movesLeft.add(2);

		// then
		Assert.assertFalse(p1.passesBasicChecks(to, from, b1));
	}

	@Test
	public void outOfArrayBoundsTo_shouldFail_passesBasicChecks() {
		// given
		final int to = 27;
		final int from = 1;
		b1.setStartPosition();
		p1.movesLeft.add(2);

		// then
		Assert.assertFalse(p1.passesBasicChecks(to, from, b1));
	}

	@Before
	public void setUp() throws Exception {
		p1 = new Player(false);
		p2 = new Player(true);
		b1 = new Board();
	}

	@Test
	public void shouldPass_passesBasicChecks() {
		// given
		final int to = 3;
		final int from = 1;
		b1.setStartPosition();
		p1.movesLeft.add(2);

		// then
		Assert.assertTrue(p1.passesBasicChecks(to, from, b1));
	}

	@Test
	public void shouldReturnTheDifferenceBetweenThe2NumbersAgtB_distanceBetween() {

		// Given
		final int a = 4;
		final int b = 10;

		// When
		final int c = p1.distanceBetween(a, b);

		// Then
		assertEquals(6, c);
	}

	@Test
	public void shouldReturnTheDifferenceBetweenThe2NumbersBgtA_distanceBetween() {

		// Given
		final int a = 23;
		final int b = 5;

		// When
		final int c = p1.distanceBetween(a, b);

		// Then
		assertEquals(18, c);
	}

	@Test
	public void shouldReturnTrueWhenIInputACorrectMoveASingleDiceRoll_distanceBetween() {

		// Given
		p1.movesLeft.add(1);
		p1.movesLeft.add(2);

		// When
		// Then

		assertTrue(p1.candidateMovePossible(1, 3, b1));
		assertTrue(p1.candidateMovePossible(1, 2, b1));
	}

	@Test
	public void thereIsZero_shouldFail_passesBasicChecks() {
		// given
		final int to = 3;
		final int from = 1;
		b1.setStartPosition();
		p1.movesLeft.add(2);
		b1.Points[0].addRedPiece();

		// then
		Assert.assertFalse(p1.passesBasicChecks(to, from, b1));
	}

	@Test
	public void thereIsZeroOnOtherPlayer_shouldPass_passesBasicChecks() {
		// given
		final int to = 3;
		final int from = 1;
		b1.setStartPosition();
		p1.movesLeft.add(2);
		b1.Points[25].addBlackPiece();

		// then
		Assert.assertTrue(p1.passesBasicChecks(to, from, b1));
	}

}
