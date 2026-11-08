package uk.co.davidlomas.gammon.gui;

import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.SavedAttempts.Attempt;
import uk.co.davidlomas.gammon.genes.SavedPlayers;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Shows every attempt saved: the GA settings it ran with, how each of its generations came out, and the genes of each
 * generation's best player. It shows what was on file when it opened, so a search still running carries on without it.
 */
public class PlayersFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter STARTED = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.UK)
			.withZone(ZoneId.systemDefault());
	private static final String CONTACT = "contact";
	private static final String RACE = "race";

	private final transient Runnable showMenu;
	private final AttemptsModel attempts;
	private final SearchProgressFrame.GenerationsModel generations = new SearchProgressFrame.GenerationsModel();
	private final GenesModel genes = new GenesModel();
	private final JTable attemptsTable;
	private final JTable generationsTable;
	private final JLabel settings = MainMenuPanel.label(" ", Font.PLAIN, 13, MainMenuPanel.CREAM);
	private final JLabel generationsTitle = MainMenuPanel.label(" ", Font.BOLD, 14, MainMenuPanel.CREAM);
	private final JLabel genesTitle = MainMenuPanel.label(" ", Font.BOLD, 14, MainMenuPanel.CREAM);

	/**
	 * @param attempts what to show, newest first. There has to be at least one
	 * @param showMenu run on the Swing thread when going back to the menu
	 */
	public PlayersFrame(final List<Attempt> attempts, final Runnable showMenu) {
		this.attempts = new AttemptsModel(attempts);
		this.showMenu = showMenu;
		setTitle("GA-mmon: saved players");

		attemptsTable = selectable(SearchProgressFrame.table(this.attempts, 1));
		attemptsTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				showAttempt();
			}
		});
		// the date needs the room, the attempt's number doesn't
		final int[] widths = {55, 165, 90, 110};
		for (int column = 0; column < widths.length; column++) {
			attemptsTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
		}
		generationsTable = selectable(SearchProgressFrame.table(generations, 1));
		generationsTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				showPlayer();
			}
		});
		final JTable genesTable = SearchProgressFrame.table(genes, 1);

		final JPanel header = new JPanel(new BorderLayout(12, 0));
		header.setOpaque(false);
		header.add(MainMenuPanel.label(summaryText(attempts), Font.BOLD, 18, MainMenuPanel.CREAM), BorderLayout.CENTER);
		header.add(SearchProgressFrame.backToMenuButton(this, this::back), BorderLayout.EAST);

		final JPanel left = SearchProgressFrame.titled("Attempts, newest first", attemptsTable);
		left.setPreferredSize(new Dimension(420, 0));

		final JPanel tables = new JPanel(new GridLayout(2, 1, 0, 14));
		tables.setOpaque(false);
		tables.add(SearchProgressFrame.titled(generationsTitle, generationsTable));
		tables.add(SearchProgressFrame.titled(genesTitle, genesTable));

		final JPanel right = new JPanel(new BorderLayout(0, 10));
		right.setOpaque(false);
		right.add(settings, BorderLayout.NORTH);
		right.add(tables, BorderLayout.CENTER);

		final JPanel middle = new JPanel(new BorderLayout(16, 0));
		middle.setOpaque(false);
		middle.add(left, BorderLayout.WEST);
		middle.add(right, BorderLayout.CENTER);

		final JPanel content = new JPanel(new BorderLayout(0, 14));
		content.setBackground(MainMenuPanel.FELT);
		content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		content.add(header, BorderLayout.NORTH);
		content.add(middle, BorderLayout.CENTER);
		add(content);

		setSize(SearchProgressFrame.fitted(new Dimension(1300, 860),
				GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()));
		setMinimumSize(new Dimension(900, 560));
		setLocationRelativeTo(null);
		// the menu is hidden behind this window, so closing it has to bring that back, or nothing would be showing
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				back();
			}
		});

		attemptsTable.setRowSelectionInterval(0, 0);
	}

	/**
	 * A table whose rows can be picked, with the mouse or the arrow keys
	 */
	private static JTable selectable(final JTable table) {
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setSelectionBackground(MainMenuPanel.ACCENT);
		table.setSelectionForeground(MainMenuPanel.CREAM);
		table.setFocusable(true);
		return table;
	}

	/**
	 * Goes back to the menu
	 */
	final void back() {
		dispose();
		showMenu.run();
	}

	/**
	 * Shows the attempt in this row, and picks its latest generation
	 */
	void selectAttempt(final int row) {
		attemptsTable.setRowSelectionInterval(row, row);
	}

	/**
	 * Shows the best player of the generation in this row, numbered newest first
	 */
	void selectGeneration(final int row) {
		generationsTable.setRowSelectionInterval(row, row);
	}

	String settingsShown() {
		return settings.getText();
	}

	String genesTitle() {
		return genesTitle.getText();
	}

	GenesModel genes() {
		return genes;
	}

	private void showAttempt() {
		final int row = attemptsTable.getSelectedRow();
		if (row < 0) {
			return;
		}
		final Attempt attempt = attempts.attemptAt(row);
		settings.setText(settingsText(attempt.settings()));
		generationsTitle.setText("Attempt " + attempt.number() + "'s generations, newest first: pick one to see its "
				+ "best player");
		generations.showNow(attempt.generations());
		if (generations.getRowCount() > 0) {
			generationsTable.setRowSelectionInterval(0, 0);
		} else {
			genes.show(null);
			genesTitle.setText("This attempt has no stats, so there are no players to show");
		}
	}

	private void showPlayer() {
		final int row = generationsTable.getSelectedRow();
		if (row < 0) {
			return;
		}
		final Attempt attempt = attempts.attemptAt(attemptsTable.getSelectedRow());
		final String generation = generations.resultAt(row).generation();
		final Individual player = attempt.player(generation);
		genes.show(player);
		final String name = SavedPlayers.name(attempt.number(), generation);
		genesTitle.setText(player == null
				? name + ": its player wasn't saved, or was saved by an older version with other genes"
				: "The genes of the best player of " + name + " (weights from 0 to 100)");
	}

	static String summaryText(final List<Attempt> attempts) {
		final int generations = attempts.stream().mapToInt(a -> a.generations().size()).sum();
		return String.format(Locale.ROOT, "%d %s, %d %s with a best player each", attempts.size(),
				attempts.size() == 1 ? "attempt" : "attempts", generations,
				generations == 1 ? "generation" : "generations");
	}

	/**
	 * @return the GA settings in words, or that they weren't saved
	 */
	static String settingsText(final Map<String, String> settings) {
		if (settings.isEmpty()) {
			return "This attempt's GA settings weren't saved: only attempts made from now on save them";
		}
		final Map<String, String> words = new LinkedHashMap<>(settings);
		final List<String> parts = new ArrayList<>();
		said(words, "populationSize", "a population of %s", parts);
		said(words, "generations", "%s generations", parts);
		said(words, "gamesPerPairing", "%s meetings a pairing", parts);
		said(words, "tournamentSize", "tournaments of %s", parts);
		said(words, "uniformRate", "a crossover rate of %s", parts);
		said(words, "mutationRate", "a mutation rate of %s", parts);
		final String elitism = words.remove("elitism");
		if (elitism != null) {
			parts.add(Boolean.parseBoolean(elitism) ? "the fittest kept each generation"
					: "the fittest not kept each generation");
		}
		// any others, from a later version, as they are
		words.forEach((name, value) -> parts.add(name + " " + value));
		return "Ran with " + String.join(", ", parts);
	}

	private static void said(final Map<String, String> settings, final String name, final String words,
							 final List<String> parts) {
		final String value = settings.remove(name);
		if (value != null) {
			parts.add(String.format(Locale.ROOT, words, value));
		}
	}

	/**
	 * @return a gene's feature in words, such as "Pip lead" for "PipLead"
	 */
	static String words(final String feature) {
		final StringBuilder words = new StringBuilder();
		for (final char letter : feature.toCharArray()) {
			if (Character.isUpperCase(letter) && !words.isEmpty()) {
				words.append(' ').append(Character.toLowerCase(letter));
			} else {
				words.append(words.isEmpty() ? Character.toUpperCase(letter) : letter);
			}
		}
		return words.toString();
	}

	/**
	 * A row for each attempt
	 */
	static final class AttemptsModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private static final String[] COLUMNS = {"Attempt", "Started", "Generations", "Best v Reference"};

		private final transient List<Attempt> rows;

		AttemptsModel(final List<Attempt> attempts) {
			rows = attempts;
		}

		Attempt attemptAt(final int row) {
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
			final Attempt attempt = rows.get(row);
			return switch (column) {
				case 0 -> attempt.number();
				case 1 -> attempt.started() == null ? "" : STARTED.format(attempt.started());
				// the initial population's row isn't a generation of evolving
				case 2 -> attempt.generations().stream().filter(g -> !SavedPlayers.INITIAL.equals(g.generation())).count();
				default -> {
					final Double best = attempt.bestPointsAgainstReference();
					yield best == null ? "" : String.format(Locale.ROOT, "%+.3f", best);
				}
			};
		}
	}

	/**
	 * A row for each feature of the board a player weighs, with its weight while the players can hit each other and
	 * in the race after, which only some features have
	 */
	static final class GenesModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private static final String[] COLUMNS = {"Feature", "In contact", "In the race"};

		private transient List<String[]> rows = new ArrayList<>();

		void show(final Individual player) {
			final Map<String, String[]> features = new LinkedHashMap<>();
			if (player != null) {
				for (int gene = 0; gene < player.getNumOfAttributes(); gene++) {
					final String name = player.getAttribute(gene).getName();
					final int value = player.getAttribute(gene).getValue();
					final boolean contact = name.startsWith(CONTACT);
					final String feature = name.substring(contact ? CONTACT.length()
							: name.startsWith(RACE) ? RACE.length() : 0);
					final String[] row = features.computeIfAbsent(feature, f -> new String[]{words(f), "", ""});
					row[contact ? 1 : 2] = String.valueOf(value);
				}
			}
			rows = new ArrayList<>(features.values());
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
			return rows.get(row)[column];
		}
	}
}
