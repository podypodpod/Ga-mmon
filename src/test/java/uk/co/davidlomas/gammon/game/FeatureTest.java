package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.test.helpers.TestBoards;

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
		blackPosition.setBlackCount(13, 4);
		blackPosition.setBlackCount(7, 1);

		assertEquals(6, Feature.PIP_LEAD.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aBlotWithinSixOfAnOpponentsPieceIsAtRisk(final boolean black) {
		// red on 3 is 4 short of the blot on 7, but 17 short of the one on 20
		final Board blackPosition = TestBoards.empty();
		blackPosition.setBlackCount(7, 1);
		blackPosition.setBlackCount(20, 1);
		blackPosition.setRedCount(3, 2);

		final Board board = forColour(blackPosition, black);
		assertEquals(2, Feature.BLOTS.measure(board, black));
		assertEquals(18, Feature.PIPS_AT_RISK.measure(board, black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aBlotSevenAwayIsNotAtRisk(final boolean black) {
		// it would need both dice, which isn't counted
		final Board blackPosition = TestBoards.empty();
		blackPosition.setBlackCount(10, 1);
		blackPosition.setRedCount(3, 2);

		assertEquals(0, Feature.PIPS_AT_RISK.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aBlotAnOpponentHasPassedIsNotAtRisk(final boolean black) {
		// red moves up, away from the blot on 3
		final Board blackPosition = TestBoards.empty();
		blackPosition.setBlackCount(3, 1);
		blackPosition.setRedCount(5, 2);

		assertEquals(0, Feature.PIPS_AT_RISK.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPieceOnTheBarPutsBlotsInThePlayersHomeAtRisk(final boolean black) {
		final Board blackPosition = TestBoards.empty();
		blackPosition.setBlackCount(5, 1);
		// red's bar: it comes in on 1 to 6
		blackPosition.setRedCount(0, 1);

		final Board board = forColour(blackPosition, black);
		assertEquals(20, Feature.PIPS_AT_RISK.measure(board, black));
		assertEquals(1, Feature.OPPONENT_ON_BAR.measure(board, black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void pipsAtRiskAddUpWhatTheExposedBlotsWouldLose(final boolean black) {
		// red on 3 exposes the blots on 7, which would lose 18 pips, and 9, which would lose 16, but not the one on 20
		final Board blackPosition = TestBoards.empty();
		blackPosition.setBlackCount(7, 1);
		blackPosition.setBlackCount(9, 1);
		blackPosition.setBlackCount(20, 1);
		blackPosition.setRedCount(3, 2);

		assertEquals(34, Feature.PIPS_AT_RISK.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aBlotThatHasComeFurtherHasMoreAtRisk(final boolean black) {
		// red's bar is 0, so it exposes the blot on 2, which has come much further than the one on 22
		final Board nearHome = TestBoards.empty();
		nearHome.setBlackCount(2, 1);
		nearHome.setRedCount(0, 1);
		final Board farFromHome = TestBoards.empty();
		farFromHome.setBlackCount(22, 1);
		farFromHome.setRedCount(20, 1);

		assertEquals(23, Feature.PIPS_AT_RISK.measure(forColour(nearHome, black), black));
		assertEquals(3, Feature.PIPS_AT_RISK.measure(forColour(farFromHome, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void thePrimeIsTheLongestRunOfPointsMade(final boolean black) {
		final Board blackPosition = TestBoards.empty();
		for (final int point : new int[]{4, 5, 6, 7, 9, 10}) {
			blackPosition.setBlackCount(point, 2);
		}

		assertEquals(4, Feature.LONGEST_PRIME.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void borneOffCountsThePlayersPiecesOff(final boolean black) {
		final Board blackPosition = TestBoards.empty();
		blackPosition.setBlackCount(3, 12);
		blackPosition.setBlackBore(3);

		assertEquals(3, Feature.BORNE_OFF.measure(forColour(blackPosition, black), black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void onceThePlayersHavePassedEachOtherItsARace(final boolean black) {
		final Board blackPosition = TestBoards.empty();
		blackPosition.setBlackCount(9, 15);
		blackPosition.setRedCount(10, 15);
		assertFalse(Feature.isContact(forColour(blackPosition, black)));

		// one red piece left behind black's makes it contact again
		blackPosition.setRedCount(10, 14);
		blackPosition.setRedCount(2, 1);
		assertTrue(Feature.isContact(forColour(blackPosition, black)));
	}

	private static Board forColour(final Board blackPosition, final boolean black) {
		return black ? blackPosition : BoardMirror.mirror(blackPosition);
	}
}
