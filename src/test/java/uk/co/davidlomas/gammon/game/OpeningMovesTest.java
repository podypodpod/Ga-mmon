package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks every opening move gene (attributes 7 to 31), for both colours.
 * <p>
 * Lives in the same package as the game code so it can reach the package-private opening checks.
 */
public class OpeningMovesTest {

	/**
	 * One opening: the gene that asks for it, the roll it is for, the check that recognises it, and the moves as
	 * black plays them (from, to). Red plays the mirror image, point p becoming 25 - p.
	 */
	private static final class Opening {
		final int gene;
		final int die1;
		final int die2;
		final BiPredicate<BoardEvaluator, Board> check;
		final int[][] blackMoves;

		Opening(final int gene, final int die1, final int die2, final BiPredicate<BoardEvaluator, Board> check,
				final int[]... blackMoves) {
			this.gene = gene;
			this.die1 = die1;
			this.die2 = die2;
			this.check = check;
			this.blackMoves = blackMoves;
		}
	}

	private static final List<Opening> OPENINGS = List.of(
			new Opening(7, 2, 1, BoardEvaluator::isTwoOneSplitPlayInitialMove, new int[]{24, 23}, new int[]{13, 11}),
			new Opening(8, 2, 1, BoardEvaluator::isTwoOneSlotPlayInitialMove, new int[]{13, 11}, new int[]{6, 5}),
			new Opening(9, 3, 1, BoardEvaluator::isThreeOneInitialMove, new int[]{8, 5}, new int[]{6, 5}),
			new Opening(10, 3, 2, BoardEvaluator::isThreeTwoSplitInitialMove, new int[]{24, 21}, new int[]{13, 11}),
			new Opening(11, 3, 2, BoardEvaluator::isThreeTwoOffenceInitialMove, new int[]{13, 10}, new int[]{13, 11}),
			new Opening(12, 4, 1, BoardEvaluator::isFourOneInitialMove, new int[]{24, 23}, new int[]{13, 9}),
			new Opening(13, 4, 1, BoardEvaluator::isFourOneInitialMoveAlt, new int[]{13, 9}, new int[]{6, 5}),
			new Opening(14, 4, 2, BoardEvaluator::isFourTwoInitialMove, new int[]{8, 4}, new int[]{6, 4}),
			new Opening(15, 4, 3, BoardEvaluator::isFourThreeInitialMoveSplit, new int[]{24, 21}, new int[]{13, 9}),
			new Opening(16, 4, 3, BoardEvaluator::isFourThreeInitialMoveBlock, new int[]{13, 10}, new int[]{13, 9}),
			new Opening(17, 5, 1, BoardEvaluator::isFiveOneInitialMove, new int[]{24, 23}, new int[]{13, 8}),
			new Opening(18, 5, 1, BoardEvaluator::isFiveOneInitialMoveAlt, new int[]{13, 8}, new int[]{6, 5}),
			new Opening(19, 5, 2, BoardEvaluator::isFiveTwoInitialMove, new int[]{13, 11}, new int[]{13, 8}),
			new Opening(20, 5, 2, BoardEvaluator::isFiveTwoInitialMoveRisk, new int[]{24, 22}, new int[]{13, 8}),
			new Opening(21, 5, 3, BoardEvaluator::isFiveThreeInitialMove, new int[]{8, 3}, new int[]{6, 3}),
			new Opening(22, 5, 4, BoardEvaluator::isFiveFourInitialMoveAgr, new int[]{13, 9}, new int[]{13, 8}),
			new Opening(23, 5, 4, BoardEvaluator::isFiveFourInitialMoveBal, new int[]{24, 20}, new int[]{13, 8}),
			new Opening(24, 6, 1, BoardEvaluator::isSixOneInitialMove, new int[]{13, 7}, new int[]{8, 7}),
			new Opening(25, 6, 2, BoardEvaluator::isSixTwoInitialMove, new int[]{24, 18}, new int[]{13, 11}),
			new Opening(26, 6, 2, BoardEvaluator::isSixTwoInitialMoveAgr, new int[]{13, 7}, new int[]{7, 5}),
			new Opening(27, 6, 3, BoardEvaluator::isSixThreeInitialMove, new int[]{24, 18}, new int[]{18, 15}),
			new Opening(28, 6, 3, BoardEvaluator::isSixThreeInitialMoveSplit, new int[]{24, 18}, new int[]{13, 10}),
			new Opening(29, 6, 4, BoardEvaluator::isSixFourInitialMove, new int[]{24, 18}, new int[]{18, 14}),
			new Opening(30, 6, 4, BoardEvaluator::isSixFourInitialMoveSplit, new int[]{24, 18}, new int[]{13, 9}),
			new Opening(31, 6, 5, BoardEvaluator::isSixFiveInitialMove, new int[]{24, 18}, new int[]{18, 13}));

	private static Stream<Arguments> openingsForBothColours() {
		final List<Arguments> arguments = new ArrayList<>();
		for (final Opening opening : OPENINGS) {
			arguments.add(Arguments.of(opening.gene, true, opening));
			arguments.add(Arguments.of(opening.gene, false, opening));
		}
		return arguments.stream();
	}

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

	@ParameterizedTest(name = "gene {0}, black {1}")
	@MethodSource("openingsForBothColours")
	public void theOpeningCheckRecognisesItsOpening(final int gene, final boolean black, final Opening opening) {
		// Given the board after the opening has been played
		final Board start = new Board();
		final Board afterOpening = playOpening(start, opening, black);
		final BoardEvaluator evaluator = new BoardEvaluator();
		evaluator.setBoard(start);
		evaluator.setPlayer(new AiPlayer(black, new Individual()));

		// Then its check recognises it
		assertTrue(opening.check.test(evaluator, afterOpening));
	}

	@ParameterizedTest(name = "gene {0}, black {1}")
	@MethodSource("openingsForBothColours")
	public void aPlayerWhoAlwaysFollowsTheGenePlaysItsOpening(final int gene, final boolean black, final Opening opening) {
		// Given a player who only ever follows this gene
		final Individual individual = new Individual();
		for (int i = 0; i < individual.getNumOfAttributes(); i++) {
			individual.getAttribute(i).setValue(i == gene ? 100 : 0);
		}

		// Several seeds, so a random move that happens to be the right one can't make this pass
		for (long seed = 0; seed < 10; seed++) {
			RandomSource.setSeed(seed);

			// When it rolls for the first move of the game
			final Board start = new Board();
			start.isInitialMove = true;
			final AiPlayer player = new AiPlayer(black, individual);
			player.movesLeft.add(opening.die1);
			player.movesLeft.add(opening.die2);
			final Board chosen = new MoveGenerator(individual).getNextMoveBoard(start, player);

			// Then it plays the opening
			assertEquals(playOpening(new Board(), opening, black), chosen, "seed " + seed);
		}
	}

	private static Board playOpening(final Board start, final Opening opening, final boolean black) {
		final Board board = new Board(start);
		for (final int[] move : opening.blackMoves) {
			final int from = black ? move[0] : 25 - move[0];
			final int to = black ? move[1] : 25 - move[1];
			board.Points[from].removePiece(black);
			board.Points[to].addPiece(black);
		}
		return board;
	}
}
