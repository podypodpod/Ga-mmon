package uk.co.davidlomas.gammon.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameObserver;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.game.MoveNotation;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.SavedPlayers.SavedPlayer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
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
	private final MovesModel moves = new MovesModel();
	// none of its columns lined up as numbers: the moves read left to right
	private final JTable movesTable = SearchProgressFrame.table(moves, MovesModel.COLUMNS.length);

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
		final JPanel boardArea = new JPanel(new GridBagLayout());
		boardArea.setOpaque(false);
		boardArea.add(board);

		final int[] widths = {40, 50, 45, 175};
		for (int column = 0; column < widths.length; column++) {
			movesTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
		}
		final JPanel movesPanel = SearchProgressFrame.titled("Moves, by the board's numbers", movesTable);
		movesPanel.setPreferredSize(new Dimension(330, 0));

		final JPanel middle = new JPanel(new BorderLayout(14, 0));
		middle.setOpaque(false);
		middle.add(boardArea, BorderLayout.CENTER);
		middle.add(movesPanel, BorderLayout.EAST);

		final JPanel content = new JPanel(new BorderLayout(0, 12));
		content.setBackground(MainMenuPanel.FELT);
		content.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
		content.add(summary, BorderLayout.NORTH);
		content.add(middle, BorderLayout.CENTER);
		add(content);

		setSize(SearchProgressFrame.fitted(new Dimension(1560, 790),
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

	/**
	 * @return the moves shown so far, a row each turn
	 */
	MovesModel moves() {
		return moves;
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
		public void turnPlayed(final Board before, final Board now, final boolean black, final List<Integer> dice) {
			turn++;
			final Move move = new Move(turn, black ? "Black" : "Red", dice.get(0) + "-" + dice.get(1),
					MoveNotation.describe(before, now, black, dice));
			show(now);
			SwingUtilities.invokeLater(() -> {
				moves.add(move);
				// the latest turn, at the bottom, is kept in sight
				movesTable.scrollRectToVisible(movesTable.getCellRect(moves.getRowCount() - 1, 0, true));
			});
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

	/**
	 * One turn: who played it, what they rolled, and the checkers they moved
	 */
	record Move(int turn, String player, String roll, String moves) {
	}

	/**
	 * A row for each turn, oldest first
	 */
	static final class MovesModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private static final String[] COLUMNS = {"Turn", "Player", "Roll", "Moves"};

		private final transient List<Move> rows = new ArrayList<>();

		void add(final Move move) {
			rows.add(move);
			fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
		}

		Move moveAt(final int row) {
			return rows.get(row);
		}

		@Override
		public int getRowCount() {
			return rows.size();
		}

		@Override
		public int getColumnCount() {
			return COLUMNS.length;
		}

		@Override
		public String getColumnName(final int column) {
			return COLUMNS[column];
		}

		@Override
		public Object getValueAt(final int row, final int column) {
			final Move move = rows.get(row);
			return switch (column) {
				case 0 -> move.turn();
				case 1 -> move.player();
				case 2 -> move.roll();
				default -> move.moves();
			};
		}
	}
}
