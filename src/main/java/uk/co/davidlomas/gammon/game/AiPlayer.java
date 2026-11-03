/*
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

package uk.co.davidlomas.gammon.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.genes.Individual;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class AIPlayer.
 * <p>
 * extends the Player class and implements methods to allow the AI to choose the
 * next move
 *
 * @author David Lomas
 */
public class AiPlayer extends Player {

	private final static Logger logger = LoggerFactory.getLogger(AiPlayer.class);
	private final MoveGenerator moveGenerator;
	private Individual individual;
	// while the move generator tries out a turn, the dice this copy of the player has used so far, in order
	final List<Integer> diceUsed = new ArrayList<>();

	/**
	 * Clone constructor
	 *
	 * @param playerToClone the player
	 */
	AiPlayer(final AiPlayer playerToClone) {
		super(playerToClone.black, playerToClone.dice);
		movesLeft = new MovesList(playerToClone.movesLeft);
		black = playerToClone.black;
		moveGenerator = playerToClone.moveGenerator;
		diceUsed.addAll(playerToClone.diceUsed);
	}

	public AiPlayer(final boolean black, final Individual individual) {
		super(black);
		this.individual = individual;
		moveGenerator = new MoveGenerator(individual);
	}

	Board AiTurn(final Board currentBoard) {
		return AiTurn(currentBoard, dice.RollTwoDice());
	}

	/**
	 * A turn with dice already rolled, such as the opening move, which uses the dice from the opening roll
	 *
	 * @param currentBoard the board
	 * @param roll         the dice to play
	 *
	 * @return the board after the move, or the same board if no move could be made
	 */
	Board AiTurn(final Board currentBoard, final List<Integer> roll) {

		logger.trace("------------{} player's turn!-----------------", getColourString());

		movesLeft = new MovesList();
		movesLeft.setTo(roll);

		logger.trace("Player has {}", movesLeft.toString());

		Board newBoard = moveGenerator.getNextMoveBoard(currentBoard, this);

		// if the board has not changed
		if (newBoard == null) {
			newBoard = currentBoard;
		}
		return newBoard;
	}

	public Individual getIndividual() {
		return individual;
	}
}