package uk.co.davidlomas.gammon.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.game.WinType;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.SavedPlayers.SavedPlayer;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class WatchGameFrameTest {

	private static final int LONG_ENOUGH_MILLIS = 120_000;

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void itPlaysTheGameToTheEndAndSaysWhoWon() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final SavedPlayer first = player("First");
		final SavedPlayer second = player("Second");
		final WatchGameFrame[] frame = new WatchGameFrame[1];
		SwingUtilities.invokeAndWait(() -> {
			frame[0] = new WatchGameFrame(first, second, () -> {
			}, 0);
			frame[0].start();
		});
		try {
			assertTrue(frame[0].isShowing());
			frame[0].gameThread().join(LONG_ENOUGH_MILLIS);
			assertFalse(frame[0].gameThread().isAlive(), "the game didn't finish");
			// what the game said last is queued on the Swing thread, so this is after it
			SwingUtilities.invokeAndWait(() -> {
			});

			assertTrue(frame[0].status().matches("(First|Second) won .*|A draw.*"), frame[0].status());
			final WatchGameFrame.MovesModel moves = frame[0].moves();
			assertTrue(moves.getRowCount() > 0, "each turn's moves should be listed");
			for (int row = 0; row < moves.getRowCount(); row++) {
				assertEquals(row + 1, moves.moveAt(row).turn());
				assertFalse(moves.moveAt(row).moves().contains("?"), moves.moveAt(row).toString());
			}
			// black and red take turns
			assertFalse(moves.moveAt(0).player().equals(moves.moveAt(1).player()));
			assertTrue(frame[0].isShowing(), "it should stay until it's left, to show the result");
		} finally {
			SwingUtilities.invokeAndWait(frame[0]::dispose);
		}
	}

	@Test
	public void goingBackEndsTheGameAndShowsTheMenu() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final AtomicInteger menuShown = new AtomicInteger();
		final WatchGameFrame[] frame = new WatchGameFrame[1];
		SwingUtilities.invokeAndWait(() -> {
			// so slow it would never end by itself in this test
			frame[0] = new WatchGameFrame(player("First"), player("Second"), menuShown::incrementAndGet, LONG_ENOUGH_MILLIS);
			frame[0].start();
		});

		SwingUtilities.invokeAndWait(frame[0]::back);

		assertEquals(1, menuShown.get());
		assertFalse(frame[0].isDisplayable());
		frame[0].gameThread().join(LONG_ENOUGH_MILLIS);
		assertFalse(frame[0].gameThread().isAlive(), "the game should have stopped");
	}

	@Test
	public void itSaysWhoIsPlayingAndWhereTheyAre() {
		assertEquals("Black: Attempt 1, generation 30 (+0.553 against the reference player), 167 pips to go, 0 off",
				WatchGameFrame.playerLine("Black", new SavedPlayer("Attempt 1, generation 30", new Individual(), 0.553), 167, 0));
	}

	@Test
	public void itSaysHowTheGameEnded() {
		final SavedPlayer first = player("First");
		final SavedPlayer second = player("Second");

		assertEquals("Second won " + WinType.GAMMON.describe(), WatchGameFrame.resultText(
				new GameStats(second.player(), WinType.GAMMON, -2, 2), first, second));
		assertTrue(WatchGameFrame.resultText(new GameStats(null, WinType.DRAW, 0, 0), first, second).startsWith("A draw"));
	}

	private static SavedPlayer player(final String name) {
		return new SavedPlayer(name, new Individual(), 0);
	}
}
