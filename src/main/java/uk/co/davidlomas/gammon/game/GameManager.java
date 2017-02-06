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

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.gui.BoardContainerFrame;
import uk.co.davidlomas.gammon.settings.GameSettings;

/**
 * GameManager
 *
 * Allows the starting of games and returns the results of games without having
 * to deal with all the actual methods etc in the game class
 *
 * this class will be called when testing players etc, you can just make an
 * object and then pass it two individuals to the method to play a game and
 * return results.
 *
 * @author David Lomas
 */
public class GameManager {

  static BoardContainerFrame boardContainerFrame;

  /**
   * play Individuals Vs Each Other
   *
   * @param individual1 individual 1
   * @param individual2 individual 2
   * @return GameStats result of game (winner, values)
   */
  public GameStats playIndividualsVsEachOther(final Individual individual1,
      final Individual individual2) {

    if (GameSettings.getDisplayGUI()) {
      startGui();
    }

    // The Game Thread
    final Game currentGame = new Game(individual1, individual2);
    currentGame.run();
    return currentGame.getGameStats();
  }

  /**
   * start the Gui
   */
  private void startGui() {
    // The Gui Thread
    boardContainerFrame = new BoardContainerFrame();
    final Thread guiThread = new Thread(boardContainerFrame);
    guiThread.start();

    // Stops it multi-threading,
    try {
      if (!GameSettings.getMultiThreading()) {
        guiThread.join();
      }
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }
}