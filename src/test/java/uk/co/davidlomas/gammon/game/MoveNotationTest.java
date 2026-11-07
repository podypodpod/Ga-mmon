package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.test.helpers.TestBoards;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveNotationTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
		RandomSource.reset();
	}

	@Test
	public void movesGoByTheBoardsNumbersFurthestBackFirst() {
		final Board black = moved(new Board(), true, 24, 18, 13, 9);
		assertEquals("24/18 13/9", MoveNotation.describe(new Board(), black, true, List.of(6, 4)));

		// red moves up the numbers, so its furthest back is the lowest
		final Board red = moved(new Board(), false, 12, 16, 1, 7);
		assertEquals("1/7 12/16", MoveNotation.describe(new Board(), red, false, List.of(6, 4)));
	}

	@Test
	public void aHitIsMarked() {
		final Board before = TestBoards.empty();
		before.addPiece(10, true);
		before.addPiece(20, true);
		before.addPiece(7, false);

		final Board after = moved(before, true, 10, 7, 20, 19);

		assertEquals("20/19 10/7*", MoveNotation.describe(before, after, true, List.of(3, 1)));
	}

	@Test
	public void comingInFromTheBar() {
		final Board before = TestBoards.empty();
		before.addPiece(25, true);
		before.addPiece(6, true);

		final Board after = moved(before, true, 25, 20, 20, 18);

		assertEquals("bar/20 20/18", MoveNotation.describe(before, after, true, List.of(5, 2)));
	}

	@Test
	public void redComesInFromTheBarAndBearsOffToo() {
		final Board before = TestBoards.empty();
		before.addPiece(0, false);
		before.addPiece(10, true);
		final Board in = moved(before, false, 0, 3);
		assertEquals("bar/3", MoveNotation.describe(before, in, false, List.of(3, 1)).split(" ")[0]);

		final Board home = TestBoards.empty();
		home.addPiece(23, false);
		home.setRedBore(14);
		final Board off = new Board(home);
		off.removePiece(23, false);
		off.addToBear(false);
		assertEquals("23/off", MoveNotation.describe(home, off, false, List.of(2, 1)));
	}

	@Test
	public void bearingOff() {
		final Board before = TestBoards.empty();
		before.addPiece(2, true);
		before.setBlackBore(14);
		final Board after = new Board(before);
		after.removePiece(2, true);
		after.addToBear(true);

		assertEquals("2/off", MoveNotation.describe(before, after, true, List.of(6, 1)));
	}

	@Test
	public void aDoubleMovesFourCheckers() {
		final Board after = moved(new Board(), true, 24, 18, 24, 18, 13, 7, 13, 7);

		assertEquals("24/18 24/18 13/7 13/7", MoveNotation.describe(new Board(), after, true, List.of(6, 6, 6, 6)));
	}

	@Test
	public void noMoveIsSaid() {
		assertEquals(MoveNotation.NO_MOVE, MoveNotation.describe(new Board(), new Board(), true, List.of(6, 6, 6, 6)));
	}

	@Test
	public void everyTurnOfRealGamesCanBeDescribed() {
		final List<String> notFound = new ArrayList<>();
		final int[] turns = new int[1];
		for (long seed = 1; seed <= 20; seed++) {
			RandomSource.setSeed(seed);
			final GameManager manager = new GameManager();
			manager.setObserver(new GameObserver() {
				@Override
				public void turnPlayed(final Board before, final Board board, final boolean black,
									   final List<Integer> dice) {
					turns[0]++;
					final String moves = MoveNotation.describe(before, board, black, dice);
					if (moves.contains("?")) {
						notFound.add(dice + " " + before + " -> " + board);
					}
				}
			});
			manager.playToWatch(new Individual(), new Individual());
		}

		assertTrue(turns[0] > 500, "the games should have had plenty of turns, not " + turns[0]);
		assertTrue(notFound.isEmpty(), notFound.toString());
	}

	/**
	 * @param fromTo pairs of points, each a checker to move
	 */
	private static Board moved(final Board start, final boolean black, final int... fromTo) {
		final Board board = new Board(start);
		final AiPlayer player = new AiPlayer(black, null);
		for (int i = 0; i < fromTo.length; i += 2) {
			player.movePiece(fromTo[i], fromTo[i + 1], board);
		}
		return board;
	}
}
