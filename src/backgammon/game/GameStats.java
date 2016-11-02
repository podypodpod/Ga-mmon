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

package backgammon.game;

import backgammon.genes.Individual;

/**
 * GameStats
 * 
 * The game stats class is returned after a finished game and supplies the
 * winning player as well as a score for the game.
 *
 * @author David Lomas - 11035527
 */
public class GameStats {

	Individual gameVictor = null;

	double playerOneScore = 0;
	double playerTwoScore = 0;

	/**
	 * GameStats default constructor
	 *
	 * @param winner
	 * @param p1score
	 * @param p2score
	 */
	public GameStats(final Individual winner, final double p1score, final double p2score) {
		gameVictor = winner;
		playerOneScore = p1score;
		playerOneScore = p2score;
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
}