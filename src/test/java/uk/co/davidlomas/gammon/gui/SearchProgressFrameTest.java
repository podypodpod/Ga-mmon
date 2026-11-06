package uk.co.davidlomas.gammon.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.SearchProgress;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class SearchProgressFrameTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void goingBackLeavesItToReturnToWhileTheSearchRuns() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final SearchProgress progress = new SearchProgress();
		final AtomicInteger menuShown = new AtomicInteger();
		final SearchProgressFrame[] frame = new SearchProgressFrame[1];
		SwingUtilities.invokeAndWait(() -> {
			frame[0] = new SearchProgressFrame(progress, menuShown::incrementAndGet);
			frame[0].open();
		});
		try {
			assertTrue(frame[0].isShowing());
			// closing it has to bring the menu back rather than leave nothing showing
			assertEquals(WindowConstants.DO_NOTHING_ON_CLOSE, frame[0].getDefaultCloseOperation());

			SwingUtilities.invokeAndWait(frame[0]::back);
			assertEquals(1, menuShown.get());
			assertFalse(frame[0].isVisible());
			assertTrue(frame[0].isDisplayable(), "it should still be there to come back to");

			SwingUtilities.invokeAndWait(frame[0]::open);
			assertTrue(frame[0].isShowing());

			// with the search over, there's nothing to come back to
			progress.finished();
			SwingUtilities.invokeAndWait(frame[0]::refresh);
			SwingUtilities.invokeAndWait(frame[0]::back);
			assertEquals(2, menuShown.get());
			assertFalse(frame[0].isDisplayable(), "it should have gone, or the program would never end");
		} finally {
			SwingUtilities.invokeAndWait(frame[0]::dispose);
		}
	}

	@Test
	public void itSaysHowTheSearchIsGoing() {
		final SearchProgress progress = new SearchProgress();
		progress.stageStarted("Generation 3 of 50");
		assertEquals("Generation 3 of 50", SearchProgressFrame.statusText(progress));

		progress.finished();
		assertEquals("Finished", SearchProgressFrame.statusText(progress));
		progress.savingIn("somewhere/Attempt - 3");
		assertEquals("Finished: the players are saved in somewhere/Attempt - 3", SearchProgressFrame.statusText(progress));

		final SearchProgress failing = new SearchProgress();
		failing.failed();
		assertTrue(SearchProgressFrame.statusText(failing).contains("error"));
	}

	@Test
	public void theWindowIsCutDownToFitTheScreen() {
		assertEquals(new Dimension(1000, 890),
				SearchProgressFrame.fitted(new Dimension(1000, 890), new Rectangle(0, 0, 1920, 1040)));
		assertEquals(new Dimension(1000, 728),
				SearchProgressFrame.fitted(new Dimension(1000, 890), new Rectangle(0, 0, 1366, 728)));
	}

	@Test
	public void timesAndSharesAreWrittenSimply() {
		assertEquals("0:05", SearchProgressFrame.minutesAndSeconds(5.9));
		assertEquals("2:03", SearchProgressFrame.minutesAndSeconds(123));
		assertEquals("12.5%", SearchProgressFrame.percent(0.125));
	}

	@Test
	public void theGamesTableHasARowForEachPlaceAGameCanBePlaying() {
		final Individual first = new Individual();
		final Individual second = new Individual();
		final SearchProgress progress = new SearchProgress();
		progress.roundStarted(SearchProgress.Round.FITNESS, 4, 2);
		final SearchProgressFrame.LiveGamesModel model = new SearchProgressFrame.LiveGamesModel();

		try (SearchProgress.MeetingWatch watch = progress.watchMeeting(first, "Player 1", "Player 2")) {
			watch.gameStarted(first, second, true);
			watch.turnPlayed(new Board());
			model.show(progress.getLiveGames());
		}

		assertEquals(2, model.getRowCount());
		assertEquals("Player 1", model.getValueAt(0, 0));
		assertEquals("Player 2", model.getValueAt(0, 1));
		assertEquals(1, model.getValueAt(0, 2));
		assertEquals(167, model.getValueAt(0, 3));
		assertEquals("Between games", model.getValueAt(1, 0));
		assertEquals("", model.getValueAt(1, 2));
	}

	@Test
	public void theGenerationsTableShowsTheNewestFirst() {
		final SearchProgressFrame.GenerationsModel model = new SearchProgressFrame.GenerationsModel();

		model.show(List.of(result("initial", null), result("0", 0.5)));

		assertEquals(2, model.getRowCount());
		assertEquals("0", model.getValueAt(0, 0));
		assertEquals("+0.500", model.getValueAt(0, 8));
		assertEquals("initial", model.getValueAt(1, 0));
		assertEquals("", model.getValueAt(1, 8));
		assertEquals("25.0%", model.getValueAt(0, 4));
		assertEquals(Arrays.asList("Generation", "Best fitness"),
				Arrays.asList(model.getColumnName(0), model.getColumnName(1)));
	}

	private static SearchProgress.GenerationResult result(final String generation, final Double points) {
		return new SearchProgress.GenerationResult(generation, 1.5, 10, 40, 0.25, 0.5, 0.2, 0.05, points);
	}
}
