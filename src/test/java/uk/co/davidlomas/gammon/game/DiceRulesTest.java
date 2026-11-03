package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The rules for using the dice, for a person on the console and for the AI: use as many of the dice as you can, and
 * if only one of two different dice can be used, use the larger. Each position is set up for black and mirrored for
 * red, where point p is 25 - p, and the roll is a 6 and a 1.
 */
public class DiceRulesTest {

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
		RandomSource.reset();
	}

	/**
	 * Black has pieces on 10 and 8, and red holds 3 and 2. Playing the 1 from 10 to 9 is allowed on its own, but then
	 * the 6 has nowhere to go, while 10 to 4 and 8 to 7 uses both dice.
	 */
	private static Board bothDiceCanBeUsed() {
		final Board board = emptyBoard();
		board.Points[10].setBlackCount(1);
		board.Points[8].setBlackCount(1);
		board.Points[3].setRedCount(2);
		board.Points[2].setRedCount(2);
		return board;
	}

	/**
	 * Black has pieces on 10 and 2, and red holds 3 and 1. Whichever die black plays, the other can't be used after
	 * it, so only one can be, and it has to be the 6: 10 to 4.
	 */
	private static Board onlyOneDieCanBeUsed() {
		final Board board = emptyBoard();
		board.Points[10].setBlackCount(1);
		board.Points[2].setBlackCount(1);
		board.Points[3].setRedCount(2);
		board.Points[1].setRedCount(2);
		return board;
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPersonCantPlayADieThatStopsThemUsingTheOther(final boolean black) {
		final Board board = forColour(bothDiceCanBeUsed(), black);
		// the 1 from 10, refused, then the 6 from 10 and the 1 from 8
		final ByteArrayOutputStream printed = new ByteArrayOutputStream();
		final Player player = person(black, printed, "1", point(10, black), "1", "1", point(10, black), "6", "1",
				point(8, black), "1");

		player.playRoll(board);

		assertTrue(printed.toString(StandardCharsets.UTF_8).contains("you have to use as many as you can"));
		assertEquals(7, pipsMoved(bothDiceCanBeUsed(), board, black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPersonWhoCanOnlyUseOneDieHasToUseTheLarger(final boolean black) {
		final Board board = forColour(onlyOneDieCanBeUsed(), black);
		// the 1 from 10, refused, then the 6 from 10
		final ByteArrayOutputStream printed = new ByteArrayOutputStream();
		final Player player = person(black, printed, "1", point(10, black), "1", "1", point(10, black), "6");

		player.playRoll(board);

		assertTrue(printed.toString(StandardCharsets.UTF_8).contains("it has to be the larger one, the 6"));
		assertEquals(6, pipsMoved(onlyOneDieCanBeUsed(), board, black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void theAiUsesBothDiceWhenItCan(final boolean black) {
		// all its genes at 0, so it picks at random from the moves it's allowed, over several seeds
		for (long seed = 0; seed < 20; seed++) {
			RandomSource.setSeed(seed);

			final Board chosen = aiMove(forColour(bothDiceCanBeUsed(), black), black);

			assertEquals(7, pipsMoved(bothDiceCanBeUsed(), chosen, black), "seed " + seed);
		}
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void theAiThatCanOnlyUseOneDieUsesTheLarger(final boolean black) {
		for (long seed = 0; seed < 20; seed++) {
			RandomSource.setSeed(seed);

			final Board chosen = aiMove(forColour(onlyOneDieCanBeUsed(), black), black);

			assertEquals(6, pipsMoved(onlyOneDieCanBeUsed(), chosen, black), "seed " + seed);
		}
	}

	private static Board aiMove(final Board board, final boolean black) {
		final Individual individual = new Individual();
		for (int i = 0; i < individual.getNumOfAttributes(); i++) {
			individual.getAttribute(i).setValue(0);
		}
		final AiPlayer player = new AiPlayer(black, individual);
		player.movesLeft.add(6);
		player.movesLeft.add(1);
		return new MoveGenerator(individual).getNextMoveBoard(board, player);
	}

	/**
	 * How far the player's pieces moved between black's starting position and the board after, for either colour
	 */
	private static int pipsMoved(final Board blackStart, final Board after, final boolean black) {
		final Board start = forColour(blackStart, black);
		return pips(start, black) - pips(after, black);
	}

	private static int pips(final Board board, final boolean black) {
		int pips = 0;
		for (int point = 0; point <= 25; point++) {
			pips += black ? point * board.Points[point].getBlackCount() : (25 - point) * board.Points[point].getRedCount();
		}
		return pips;
	}

	private static Player person(final boolean black, final ByteArrayOutputStream printed, final String... lines) {
		final Player player = new Player(black);
		player.useConsole(new ByteArrayInputStream((String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8)),
				new PrintStream(printed, true, StandardCharsets.UTF_8));
		player.movesLeft.add(6);
		player.movesLeft.add(1);
		return player;
	}

	/**
	 * Black's point, or the same point for red
	 */
	private static String point(final int blackPoint, final boolean black) {
		return String.valueOf(black ? blackPoint : 25 - blackPoint);
	}

	private static Board forColour(final Board blackPosition, final boolean black) {
		return black ? blackPosition : BoardMirror.mirror(blackPosition);
	}

	private static Board emptyBoard() {
		final Board board = new Board();
		for (final Point point : board.Points) {
			point.setBlackCount(0);
			point.setRedCount(0);
		}
		return board;
	}
}
