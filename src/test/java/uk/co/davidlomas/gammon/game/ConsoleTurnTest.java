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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
		assertEquals(2, board.getBlackCount(8));
		assertEquals(4, board.getBlackCount(6));
		assertEquals(2, board.getBlackCount(5));
		assertEquals(0, player.movesLeft.size());
		assertTrue(said().contains("you're player 2, black"));
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
		assertEquals(1, board.getBlackCount(2));
	}

	@Test
	public void bearingOffIsAMovePastTheEndOfTheBoard() {
		// black has a piece on 3 and two on 6, all home, and bears off the one on 3 with a 3
		final Board blackPosition = homeBoard();
		final Player black = person(true, "1", "3", "3");
		black.movesLeft.add(3);

		black.playRoll(blackPosition);

		assertEquals(0, blackPosition.getBlackCount(3));
		assertEquals(1, blackPosition.getBlackBore());
	}

	@Test
	public void redBearsOffPastTwentyFour() {
		// the same position for red, whose piece is on 22
		final Board redPosition = BoardMirror.mirror(homeBoard());
		final Player red = person(false, "1", "22", "3");
		red.movesLeft.add(3);

		red.playRoll(redPosition);

		assertEquals(0, redPosition.getRedCount(22));
		assertEquals(1, redPosition.getRedBore());
	}

	@Test
	public void whenNoDieCanBePlayedTheTurnEndsWithoutAsking() {
		// black is on the bar and red holds every point it could come in on
		final Board board = new Board();
		board.removeAllPieces();
		board.setBlackCount(25, 1);
		board.setBlackCount(6, 14);
		for (int point = 19; point <= 24; point++) {
			board.setRedCount(point, 2);
		}
		board.setRedCount(1, 3);
		final Player player = person(true);
		player.movesLeft.add(6);
		player.movesLeft.add(2);

		player.playRoll(board);

		assertTrue(said().contains("None of your dice can be played."));
	}

	@Test
	public void theInputEndingPartWayThroughATurnConcedes() {
		// they pick a piece, then the input ends, as with Ctrl+D or Ctrl+Z
		final Board board = new Board();
		final Player player = person(true, "1", "8");
		player.movesLeft.add(3);

		player.playRoll(board);

		assertTrue(player.hasConceded());
		assertTrue(said().contains("The input has ended, so you've conceded."));
		assertEquals(3, board.getBlackCount(8));
	}

	@Test
	public void aPieceOnTheBarComesInFromTwentyFive() {
		final Board board = new Board();
		board.setBlackCount(24, 1);
		board.setBlackCount(25, 1);
		final Player player = person(true, "1", "25", "3");
		player.movesLeft.add(3);

		player.playRoll(board);

		assertEquals(0, board.getBlackCount(25));
		assertEquals(1, board.getBlackCount(22));
	}

	@Test
	public void landingOnABlotSendsItToTheBar() {
		// red has left a piece on its own on 3
		final Board board = new Board();
		board.setRedCount(1, 1);
		board.setRedCount(3, 1);
		final Player player = person(true, "1", "6", "3");
		player.movesLeft.add(3);

		player.playRoll(board);

		assertEquals(1, board.getBlackCount(3));
		assertEquals(0, board.getRedCount(3));
		assertEquals(1, board.getRedCount(0));
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
		// at the start, the person's pieces on player 1's 24 point are in player 1's home quarter
		assertTrue(said().contains("Player 1 wins a backgammon, worth 3 points."));
		assertFalse(said().contains("You've won"));
	}

	@Test
	public void theFirstMoveIsPlayedWithTheOpeningRollsDice() {
		// over several seeds, so both players get to go first. The person concedes at their first turn
		GameSettings.setAreBothAIsCache(false);
		for (long seed = 0; seed < 20; seed++) {
			RandomSource.setSeed(seed);
			printed.reset();
			final Game game = new Game(new Individual(), new Individual());
			game.getPlayerTwo().useConsole(input("2"), new PrintStream(printed, true, StandardCharsets.UTF_8));

			game.run();

			final Matcher opening = Pattern.compile("player 1 rolled (\\d), you rolled (\\d)").matcher(said());
			assertTrue(opening.find(), "seed " + seed + ": " + said());
			final int playerOne = Integer.parseInt(opening.group(1));
			final int you = Integer.parseInt(opening.group(2));
			assertNotEquals(playerOne, you, "a tied opening roll is rolled again");

			// the first dice anyone plays: the AI's if it won the opening roll, the person's if they did
			final Matcher firstDice = Pattern.compile(playerOne > you ? "rolled \\[(\\d), (\\d)]" : "Dice left: \\[(\\d), (\\d)]")
					.matcher(said());
			assertTrue(firstDice.find(), "seed " + seed + ": " + said());
			assertEquals(List.of(playerOne, you),
					List.of(Integer.parseInt(firstDice.group(1)), Integer.parseInt(firstDice.group(2))), "seed " + seed);
		}
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
		home.removeAllPieces();
		home.setBlackCount(3, 1);
		home.setBlackCount(6, 2);
		home.setRedCount(20, 2);
		return home;
	}
}
