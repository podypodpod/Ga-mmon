package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveChooserTest {

	private static final int FEATURES = Feature.values().length;

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

	@Test
	public void thereIsAGeneForEachFeatureInContactAndInTheRace() {
		final String[] names = MoveChooser.geneNames();

		assertEquals(2 * FEATURES, names.length);
		assertEquals("contactPipLead", names[0]);
		assertEquals("racePipLead", names[FEATURES]);
		assertEquals("raceStacking", names[2 * FEATURES - 1]);
	}

	@Test
	public void aGeneOf0To100IsAWeightOfMinusOneToOne() {
		final Individual individual = weighted(0);
		individual.getAttribute(1).setValue(50);
		individual.getAttribute(2).setValue(100);

		assertEquals(-1.0, MoveChooser.weight(individual, 0));
		assertEquals(0.0, MoveChooser.weight(individual, 1));
		assertEquals(1.0, MoveChooser.weight(individual, 2));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPlayerWhoValuesHittingHits(final boolean black) {
		// red has a blot on 22 that black's 24 point can hit with the 2
		final Individual hitter = weighted(50);
		hitter.getAttribute(Feature.OPPONENT_ON_BAR.ordinal()).setValue(100);

		for (long seed = 0; seed < 10; seed++) {
			RandomSource.setSeed(seed);
			assertEquals(1, Feature.OPPONENT_ON_BAR.measure(move(blotToHit(black), black, hitter, 2, 1), black));
		}
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aPlayerWhoAvoidsHittingDoesNotHit(final boolean black) {
		final Individual avoider = weighted(50);
		avoider.getAttribute(Feature.OPPONENT_ON_BAR.ordinal()).setValue(0);

		for (long seed = 0; seed < 10; seed++) {
			RandomSource.setSeed(seed);
			assertEquals(0, Feature.OPPONENT_ON_BAR.measure(move(blotToHit(black), black, avoider, 2, 1), black));
		}
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void inARaceTheRaceWeightsDecide(final boolean black) {
		// black has three on 6 and one on 9, red is all past, and a 3 either stacks the 6 point or moves 6 to 3.
		// The contact weights say avoid stacking and the race weights say stack, so it stacks
		final Board blackPosition = empty();
		blackPosition.Points[6].setBlackCount(3);
		blackPosition.Points[9].setBlackCount(1);
		blackPosition.Points[20].setRedCount(15);
		blackPosition.setBlackBore(11);
		final Individual stacker = weighted(50);
		stacker.getAttribute(Feature.STACKING.ordinal()).setValue(0);
		stacker.getAttribute(FEATURES + Feature.STACKING.ordinal()).setValue(100);

		final Board chosen = move(forColour(blackPosition, black), black, stacker, 3);

		assertEquals(1, Feature.STACKING.measure(chosen, black));
	}

	@ParameterizedTest(name = "black {0}")
	@ValueSource(booleans = {true, false})
	public void aWinningMoveIsAlwaysPlayed(final boolean black) {
		// black's last piece is on 1, and this player's genes say bearing off is bad
		final Board blackPosition = empty();
		blackPosition.Points[1].setBlackCount(1);
		blackPosition.setBlackBore(14);
		blackPosition.Points[20].setRedCount(15);
		final Individual reluctant = weighted(50);
		reluctant.getAttribute(FEATURES + Feature.BORNE_OFF.ordinal()).setValue(0);

		final Board chosen = move(forColour(blackPosition, black), black, reluctant, 1);

		assertTrue(chosen.hasPlayerWon(black));
	}

	@Test
	public void withEveryWeightZeroItPicksAtRandomFromTheLegalMoves() {
		final Individual indifferent = weighted(50);
		final Set<Board> chosen = new HashSet<>();

		for (long seed = 0; seed < 20; seed++) {
			RandomSource.setSeed(seed);
			chosen.add(move(new Board(), true, indifferent, 6, 5));
		}

		assertTrue(chosen.size() > 1, "always played the same move");
	}

	private static Board move(final Board board, final boolean black, final Individual individual,
							  final Integer... dice) {
		final AiPlayer player = new AiPlayer(black, individual);
		player.movesLeft.setTo(List.of(dice));
		return new MoveGenerator(individual).getNextMoveBoard(board, player);
	}

	private static Board blotToHit(final boolean black) {
		final Board blackPosition = new Board();
		blackPosition.Points[19].setRedCount(4);
		blackPosition.Points[22].setRedCount(1);
		return forColour(blackPosition, black);
	}

	/**
	 * A player with every gene at this value
	 */
	private static Individual weighted(final int value) {
		final Individual individual = new Individual();
		for (int gene = 0; gene < individual.getNumOfAttributes(); gene++) {
			individual.getAttribute(gene).setValue(value);
		}
		return individual;
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
