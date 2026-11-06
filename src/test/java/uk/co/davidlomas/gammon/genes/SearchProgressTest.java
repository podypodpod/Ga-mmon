package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.game.WinType;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchProgressTest {

	@BeforeEach
	public void setUp() {
		SettingsUtil.resetSettings();
	}

	@AfterEach
	public void tearDown() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void aWholeSearchIsFollowedFromStartToEnd() {
		GenAlgSettings.setPopulationSize(2);
		GenAlgSettings.setGenerations(1);
		GenAlgSettings.setGamesPerPairing(1);
		final SearchProgress progress = new SearchProgress();

		StartGa.run(progress);

		assertEquals(SearchProgress.State.FINISHED, progress.getState());
		assertEquals(1.0, progress.getShareDone());
		assertEquals(1, progress.getGenerations());
		// two rounds of one meeting, and the best against the reference player after each, two games a meeting
		assertEquals(2 * (1 + RunStats.REFERENCE_MEETINGS) * 2, progress.getGames());
		final List<SearchProgress.GenerationResult> results = progress.getResults();
		assertEquals(List.of("initial", "0"), results.stream().map(SearchProgress.GenerationResult::generation).toList());
		assertNotNull(results.get(1).pointsAgainstReference());
		assertTrue(progress.getLiveGames().stream().allMatch(game -> game == null), "no games should still be showing");
		assertTrue(progress.getSavedIn().contains("Attempt"), progress.getSavedIn());
	}

	@Test
	public void aSearchThatFailsSaysSo() {
		GenAlgSettings.setPopulationSize(0);
		final SearchProgress progress = new SearchProgress();

		assertThrows(IllegalArgumentException.class, () -> StartGa.run(progress));

		assertEquals(SearchProgress.State.FAILED, progress.getState());
	}

	@Test
	public void aFinishedRoundOnlyCountsOnceTowardsTheShareDone() {
		final Individual first = new Individual();
		final Individual second = new Individual();
		final SearchProgress progress = new SearchProgress();
		// the initial population and one generation, so two stages
		progress.searchStarted(1);
		progress.roundStarted(SearchProgress.Round.FITNESS, 2, 1);
		assertEquals(0.0, progress.getShareDone());

		try (SearchProgress.MeetingWatch watch = progress.watchMeeting(first, "Player 1", "Player 2")) {
			final GameStats game = new GameStats(first, WinType.SINGLE, 1, -1);
			watch.gameFinished(game);
			assertEquals(0.25, progress.getShareDone());
			watch.gameFinished(game);
			assertEquals(0.5, progress.getShareDone());
		}

		progress.generationFinished(new SearchProgress.GenerationResult("initial", 0, 0, 0, 0, 0, 0, 0, null));
		assertEquals(0.5, progress.getShareDone());
	}

	@Test
	public void turnsAreCountedEvenWhenTheGameDoesntCountThem() {
		final Individual first = new Individual();
		final SearchProgress progress = new SearchProgress();
		progress.roundStarted(SearchProgress.Round.FITNESS, 1, 1);

		try (SearchProgress.MeetingWatch watch = progress.watchMeeting(first, "Player 1", "Player 2")) {
			watch.gameStarted(first, new Individual(), true);
			watch.turnPlayed(new Board());
			watch.turnPlayed(new Board());
			// as a game with a person reports it, with no turns of its own
			watch.gameFinished(new GameStats(first, WinType.SINGLE, 1, -1));
		}

		assertEquals(2.0, progress.getTurnsPerGame());
	}

	@Test
	public void aMeetingShowsInItsSlotUntilItEnds() {
		final Individual first = new Individual();
		final Individual second = new Individual();
		final SearchProgress progress = new SearchProgress();
		progress.roundStarted(SearchProgress.Round.FITNESS, 2, 2);

		try (SearchProgress.MeetingWatch watch = progress.watchMeeting(first, "Player 1", "Player 2")) {
			watch.gameStarted(second, first, true);
			watch.turnPlayed(new Board());
			watch.turnPlayed(new Board());

			final SearchProgress.LiveGame game = progress.getLiveGames().get(0);
			assertEquals("Player 2", game.black());
			assertEquals("Player 1", game.red());
			assertEquals(2, game.turns());
			assertEquals(167, game.blackPips());
			assertEquals(167, game.redPips());
			assertNull(progress.getLiveGames().get(1));
		}
		assertEquals(Arrays.asList(null, null), progress.getLiveGames());

		// the slot can be used again
		progress.watchMeeting(first, "Player 1", "Player 2").close();
		progress.watchMeeting(first, "Player 1", "Player 2").close();
		progress.watchMeeting(first, "Player 1", "Player 2").close();
	}
}
