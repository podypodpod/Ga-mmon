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

package uk.co.davidlomas.gammon.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.davidlomas.gammon.genes.Individual;

/**
 * GameStats
 *
 * The game stats class is returned after a finished game and supplies the
 * winning player as well as a score for the game.
 *
 * @author David Lomas
 */
public class GameStats {

	final static Logger logger = LoggerFactory.getLogger(GameStats.class);

	private Individual gameVictor = null;
	private double playerOneScore = 0;
	private double playerTwoScore = 0;

	/**
	 * GameStats default constructor
	 *
	 * @param winner
	 * @param p1score
	 * @param p2score
	 */
	public GameStats(final Individual gameVictor, final double playerOneScore, final double playerTwoScore) {
		this.gameVictor = gameVictor;
		this.playerOneScore = playerOneScore;
		this.playerOneScore = playerTwoScore;
	}

	/**
	 * getPlayerOneScore
	 *
	 * @return individual
	 */
	public double getPlayerOneScore() {
		return playerOneScore;
	}

	/**
	 * getVictor
	 *
	 * @return individual
	 */
	public Individual getVictor() {
		return gameVictor;
	}

	public double getPlayerTwoScore() {
		return playerTwoScore;
	}

	public void setPlayerTwoScore(final double playerTwoScore) {
		this.playerTwoScore = playerTwoScore;
	}
}