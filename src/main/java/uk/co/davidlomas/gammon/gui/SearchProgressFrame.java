package uk.co.davidlomas.gammon.gui;

import uk.co.davidlomas.gammon.game.WinType;
import uk.co.davidlomas.gammon.genes.SearchProgress;
import uk.co.davidlomas.gammon.genes.SearchProgress.GenerationResult;
import uk.co.davidlomas.gammon.genes.SearchProgress.LiveGame;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Shows how a search is going: how far through it is, the stats so far, every game being played, and how each
 * generation came out. It looks at the search's progress a few times a second, so it shows the games as they are at
 * that moment rather than every turn, and it can't slow them down by falling behind.
 * <p>
 * Going back to the menu, by its button or by closing it, doesn't stop the search.
 */
public class SearchProgressFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int REFRESH_MILLIS = 200;
	static final Color GRID = new Color(0x2F6650);

	private final transient SearchProgress progress;
	private final transient Runnable showMenu;
	private final JLabel status = MainMenuPanel.label("Starting", Font.BOLD, 18, MainMenuPanel.CREAM);
	private final JProgressBar bar = new JProgressBar(0, 1000);
	private final JLabel games = tileValue();
	private final JLabel gamesPerSecond = tileValue();
	private final JLabel elapsed = tileValue();
	private final JLabel turnsPerGame = tileValue();
	private final JLabel draws = tileValue();
	private final JLabel gammons = tileValue();
	private final JLabel thisRound = MainMenuPanel.label(" ", Font.PLAIN, 13, MainMenuPanel.MUTED);
	private final LiveGamesModel liveGames = new LiveGamesModel();
	private final GenerationsModel generations = new GenerationsModel();
	private final Timer timer = new Timer(REFRESH_MILLIS, e -> refresh());

	/**
	 * @param showMenu run on the Swing thread when going back to the menu
	 */
	public SearchProgressFrame(final SearchProgress progress, final Runnable showMenu) {
		this.progress = progress;
		this.showMenu = showMenu;
		setTitle("GA-mmon: search progress");

		final JPanel content = new JPanel(new BorderLayout(0, 14));
		content.setBackground(MainMenuPanel.FELT);
		content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		content.add(summary(), BorderLayout.NORTH);
		content.add(tables(), BorderLayout.CENTER);
		add(content);

		// tall enough to show a game for each of 12 cores without scrolling, if the screen is
		setSize(fitted(new Dimension(1000, 890),
				GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()));
		setMinimumSize(new Dimension(760, 560));
		setLocationRelativeTo(null);
		// the menu is hidden behind this window, so closing it has to bring that back, or nothing would be showing
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				back();
			}

			@Override
			public void windowClosed(final WindowEvent e) {
				timer.stop();
			}
		});
	}

	/**
	 * Shows the window, and keeps it up to date until the search ends. Call it on the Swing thread
	 */
	public void open() {
		timer.start();
		refresh();
		setVisible(true);
	}

	/**
	 * Goes back to the menu, leaving the search running. It can come back to this window while the search runs, but
	 * once that's over there's nothing to come back for
	 */
	final void back() {
		timer.stop();
		if (progress.getState() == SearchProgress.State.RUNNING) {
			setVisible(false);
		} else {
			dispose();
		}
		showMenu.run();
	}

	/**
	 * @param window the window it's for, where Escape will do the same as pressing it
	 * @param back   what going back does
	 */
	static JButton backToMenuButton(final JFrame window, final Runnable back) {
		final MenuButton button = new MenuButton("Back to Menu");
		button.setPreferredSize(new Dimension(160, 36));
		// it's the only thing that could take the focus, and then the space bar would press it. Escape does it instead
		button.setFocusable(false);
		button.addActionListener(e -> back.run());
		window.getRootPane().registerKeyboardAction(e -> back.run(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		return button;
	}

	private JPanel header() {
		final JButton back = backToMenuButton(this, this::back);

		final JPanel header = new JPanel(new BorderLayout(12, 0));
		header.setOpaque(false);
		header.add(status, BorderLayout.CENTER);
		header.add(back, BorderLayout.EAST);
		return header;
	}

	/**
	 * @return the size wanted, cut down to fit on the screen
	 */
	static Dimension fitted(final Dimension wanted, final Rectangle screen) {
		return new Dimension(Math.min(wanted.width, screen.width), Math.min(wanted.height, screen.height));
	}

	private JPanel summary() {
		final JPanel summary = new JPanel();
		summary.setOpaque(false);
		summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));

		// the colours of the percentage over each part of the bar, which otherwise don't suit it
		bar.setUI(new BasicProgressBarUI() {
			@Override
			protected Color getSelectionBackground() {
				return MainMenuPanel.FELT;
			}

			@Override
			protected Color getSelectionForeground() {
				return MainMenuPanel.CREAM;
			}
		});
		bar.setStringPainted(true);
		bar.setForeground(MainMenuPanel.ACCENT);
		bar.setBackground(MainMenuPanel.CREAM);
		bar.setBorderPainted(false);
		bar.setPreferredSize(new Dimension(0, 22));
		bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

		final JPanel tiles = new JPanel(new GridLayout(1, 6, 12, 0));
		tiles.setOpaque(false);
		tiles.add(tile(games, "Games played"));
		tiles.add(tile(gamesPerSecond, "Games a second"));
		tiles.add(tile(elapsed, "Time so far"));
		tiles.add(tile(turnsPerGame, "Turns a game"));
		tiles.add(tile(draws, "Cut short as draws"));
		tiles.add(tile(gammons, "Gammons or better"));

		final JPanel header = header();
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		for (final JComponent part : List.of(header, bar, thisRound, tiles)) {
			part.setAlignmentX(LEFT_ALIGNMENT);
		}
		// as wide as it likes, so its text isn't cut off when it grows between layouts
		thisRound.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		summary.add(header);
		summary.add(Box.createVerticalStrut(10));
		summary.add(bar);
		summary.add(Box.createVerticalStrut(6));
		summary.add(thisRound);
		summary.add(Box.createVerticalStrut(12));
		summary.add(tiles);
		return summary;
	}

	private JPanel tables() {
		final JPanel tables = new JPanel(new GridLayout(2, 1, 0, 14));
		tables.setOpaque(false);
		tables.add(titled("Games being played (players are numbered afresh each generation)", table(liveGames, 2)));
		tables.add(titled("Generations, newest first", table(generations, 1)));
		return tables;
	}

	static JPanel titled(final String title, final JTable table) {
		return titled(MainMenuPanel.label(title, Font.BOLD, 14, MainMenuPanel.CREAM), table);
	}

	/**
	 * @param title above the table, which can be changed later to say what's in it
	 */
	static JPanel titled(final JLabel title, final JTable table) {
		final JScrollPane scroll = new JScrollPane(table);
		scroll.getViewport().setBackground(MainMenuPanel.FELT);
		scroll.setBorder(BorderFactory.createLineBorder(GRID));

		final JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.setOpaque(false);
		panel.add(title, BorderLayout.NORTH);
		panel.add(scroll, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * @param firstNumberColumn the columns from here on hold numbers, so they're lined up on the right
	 */
	static JTable table(final AbstractTableModel model, final int firstNumberColumn) {
		final JTable table = new JTable(model);
		table.setBackground(MainMenuPanel.FELT);
		table.setForeground(MainMenuPanel.CREAM);
		table.setGridColor(GRID);
		table.setRowHeight(22);
		table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
		table.setRowSelectionAllowed(false);
		table.setFocusable(false);
		table.setFillsViewportHeight(true);

		final DefaultTableCellRenderer numbers = new DefaultTableCellRenderer();
		numbers.setHorizontalAlignment(SwingConstants.RIGHT);
		for (int column = firstNumberColumn; column < model.getColumnCount(); column++) {
			table.getColumnModel().getColumn(column).setCellRenderer(numbers);
		}

		final JTableHeader header = table.getTableHeader();
		header.setBackground(MainMenuPanel.CREAM);
		header.setForeground(MainMenuPanel.FELT);
		header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		header.setReorderingAllowed(false);
		return table;
	}

	private static JPanel tile(final JLabel value, final String caption) {
		final JPanel tile = new JPanel(new BorderLayout(0, 2));
		tile.setOpaque(false);
		tile.add(value, BorderLayout.NORTH);
		tile.add(MainMenuPanel.label(caption, Font.PLAIN, 12, MainMenuPanel.MUTED), BorderLayout.SOUTH);
		return tile;
	}

	private static JLabel tileValue() {
		return MainMenuPanel.label("0", Font.BOLD, 22, MainMenuPanel.CREAM);
	}

	/**
	 * Shows the search as it is now, and stops looking once it has ended
	 */
	final void refresh() {
		final SearchProgress.State state = progress.getState();

		status.setText(statusText(progress));
		final double share = progress.getShareDone();
		bar.setValue((int) Math.round(share * bar.getMaximum()));
		bar.setString(String.format(Locale.ROOT, "%.0f%%", share * 100));
		thisRound.setText(state == SearchProgress.State.RUNNING ? roundText(progress) : " ");

		final double seconds = progress.getSeconds();
		games.setText(String.format(Locale.ROOT, "%,d", progress.getGames()));
		gamesPerSecond.setText(String.format(Locale.ROOT, "%,.0f", seconds == 0 ? 0 : progress.getGames() / seconds));
		elapsed.setText(minutesAndSeconds(seconds));
		turnsPerGame.setText(String.format(Locale.ROOT, "%.1f", progress.getTurnsPerGame()));
		draws.setText(percent(progress.getRate(WinType.DRAW)));
		gammons.setText(percent(progress.getRate(WinType.GAMMON) + progress.getRate(WinType.BACKGAMMON)));

		liveGames.show(state == SearchProgress.State.RUNNING ? progress.getLiveGames() : List.of());
		generations.show(progress.getResults());

		if (state != SearchProgress.State.RUNNING) {
			timer.stop();
		}
	}

	static String statusText(final SearchProgress progress) {
		return switch (progress.getState()) {
			case RUNNING -> progress.getStage();
			case FINISHED -> progress.getSavedIn() == null ? "Finished"
					: "Finished: the players are saved in " + progress.getSavedIn();
			case FAILED -> "The search stopped because of an error: see the terminal for why";
		};
	}

	/**
	 * @return how far through its round the search is, blank between rounds
	 */
	static String roundText(final SearchProgress progress) {
		final int planned = progress.getGamesPlannedThisRound();
		if (planned == 0) {
			return " ";
		}
		return String.format(Locale.ROOT, "%s: game %,d of %,d", capitalised(progress.getRound().describe()),
				progress.getGamesThisRound(), planned);
	}

	private static String capitalised(final String text) {
		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}

	static String minutesAndSeconds(final double seconds) {
		final long whole = (long) seconds;
		return String.format(Locale.ROOT, "%d:%02d", whole / 60, whole % 60);
	}

	static String percent(final double share) {
		return String.format(Locale.ROOT, "%.1f%%", share * 100);
	}

	/**
	 * A row for each game that can be playing at once
	 */
	static final class LiveGamesModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private static final String[] COLUMNS = {"Black", "Red", "Turn", "Black pips", "Red pips", "Black off", "Red off"};

		private transient List<LiveGame> rows = new ArrayList<>();

		void show(final List<LiveGame> games) {
			rows = games;
			fireTableDataChanged();
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
			final LiveGame game = rows.get(row);
			if (game == null) {
				return column == 0 ? "Between games" : "";
			}
			return switch (column) {
				case 0 -> game.black();
				case 1 -> game.red();
				case 2 -> game.turns();
				case 3 -> game.blackPips();
				case 4 -> game.redPips();
				case 5 -> game.blackOff();
				default -> game.redOff();
			};
		}
	}

	/**
	 * A row for each finished generation, newest first
	 */
	static final class GenerationsModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private static final String[] COLUMNS = {"Generation", "Best fitness", "Gene spread", "Turns a game", "Draws",
				"Single wins", "Gammons", "Backgammons", "v Reference"};

		private transient List<GenerationResult> rows = new ArrayList<>();

		void show(final List<GenerationResult> results) {
			// the same list means nothing has changed, and redrawing would only make it flicker
			if (results.size() != rows.size()) {
				showNow(results);
			}
		}

		/**
		 * Shows these, even if there are as many as before, such as another search's
		 */
		void showNow(final List<GenerationResult> results) {
			rows = results;
			fireTableDataChanged();
		}

		/**
		 * @return the generation in that row
		 */
		GenerationResult resultAt(final int row) {
			return rows.get(rows.size() - 1 - row);
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
			final GenerationResult result = resultAt(row);
			return switch (column) {
				case 0 -> result.generation();
				case 1 -> String.format(Locale.ROOT, "%+.3f", result.bestFitness());
				case 2 -> String.format(Locale.ROOT, "%.1f", result.geneSpread());
				case 3 -> String.format(Locale.ROOT, "%.1f", result.turnsPerGame());
				case 4 -> percent(result.drawRate());
				case 5 -> percent(result.singleRate());
				case 6 -> percent(result.gammonRate());
				case 7 -> percent(result.backgammonRate());
				default -> result.pointsAgainstReference() == null ? ""
						: String.format(Locale.ROOT, "%+.3f", result.pointsAgainstReference());
			};
		}
	}
}
