/*
  GNU General Public License

  This file is part of GA-mmon.

  GA-mmon is free software: you can redistribute it and/or modify it under the
  terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
  A PARTICULAR PURPOSE. See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with
  GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.gui.BoardContainerFrame;
import uk.co.davidlomas.gammon.settings.GameSettings;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

/**
 * GameManager
 * <p>
 * Allows the starting of games and returns the results of games without having
 * to deal with all the actual methods etc in the game class
 * <p>
 * this class will be called when testing players etc, you can just make an
 * object and then pass it two individuals to the method to play a game and
 * return results.
 *
 * @author David Lomas
 */
public class GameManager {

	// set once, by startGui, and read by the game threads when they draw
	static volatile BoardContainerFrame boardContainerFrame;

	/**
	 * play Individuals Vs Each Other
	 *
	 * @param individual1 individual 1
	 * @param individual2 individual 2
	 *
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
	 * <p>
	 * One window is shared by every game, made the first time it's needed. It's built on the Swing thread, and this
	 * waits for it, so the board panel is there before the game first draws on it
	 */
	private static synchronized void startGui() {
		if (boardContainerFrame != null) {
			return;
		}
		final BoardContainerFrame frame = new BoardContainerFrame();
		// invokeAndWait can't be used from the Swing thread itself, which is already the right place to build it
		if (SwingUtilities.isEventDispatchThread()) {
			frame.run();
			boardContainerFrame = frame;
			return;
		}
		try {
			SwingUtilities.invokeAndWait(frame);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Interrupted while opening the board window", e);
		} catch (final InvocationTargetException e) {
			throw new IllegalStateException("Couldn't open the board window", e.getCause());
		}
		boardContainerFrame = frame;
	}
}