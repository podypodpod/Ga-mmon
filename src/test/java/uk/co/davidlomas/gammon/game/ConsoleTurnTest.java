package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Plays a person's turn on the console with scripted input, one number per line, and checks what it does and says
 */
public class ConsoleTurnTest {

	private final ByteArrayOutputStream printed = new ByteArrayOutputStream();

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@AfterEach
	public void tearDown() {
		GameSettings.setAreBothAIsCache(true);
		RandomSource.reset();
	}

	@Test
	public void thePersonMovesAPieceWithEachDie() {
		final Board board = new Board();
		final Player player = person(true, "1", "8", "3", "1", "6", "1");
		player.movesLeft.add(3);
		player.movesLeft.add(1);

		player.playRoll(board);

		// 8 to 5 with the 3, then 6 to 5 with the 1
		assertEquals(2, board.Points[8].getBlackCount());
		assertEquals(4, board.Points[6].getBlackCount());
		assertEquals(2, board.Points[5].getBlackCount());
		assertEquals(0, player.movesLeft.size());
		assertTrue(said().contains("you're black"));
		assertTrue(said().contains("That's all your dice."));
	}

	@Test
	public void aMistakeIsExplainedAndAskedAgain() {
		final Board board = new Board();
		// not a number, a die they don't have, a menu option that doesn't exist, then a real move
		final Player player = person(true, "x", "1", "6", "5", "3", "1", "6", "4");
		player.movesLeft.add(4);

		player.playRoll(board);

		assertTrue(said().contains("Type a number."));
		assertTrue(said().contains("That move isn't allowed."));
		assertTrue(said().contains("Type 1 or 2."));
		assertEquals(1, board.Points[2].getBlackCount());
	}

	@Test
	public void bearingOffIsAMovePastTheEndOfTheBoard() {
		// black has a piece on 3 and two on 6, all home, and bears off the one on 3 with a 3
		final Board blackPosition = homeBoard();
		final Player black = person(true, "1", "3", "3");
		black.movesLeft.add(3);

		black.playRoll(blackPosition);

		assertEquals(0, blackPosition.Points[3].getBlackCount());
		assertEquals(1, blackPosition.getBlackBore());
	}

	@Test
	public void redBearsOffPastTwentyFour() {
		// the same position for red, whose piece is on 22
		final Board redPosition = BoardMirror.mirror(homeBoard());
		final Player red = person(false, "1", "22", "3");
		red.movesLeft.add(3);

		red.playRoll(redPosition);

		assertEquals(0, redPosition.Points[22].getRedCount());
		assertEquals(1, redPosition.getRedBore());
	}

	@Test
	public void whenNoDieCanBePlayedTheTurnEndsWithoutAsking() {
		// black is on the bar and red holds every point it could come in on
		final Board board = new Board();
		for (final Point point : board.Points) {
			point.setBlackCount(0);
			point.setRedCount(0);
		}
		board.Points[25].setBlackCount(1);
		board.Points[6].setBlackCount(14);
		for (int point = 19; point <= 24; point++) {
			board.Points[point].setRedCount(2);
		}
		board.Points[1].setRedCount(3);
		final Player player = person(true);
		player.movesLeft.add(6);
		player.movesLeft.add(2);

		player.playRoll(board);

		assertTrue(said().contains("None of your dice can be played."));
	}

	@Test
	public void runningOutOfInputPartWayThroughATurnIsAnError() {
		final Player player = person(true, "1", "8");
		player.movesLeft.add(3);

		assertThrows(IllegalStateException.class, () -> player.playRoll(new Board()));
	}

	@Test
	public void concedingHandsTheGameToPlayerOne() {
		// a person plays player 2 and concedes at their first turn
		GameSettings.setAreBothAIsCache(false);
		RandomSource.setSeed(1);
		final Individual playerOne = new Individual();
		final Game game = new Game(playerOne, new Individual());
		game.getPlayerTwo().useConsole(input("2"), new PrintStream(printed, true, StandardCharsets.UTF_8));

		game.run();

		assertTrue(game.getPlayerTwo().hasConceded());
		assertSame(playerOne, game.getGameStats().getVictor());
		assertTrue(said().contains("You conceded."));
		assertFalse(said().contains("You've won!"));
	}

	/**
	 * A person playing the colour, who types these lines
	 */
	private Player person(final boolean black, final String... lines) {
		final Player player = new Player(black);
		player.useConsole(input(lines), new PrintStream(printed, true, StandardCharsets.UTF_8));
		return player;
	}

	private static ByteArrayInputStream input(final String... lines) {
		return new ByteArrayInputStream((String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8));
	}

	private String said() {
		return printed.toString(StandardCharsets.UTF_8);
	}

	/**
	 * Every black piece is home, one on 3 and two on 6, and red has two on 20
	 */
	private static Board homeBoard() {
		final Board home = new Board();
		for (final Point point : home.Points) {
			point.setBlackCount(0);
			point.setRedCount(0);
		}
		home.Points[3].setBlackCount(1);
		home.Points[6].setBlackCount(2);
		home.Points[20].setRedCount(2);
		return home;
	}
}
