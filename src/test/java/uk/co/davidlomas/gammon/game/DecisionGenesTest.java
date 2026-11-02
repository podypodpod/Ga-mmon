package uk.co.davidlomas.gammon.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.test.helpers.BoardMirror;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks the genes used after the first move (attributes 1 to 6), for both colours: a player who always follows
 * a gene picks a move that does what the gene asks, when other moves were on offer too.
 * <p>
 * Bearing a piece off (attribute 0) is covered in BoardListTest.
 */
public class DecisionGenesTest {

	/**
	 * One gene: the position and roll to try it on, as black, and the check for the kind of move it asks for.
	 */
	private static final class Scenario {
		final int gene;
		final String name;
		final Supplier<Board> blackBoard;
		final int die1;
		final int die2;
		final BiPredicate<BoardEvaluator, Board> check;

		Scenario(final int gene, final String name, final Supplier<Board> blackBoard, final int die1, final int die2,
				final BiPredicate<BoardEvaluator, Board> check) {
			this.gene = gene;
			this.name = name;
			this.blackBoard = blackBoard;
			this.die1 = die1;
			this.die2 = die2;
			this.check = check;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final List<Scenario> SCENARIOS = List.of(
			// a red blot on 22 that black's 24 point can hit with the 2
			new Scenario(1, "take a piece", () -> {
				final Board board = new Board();
				board.Points[19].setRedCount(4);
				board.Points[22].setRedCount(1);
				return board;
			}, 2, 1, BoardEvaluator::hasAPieceBeenTaken),
			// a black blot on 5 that the 6 point can cover with the 1
			new Scenario(2, "double up a blot", () -> {
				final Board board = new Board();
				board.Points[6].setBlackCount(4);
				board.Points[5].setBlackCount(1);
				return board;
			}, 2, 1, BoardEvaluator::hasABlotBeenDoubled),
			new Scenario(3, "block an opponent", Board::new, 4, 2, BoardEvaluator::hasTheOpponentBeenBlocked),
			new Scenario(4, "move a piece solo", Board::new, 6, 4, BoardEvaluator::hasAPieceBeenMovedSolo),
			new Scenario(6, "add to a stack", Board::new, 6, 5, BoardEvaluator::hasAStackBeenAddedTo),
			// every black piece is home, so this is the end game, where spreading is used
			new Scenario(5, "spread a home piece", () -> {
				final Board board = new Board();
				for (final Point point : board.Points) {
					point.setBlackCount(0);
					point.setRedCount(0);
				}
				board.Points[1].setBlackCount(1);
				board.Points[6].setBlackCount(3);
				board.Points[20].setRedCount(2);
				return board;
			}, 2, 1, BoardEvaluator::hasAPieceBeenSpread));

	private static Stream<Arguments> scenariosForBothColours() {
		final List<Arguments> arguments = new ArrayList<>();
		for (final Scenario scenario : SCENARIOS) {
			arguments.add(Arguments.of(scenario, true));
			arguments.add(Arguments.of(scenario, false));
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

	@ParameterizedTest(name = "{0}, black {1}")
	@MethodSource("scenariosForBothColours")
	public void aPlayerWhoAlwaysFollowsTheGeneMakesThatKindOfMove(final Scenario scenario, final boolean black) {
		// Given a player who only ever follows this gene
		final Individual individual = new Individual();
		for (int i = 0; i < individual.getNumOfAttributes(); i++) {
			individual.getAttribute(i).setValue(i == scenario.gene ? 100 : 0);
		}
		final Board start = black ? scenario.blackBoard.get() : BoardMirror.mirror(scenario.blackBoard.get());
		final BoardEvaluator evaluator = new BoardEvaluator();
		evaluator.setBoard(start);
		evaluator.setPlayer(new AiPlayer(black, individual));

		// And the roll offers moves that do what the gene asks, and moves that don't
		final MoveGenerator offered = new MoveGenerator(individual);
		offered.generateMoves(start, playerWithRoll(black, individual, scenario));
		final List<Board> options = offered.getBoardList().getBoards();
		final long matching = options.stream().filter(board -> scenario.check.test(evaluator, board)).count();
		assertTrue(matching > 0, "no move on offer can " + scenario.name);
		assertTrue(matching < options.size(), "every move on offer would " + scenario.name + ", so the gene isn't tested");

		// Several seeds, so a random move that happens to fit can't make this pass
		for (long seed = 0; seed < 10; seed++) {
			RandomSource.setSeed(seed);

			// When it takes its turn
			final Board chosen = new MoveGenerator(individual)
					.getNextMoveBoard(start, playerWithRoll(black, individual, scenario));

			// Then it makes that kind of move
			assertTrue(scenario.check.test(evaluator, chosen), "seed " + seed);
		}
	}

	private static AiPlayer playerWithRoll(final boolean black, final Individual individual, final Scenario scenario) {
		final AiPlayer player = new AiPlayer(black, individual);
		player.movesLeft.add(scenario.die1);
		player.movesLeft.add(scenario.die2);
		return player;
	}
}
