package uk.co.davidlomas.gammon.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.gui.BoardContainerFrame;
import uk.co.davidlomas.gammon.settings.GameSettings;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

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

	private final static Logger logger = LoggerFactory.getLogger(GameManager.class);

	// set once, by startGui, and read by the game threads when they draw
	static volatile BoardContainerFrame boardContainerFrame;

	// what closing the board window does. Tests make it only close the window, rather than end the program
	static int windowCloseOperation = WindowConstants.EXIT_ON_CLOSE;

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
		return play(individual1, individual2, game -> {
		});
	}

	/**
	 * Plays a game with a person as player 2, reading their moves from in and writing to out
	 *
	 * @param ai  player 1's genes
	 * @param in  where the person's moves come from, such as System.in
	 * @param out where the board and prompts go, such as System.out
	 *
	 * @return GameStats result of game (winner, values)
	 */
	GameStats playAgainstPerson(final Individual ai, final InputStream in, final PrintStream out) {
		return play(ai, new Individual(), game -> game.getPlayerTwo().useConsole(in, out));
	}

	private GameStats play(final Individual individual1, final Individual individual2, final Consumer<Game> setUp) {

		if (GameSettings.getDisplayGUI()) {
			if (SwingUtilities.isEventDispatchThread()) {
				logger.warn("A game is being played on the Swing thread, so the board window can't update until it ends."
						+ " Start games on another thread, as the Start Search button does");
			}
			startGui();
		}

		// The Game Thread
		final Game currentGame = new Game(individual1, individual2);
		setUp.accept(currentGame);
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
		final Runnable build = () -> {
			frame.run();
			frame.setDefaultCloseOperation(windowCloseOperation);
		};
		// invokeAndWait can't be used from the Swing thread itself, which is already the right place to build it
		if (SwingUtilities.isEventDispatchThread()) {
			build.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(build);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException("Interrupted while opening the board window", e);
			} catch (final InvocationTargetException e) {
				throw new IllegalStateException("Couldn't open the board window", e.getCause());
			}
		}
		boardContainerFrame = frame;
	}
}