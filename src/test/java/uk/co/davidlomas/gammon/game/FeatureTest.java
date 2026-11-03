package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Each feature, measured on a position set up for black and on its mirror image for red, which has to give the same
 */
public class FeatureTest {

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
	public void theStartingPositionMeasuresTheSameForBothPlayers(final boolean black) {
		final Board start = forColour(new Board(), black);

		assertEquals(0, Feature.PIP_LEAD.measure(start, black));
		assertEquals(0, Feature.BLOTS.measure(start, black));
		// 24, 13, 8 and 6
		assertEquals(4, Feature.POINTS_MADE.measure(start, black));
		assertEquals(1, Feature.HOME_POINTS.measure(start, black));
		assertEquals(1, Feature.ANCHORS.measure(start, black));
		assertEquals(2, Feature.PIECES_BACK.measure(start, black));
		// five on the 13 and five on the 6, two over three each
		assertEquals(4, Feature.STACKING.measure(start, black));
		assertTrue(Feature.isContact(start));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void movingAheadGivesAPipLead(final boolean black) {
		final Board blackPosition = new Board();
		// 13 to 7, six pips
		blackPosition.Points[13].setBlackCount(4);
		blackPosition.Points[7].setBlackCount(1);

		assertEquals(6, Feature.PIP_LEAD.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aBlotFourAwayIsHitByFifteenRolls(final boolean black) {
		// any 4, then 3-1 and 1-3, 2-2 and 1-1
		final Board blackPosition = empty();
		blackPosition.Points[7].setBlackCount(1);
		blackPosition.Points[3].setRedCount(2);

		final Board board = forColour(blackPosition, black);
		assertEquals(1, Feature.BLOTS.measure(board, black));
		assertEquals(15, Feature.HIT_ROLLS.measure(board, black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aBlotEightAwayIsHitBySixRolls(final boolean black) {
		// 6-2, 2-6, 5-3, 3-5, 4-4 and 2-2: it needs both dice
		final Board blackPosition = empty();
		blackPosition.Points[11].setBlackCount(1);
		blackPosition.Points[3].setRedCount(2);

		assertEquals(6, Feature.HIT_ROLLS.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void pointsMadeInTheWayBlockShotsThatNeedBothDice(final boolean black) {
		// the blot on 9 is 6 from red on 3, and black holds 4 to 8, so only a 6 itself hits: 11 rolls
		final Board blackPosition = empty();
		blackPosition.Points[9].setBlackCount(1);
		for (int point = 4; point <= 8; point++) {
			blackPosition.Points[point].setBlackCount(2);
		}
		blackPosition.Points[3].setRedCount(2);

		assertEquals(11, Feature.HIT_ROLLS.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void anOpponentOnTheBarHasToComeInBeforeAnotherPieceCanHit(final boolean black) {
		// red has a piece on the bar and one on 7, 3 short of black's blot on 10. Black holds 1, 3, 4, 5 and 6, so red
		// only comes in with a 2, and then the piece on 7 hits with a 3: 2-3 and 3-2. Without the bar piece, any 3
		// would hit, and 2-1, 1-2 and 1-1 too
		final Board blackPosition = empty();
		blackPosition.Points[10].setBlackCount(1);
		for (final int point : new int[]{1, 3, 4, 5, 6}) {
			blackPosition.Points[point].setBlackCount(2);
		}
		blackPosition.Points[7].setRedCount(1);
		blackPosition.Points[0].setRedCount(1);

		assertEquals(2, Feature.HIT_ROLLS.measure(forColour(blackPosition, black), black));

		blackPosition.Points[0].setRedCount(0);
		assertEquals(14, Feature.HIT_ROLLS.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aBlotOutOfReachIsSafe(final boolean black) {
		final Board blackPosition = empty();
		blackPosition.Points[20].setBlackCount(1);
		blackPosition.Points[3].setRedCount(2);

		assertEquals(0, Feature.HIT_ROLLS.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPieceOnTheBarCanHitABlotOnItsWayIn(final boolean black) {
		final Board blackPosition = empty();
		blackPosition.Points[5].setBlackCount(1);
		// red's bar: it comes in on 1 to 6
		blackPosition.Points[0].setRedCount(1);

		// 5 away: any 5, then 4-1, 1-4, 3-2 and 2-3
		final Board board = forColour(blackPosition, black);
		assertEquals(15, Feature.HIT_ROLLS.measure(board, black));
		assertEquals(1, Feature.OPPONENT_ON_BAR.measure(board, black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void thePrimeIsTheLongestRunOfPointsMade(final boolean black) {
		final Board blackPosition = empty();
		for (final int point : new int[]{4, 5, 6, 7, 9, 10}) {
			blackPosition.Points[point].setBlackCount(2);
		}

		assertEquals(4, Feature.LONGEST_PRIME.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void borneOffCountsThePlayersPiecesOff(final boolean black) {
		final Board blackPosition = empty();
		blackPosition.Points[3].setBlackCount(12);
		blackPosition.setBlackBore(3);

		assertEquals(3, Feature.BORNE_OFF.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void onceThePlayersHavePassedEachOtherItsARace(final boolean black) {
		final Board blackPosition = empty();
		blackPosition.Points[9].setBlackCount(15);
		blackPosition.Points[10].setRedCount(15);
		assertFalse(Feature.isContact(forColour(blackPosition, black)));

		// one red piece left behind black's makes it contact again
		blackPosition.Points[10].setRedCount(14);
		blackPosition.Points[2].setRedCount(1);
		assertTrue(Feature.isContact(forColour(blackPosition, black)));
	}

	private static Board empty() {
		final Board board = new Board();
		for (final Point point : board.Points) {
			point.setBlackCount(0);
			point.setRedCount(0);
		}
		return board;
	}

	private static Board forColour(final Board blackPosition, final boolean black) {
		return black ? blackPosition : BoardMirror.mirror(blackPosition);
	}
}
