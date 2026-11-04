package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * The benchmark's fixed opponent, on positions set up for black and on their mirror images for red
 */
public class ReferencePlayerTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void itPlaysAWinningMove(final boolean black) {
		final Board notYet = empty();
		notYet.setBlackCount(1, 1);
		notYet.setBlackBore(14);
		notYet.setRedCount(20, 15);
		final Board won = empty();
		won.setBlackBore(15);
		won.setRedCount(20, 15);

		final Board winning = forColour(won, black);
		assertSame(winning, ReferencePlayer.choose(List.of(forColour(notYet, black), winning), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void itMakesAPointRatherThanLeaveABlotToBeHit(final boolean black) {
		// the same pips either way: one on 7 and one on 13, with red on 3 four short of the 7, or two on 10
		final Board blots = empty();
		blots.setBlackCount(7, 1);
		blots.setBlackCount(13, 1);
		blots.setRedCount(3, 2);
		final Board point = empty();
		point.setBlackCount(10, 2);
		point.setRedCount(3, 2);

		final Board safe = forColour(point, black);
		assertSame(safe, ReferencePlayer.choose(List.of(forColour(blots, black), safe), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void inARaceItBearsOffRatherThanMoveTheSamePips(final boolean black) {
		// red is all behind black, so it's a race. A 3 bears the piece on 3 off, or moves the piece on 5 to 2
		final Board moved = empty();
		moved.setBlackCount(3, 1);
		moved.setBlackCount(2, 1);
		moved.setBlackBore(13);
		moved.setRedCount(20, 15);
		final Board borne = empty();
		borne.setBlackCount(5, 1);
		borne.setBlackBore(14);
		borne.setRedCount(20, 15);

		final Board off = forColour(borne, black);
		assertSame(off, ReferencePlayer.choose(List.of(forColour(moved, black), off), black));
	}

	@Test
	public void withNoMovesThereIsNothingToPlay() {
		assertNull(ReferencePlayer.choose(List.of(), true));
	}

	@Test
	public void itPlaysWholeGamesAgainstAnEvolvedPlayer() {
		assertNotNull(new GameManager().playIndividualsVsEachOther(new ReferencePlayer(), new Individual()));
	}

	private static Board empty() {
		final Board board = new Board();
		board.removeAllPieces();
		return board;
	}

	private static Board forColour(final Board blackPosition, final boolean black) {
		return black ? blackPosition : BoardMirror.mirror(blackPosition);
	}
}
