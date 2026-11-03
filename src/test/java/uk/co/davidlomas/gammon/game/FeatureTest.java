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
	public void aBlotIsExposedOnlyWhenAnOpponentIsWithinSix(final boolean black) {
		final Board blackPosition = empty();
		blackPosition.Points[7].setBlackCount(1);
		blackPosition.Points[15].setBlackCount(1);
		blackPosition.Points[10].setBlackCount(2);
		// red on 3 is 4 short of the blot on 7, and 12 short of the one on 15
		blackPosition.Points[3].setRedCount(2);

		final Board board = forColour(blackPosition, black);
		assertEquals(2, Feature.BLOTS.measure(board, black));
		assertEquals(1, Feature.EXPOSED_BLOTS.measure(board, black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPieceOnTheBarCanHitABlotOnItsWayIn(final boolean black) {
		final Board blackPosition = empty();
		blackPosition.Points[5].setBlackCount(1);
		// red's bar: it comes in on 1 to 6
		blackPosition.Points[0].setRedCount(1);

		final Board board = forColour(blackPosition, black);
		assertEquals(1, Feature.EXPOSED_BLOTS.measure(board, black));
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
