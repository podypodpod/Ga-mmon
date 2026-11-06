package uk.co.davidlomas.gammon.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.genes.SearchProgress;
import uk.co.davidlomas.gammon.genes.StartGa;
import uk.co.davidlomas.gammon.settings.GameSettings;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class MainMenuPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final static Logger logger = LoggerFactory.getLogger(MainMenuPanel.class);

	static final Color FELT = new Color(0x1F4D3A);
	static final Color CREAM = new Color(0xF4EAD5);
	static final Color MUTED = new Color(0x9DB5A6);
	static final Color ACCENT = new Color(0xB5533C);

	/** The points of a backgammon board, along the top and bottom of the menu */
	private static final int POINTS_ALONG_AN_EDGE = 12;
	private static final int POINT_HEIGHT = 64;

	private static final String NOT_SEARCHING = " ";
	private static final String SEARCHING = "A search is running";

	private final JLabel status = label(NOT_SEARCHING, Font.PLAIN, 14, MUTED);
	private final JButton backToSearch = new MenuButton("Back to Search");
	private SearchProgressFrame progressWindow;

	public MainMenuPanel() {
		this(MainMenuPanel::startSearch);
	}

	/**
	 * @param startSearch starts a search from this menu, and is given what to run when it ends. Tests pass their own,
	 *                    so they can see the button pressed without running the GA
	 */
	MainMenuPanel(final BiConsumer<MainMenuPanel, Runnable> startSearch) {
		setLayout(new GridBagLayout());
		setBackground(FELT);

		add(label("GA-mmon", Font.BOLD, 44, CREAM), row(0, 0));
		add(label("Backgammon players, bred by a genetic algorithm", Font.PLAIN, 14, MUTED), row(1, 4));

		final JButton startSearchButton = new MenuButton("Start Search");

		// one search at a time: they share the settings and the attempt count, so two would save over each other
		startSearchButton.addActionListener(e -> {
			startSearchButton.setEnabled(false);
			status.setText(SEARCHING);
			startSearch.accept(this, () -> {
				startSearchButton.setEnabled(true);
				status.setText(NOT_SEARCHING);
				searchEnded();
			});
		});

		// only there while a search is running, for getting back to its window
		backToSearch.setVisible(false);
		backToSearch.addActionListener(e -> showProgressWindow());

		add(startSearchButton, row(2, 28));
		add(backToSearch, row(3, 12));
		add(notBuiltYet("Play Best Two"), row(4, 12));
		add(notBuiltYet("Change Settings"), row(5, 12));
		add(status, row(6, 18));
	}

	static JLabel label(final String text, final int style, final int size, final Color colour) {
		final JLabel label = new JLabel(text);
		label.setFont(new Font(Font.SANS_SERIF, style, size));
		label.setForeground(colour);
		return label;
	}

	private static JButton notBuiltYet(final String text) {
		final JButton button = new MenuButton(text);
		button.setEnabled(false);
		button.setToolTipText("Not built yet");
		return button;
	}

	private static GridBagConstraints row(final int row, final int gapAbove) {
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = row;
		constraints.insets = new Insets(gapAbove, 0, 0, 0);
		return constraints;
	}

	/**
	 * @return what the menu is saying about the search
	 */
	String status() {
		return status.getText();
	}

	@Override
	protected void paintComponent(final Graphics graphics) {
		super.paintComponent(graphics);
		final Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int point = 0; point < POINTS_ALONG_AN_EDGE; point++) {
			final int left = getWidth() * point / POINTS_ALONG_AN_EDGE;
			final int right = getWidth() * (point + 1) / POINTS_ALONG_AN_EDGE;
			final int middle = (left + right) / 2;
			g.setColor(point % 2 == 0 ? ACCENT : CREAM);
			g.fillPolygon(new int[]{left, right, middle}, new int[]{0, 0, POINT_HEIGHT}, 3);
			// the other way round along the bottom, as on a board
			g.setColor(point % 2 == 0 ? CREAM : ACCENT);
			g.fillPolygon(new int[]{left, right, middle},
					new int[]{getHeight(), getHeight(), getHeight() - POINT_HEIGHT}, 3);
		}
		g.dispose();
	}

	/**
	 * Runs the genetic algorithm on its own thread, and swaps the menu for a window showing how it's going. If the
	 * search fails, the error is shown in a message box rather than only in the log
	 *
	 * @param whenDone run on the Swing thread once the search has finished or failed
	 */
	private void startSearch(final Runnable whenDone) {
		final SearchProgress progress = new SearchProgress();
		progressWindow = new SearchProgressFrame(progress, this::showMenu);
		backToSearch.setVisible(true);
		showProgressWindow();
		searchThread(progress, whenDone).start();
	}

	/**
	 * @return the window for the latest search, null if there hasn't been one
	 */
	SearchProgressFrame progressWindow() {
		return progressWindow;
	}

	private void showProgressWindow() {
		progressWindow.open();
		setMenuVisible(false);
	}

	private void showMenu() {
		setMenuVisible(true);
	}

	private void setMenuVisible(final boolean visible) {
		final Window menu = SwingUtilities.getWindowAncestor(this);
		if (menu != null) {
			menu.setVisible(visible);
		}
	}

	/**
	 * There's no search to get back to now. A window that's been left hidden has to go, or the program would never end
	 */
	private void searchEnded() {
		backToSearch.setVisible(false);
		if (progressWindow != null && !progressWindow.isVisible()) {
			progressWindow.dispose();
		}
	}

	/**
	 * The thread a search runs on, not yet started, so tests can make it a daemon before starting it. The real one
	 * isn't a daemon, so the program waits for a search to finish rather than cutting it off mid-save
	 *
	 * @param progress kept up to date as the search goes
	 * @param whenDone run on the Swing thread once the search has finished or failed
	 *
	 * @return the thread, not started
	 */
	static Thread searchThread(final SearchProgress progress, final Runnable whenDone) {
		return new Thread(() -> {
			try {
				markingFailure(progress, () -> {
					if (waitsForAPerson()) {
						showMessage("areBothAIs is off in conf/GameSettings.properties, so this search will wait for you "
								+ "to play player 2 in the terminal.", JOptionPane.INFORMATION_MESSAGE);
					}
					StartGa.run(progress);
				});
			} catch (final Throwable e) {
				// any failure is reported, including Errors such as a settings class failing to load
				logger.error("The search failed", e);
				showMessage("The search failed: " + e, JOptionPane.ERROR_MESSAGE);
				if (e instanceof Error) {
					throw (Error) e;
				}
			} finally {
				SwingUtilities.invokeLater(whenDone);
			}
		}, "GA search");
	}

	/**
	 * Runs search, marking progress as failed if it does, even if that's before the search itself has started, such as
	 * a settings file with a mistake in it. Otherwise the window would never know the search had ended, and it would
	 * stay open with nothing to end it
	 */
	static void markingFailure(final SearchProgress progress, final Runnable search) {
		try {
			search.run();
		} catch (final RuntimeException | Error e) {
			progress.failed();
			throw e;
		}
	}

	/**
	 * @return whether a search would stop to ask for moves on the console, which the window doesn't show
	 */
	static boolean waitsForAPerson() {
		return !GameSettings.getAreBothAI();
	}

	private static void showMessage(final String message, final int type) {
		if (!GraphicsEnvironment.isHeadless()) {
			SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, "GA-mmon", type));
		}
	}
}
