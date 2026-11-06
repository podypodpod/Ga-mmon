package uk.co.davidlomas.gammon.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameObserver;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.SavedPlayers.SavedPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

/**
 * Plays a live game between two saved players, slowly enough to follow. Going back to the menu, by its button or by
 * closing it, ends the game.
 */
public class WatchGameFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(WatchGameFrame.class);

	private static final int MILLIS_BETWEEN_TURNS = 1000;

	private final transient SavedPlayer first;
	private final transient SavedPlayer second;
	private final transient Runnable showMenu;
	private final int millisBetweenTurns;
	private final transient Thread game;
	private final JLabel status = MainMenuPanel.label("Starting", Font.BOLD, 18, MainMenuPanel.CREAM);
	private final JLabel blackLine = MainMenuPanel.label(" ", Font.PLAIN, 14, MainMenuPanel.CREAM);
	private final JLabel redLine = MainMenuPanel.label(" ", Font.PLAIN, 14, MainMenuPanel.CREAM);
	private final BoardPanel board = new BoardPanel();

	/**
	 * @param showMenu run on the Swing thread when going back to the menu
	 */
	public WatchGameFrame(final SavedPlayer first, final SavedPlayer second, final Runnable showMenu) {
		this(first, second, showMenu, MILLIS_BETWEEN_TURNS);
	}

	WatchGameFrame(final SavedPlayer first, final SavedPlayer second, final Runnable showMenu,
				   final int millisBetweenTurns) {
		this.first = first;
		this.second = second;
		this.showMenu = showMenu;
		this.millisBetweenTurns = millisBetweenTurns;
		game = new Thread(this::play, "Watched game");
		// nothing is saved by this game, so there's nothing to wait for if the program is ending
		game.setDaemon(true);

		setTitle("GA-mmon: watching the best two players");

		final JPanel top = new JPanel(new BorderLayout(12, 0));
		top.setOpaque(false);
		top.add(status, BorderLayout.CENTER);
		final JButton back = SearchProgressFrame.backToMenuButton(this, this::back);
		top.add(back, BorderLayout.EAST);

		final JPanel players = new JPanel(new GridLayout(2, 1, 0, 4));
		players.setOpaque(false);
		players.add(blackLine);
		players.add(redLine);

		final JPanel summary = new JPanel(new BorderLayout(0, 10));
		summary.setOpaque(false);
		summary.add(top, BorderLayout.NORTH);
		summary.add(players, BorderLayout.CENTER);

		// the board is drawn at a fixed size, so it stays that size in the middle of whatever room there is
		board.setPreferredSize(new Dimension(1153, 600));
		final JPanel middle = new JPanel(new GridBagLayout());
		middle.setOpaque(false);
		middle.add(board);

		final JPanel content = new JPanel(new BorderLayout(0, 12));
		content.setBackground(MainMenuPanel.FELT);
		content.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
		content.add(summary, BorderLayout.NORTH);
		content.add(middle, BorderLayout.CENTER);
		add(content);

		setSize(SearchProgressFrame.fitted(new Dimension(1210, 790),
				GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()));
		setLocationRelativeTo(null);
		// the menu is hidden behind this window, so closing it has to bring that back, or nothing would be showing
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				back();
			}
		});
	}

	/**
	 * Shows the window and starts the game. Call it on the Swing thread
	 */
	public void start() {
		setVisible(true);
		game.start();
	}

	/**
	 * Ends the game and goes back to the menu
	 */
	final void back() {
		game.interrupt();
		dispose();
		showMenu.run();
	}

	/**
	 * @return what the window is saying about the game
	 */
	String status() {
		return status.getText();
	}

	Thread gameThread() {
		return game;
	}

	private void play() {
		try {
			final GameManager manager = new GameManager();
			manager.setObserver(new Watcher());
			manager.playToWatch(first.player(), second.player());
		} catch (final RuntimeException | Error e) {
			logger.error("The watched game failed", e);
			SwingUtilities.invokeLater(() -> status.setText("The game stopped because of an error: see the terminal for why"));
		}
	}

	static String playerLine(final String colour, final SavedPlayer player, final int pips, final int off) {
		return String.format(Locale.ROOT, "%s: %s (%+.3f against the reference player), %d pips to go, %d off", colour,
				player.name(), player.pointsAgainstReference(), pips, off);
	}

	static String resultText(final GameStats stats, final SavedPlayer first, final SavedPlayer second) {
		final Individual victor = stats.getVictor();
		if (victor == null) {
			return "A draw: the game went on too long, so it was cut short";
		}
		return (victor == first.player() ? first : second).name() + " won " + stats.getWinType().describe();
	}

	/**
	 * Shows each turn, then waits, which is what slows the game down to be watched. It's called on the game's thread
	 */
	private final class Watcher implements GameObserver {

		private SavedPlayer black;
		private SavedPlayer red;
		private int turn;

		@Override
		public void gameStarted(final Individual playerOne, final Individual playerTwo, final boolean playerOneIsBlack) {
			black = playerOneIsBlack ? first : second;
			red = playerOneIsBlack ? second : first;
			show(new Board());
			pause();
		}

		@Override
		public void turnPlayed(final Board now) {
			turn++;
			show(now);
			pause();
		}

		@Override
		public void gameFinished(final GameStats stats) {
			final String result = resultText(stats, first, second);
			SwingUtilities.invokeLater(() -> status.setText(result));
		}

		private void show(final Board now) {
			board.printCheckers(now);
			final String turnText = turn == 0 ? "Opening roll" : "Turn " + turn;
			final String blackText = playerLine("Black", black, now.pipCount(true), now.getBlackBore());
			final String redText = playerLine("Red", red, now.pipCount(false), now.getRedBore());
			SwingUtilities.invokeLater(() -> {
				status.setText(turnText);
				blackLine.setText(blackText);
				redLine.setText(redText);
			});
		}

		private void pause() {
			try {
				Thread.sleep(millisBetweenTurns);
			} catch (final InterruptedException e) {
				// left marked as interrupted, so the game sees that and stops
				Thread.currentThread().interrupt();
			}
		}
	}
}
