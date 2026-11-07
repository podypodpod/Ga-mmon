package uk.co.davidlomas.gammon.gui;

import uk.co.davidlomas.gammon.settings.GenAlgSettings;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;

/**
 * Asks for the GA settings a search should run with, starting from those in conf/GenAlgSettings.properties, or the last
 * ones chosen. They're used for searches until the program ends, without changing the file.
 */
public class SearchSettingsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * The GA settings for a search
	 *
	 * @param gamesPerPairing how many times each pair of players meets, two games a meeting
	 * @param uniformRate     the chance of each gene coming from the first parent
	 */
	public record SearchSettings(int generations, int populationSize, int gamesPerPairing, int tournamentSize,
								 double uniformRate, double mutationRate, boolean elitism) {

		/**
		 * @return the settings searches use now
		 * @throws IllegalArgumentException or IllegalStateException if the settings file can't be read
		 */
		public static SearchSettings current() {
			return new SearchSettings(GenAlgSettings.getGenerations(), GenAlgSettings.getPopulationSize(),
					GenAlgSettings.getGamesPerPairing(), GenAlgSettings.getTournamentSize(),
					GenAlgSettings.getUniformRate(), GenAlgSettings.getMutationRate(), GenAlgSettings.isElitism());
		}

		/**
		 * Makes these the settings searches use, until the program ends
		 */
		public void apply() {
			GenAlgSettings.setGenerations(generations);
			GenAlgSettings.setPopulationSize(populationSize);
			GenAlgSettings.setGamesPerPairing(gamesPerPairing);
			GenAlgSettings.setTournamentSize(tournamentSize);
			GenAlgSettings.setUniformRate(uniformRate);
			GenAlgSettings.setMutationRate(mutationRate);
			GenAlgSettings.setElitism(elitism);
		}
	}

	private final JSpinner generations = spinner(new SpinnerNumberModel(1, 0, 100_000, 10));
	private final JSpinner populationSize = spinner(new SpinnerNumberModel(2, 2, 10_000, 2));
	private final JSpinner gamesPerPairing = spinner(new SpinnerNumberModel(1, 1, 1_000, 1));
	private final JSpinner tournamentSize = spinner(new SpinnerNumberModel(1, 1, 10_000, 1));
	private final JSpinner uniformRate = spinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.05));
	private final JSpinner mutationRate = spinner(new SpinnerNumberModel(0.02, 0.0, 1.0, 0.005));
	private final JCheckBox elitism = new JCheckBox("Keep the fittest player each generation");
	private final JLabel games = MainMenuPanel.label(" ", Font.PLAIN, 13, MainMenuPanel.MUTED);
	private SearchSettings chosen;

	/**
	 * @param owner   the window it's shown over
	 * @param initial the settings it starts with
	 */
	SearchSettingsDialog(final Window owner, final SearchSettings initial) {
		super(owner, "GA-mmon: settings for the search", ModalityType.APPLICATION_MODAL);

		final JPanel fields = new JPanel(new GridBagLayout());
		fields.setOpaque(false);
		row(fields, 0, "Generations", generations, "How many times the population breeds");
		row(fields, 1, "Population size", populationSize, "Players in each generation");
		row(fields, 2, "Meetings a pairing", gamesPerPairing,
				"Two games each: more is less luck, and slower");
		row(fields, 3, "Tournament size", tournamentSize,
				"Players competing to be each parent: bigger favours the fittest");
		row(fields, 4, "Crossover rate", uniformRate, "The chance of a gene coming from the first parent");
		row(fields, 5, "Mutation rate", mutationRate, "The chance of each bit of a child's genes flipping");
		elitism.setOpaque(false);
		elitism.setForeground(MainMenuPanel.CREAM);
		elitism.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		final GridBagConstraints check = constraints(1, 6);
		check.gridwidth = 2;
		fields.add(elitism, check);

		final MenuButton start = new MenuButton("Start");
		start.setPreferredSize(new Dimension(160, 40));
		start.addActionListener(e -> choose());
		final MenuButton cancel = new MenuButton("Cancel");
		cancel.setPreferredSize(new Dimension(160, 40));
		cancel.addActionListener(e -> dispose());
		final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		buttons.setOpaque(false);
		buttons.add(cancel);
		buttons.add(start);

		final JPanel bottom = new JPanel(new BorderLayout(0, 14));
		bottom.setOpaque(false);
		bottom.add(games, BorderLayout.NORTH);
		bottom.add(buttons, BorderLayout.SOUTH);

		final JPanel content = new JPanel(new BorderLayout(0, 16));
		content.setBackground(MainMenuPanel.FELT);
		content.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
		content.add(MainMenuPanel.label("Settings for this search", Font.BOLD, 20, MainMenuPanel.CREAM),
				BorderLayout.NORTH);
		content.add(fields, BorderLayout.CENTER);
		content.add(bottom, BorderLayout.SOUTH);
		setContentPane(content);

		show(initial);
		for (final JSpinner spinner : new JSpinner[]{generations, populationSize, gamesPerPairing}) {
			spinner.addChangeListener(e -> games.setText(gamesText(settings())));
		}
		games.setText(gamesText(settings()));

		getRootPane().setDefaultButton(start);
		getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		// before packing, as it changes the window's border, which would otherwise cut into what was packed
		setResizable(false);
		pack();
		setLocationRelativeTo(owner);
	}

	/**
	 * Asks for the settings, starting from the ones searches use now
	 *
	 * @param over shown over this component's window
	 *
	 * @return the settings chosen, already applied, or null if it was cancelled
	 * @throws IllegalArgumentException or IllegalStateException if the settings file can't be read
	 */
	static SearchSettings ask(final Component over) {
		final SearchSettingsDialog dialog = new SearchSettingsDialog(SwingUtilities.getWindowAncestor(over),
				SearchSettings.current());
		dialog.setVisible(true);
		final SearchSettings settings = dialog.chosen();
		if (settings != null) {
			settings.apply();
		}
		return settings;
	}

	/**
	 * @return what Start chose, null if it wasn't pressed
	 */
	SearchSettings chosen() {
		return chosen;
	}

	/**
	 * What pressing Start does
	 */
	final void choose() {
		chosen = settings();
		dispose();
	}

	/**
	 * Fills in the fields
	 */
	final void show(final SearchSettings settings) {
		generations.setValue(settings.generations());
		populationSize.setValue(Math.max(2, settings.populationSize()));
		gamesPerPairing.setValue(settings.gamesPerPairing());
		tournamentSize.setValue(settings.tournamentSize());
		uniformRate.setValue(settings.uniformRate());
		mutationRate.setValue(settings.mutationRate());
		elitism.setSelected(settings.elitism());
	}

	/**
	 * @return what the fields say now
	 */
	SearchSettings settings() {
		return new SearchSettings(((Number) generations.getValue()).intValue(),
				((Number) populationSize.getValue()).intValue(), ((Number) gamesPerPairing.getValue()).intValue(),
				((Number) tournamentSize.getValue()).intValue(), ((Number) uniformRate.getValue()).doubleValue(),
				((Number) mutationRate.getValue()).doubleValue(), elitism.isSelected());
	}

	/**
	 * @return roughly how many games the search will play, as the fitness of every generation and the initial
	 * population is worked out by each pair of players meeting
	 */
	static String gamesText(final SearchSettings settings) {
		final long pairs = (long) settings.populationSize() * (settings.populationSize() - 1) / 2;
		final long games = (settings.generations() + 1L) * pairs * settings.gamesPerPairing() * 2;
		return String.format(java.util.Locale.ROOT, "About %,d games for the fitness of %,d populations", games,
				settings.generations() + 1L);
	}

	private static JSpinner spinner(final SpinnerNumberModel model) {
		final JSpinner spinner = new JSpinner(model);
		spinner.setPreferredSize(new Dimension(110, 28));
		spinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		return spinner;
	}

	private static void row(final JPanel fields, final int row, final String name, final JSpinner spinner,
							final String help) {
		fields.add(MainMenuPanel.label(name, Font.BOLD, 14, MainMenuPanel.CREAM), constraints(0, row));
		fields.add(spinner, constraints(1, row));
		fields.add(MainMenuPanel.label(help, Font.PLAIN, 12, MainMenuPanel.MUTED), constraints(2, row));
	}

	private static GridBagConstraints constraints(final int column, final int row) {
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = column;
		constraints.gridy = row;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, column == 0 ? 0 : 14, 5, 0);
		return constraints;
	}
}
