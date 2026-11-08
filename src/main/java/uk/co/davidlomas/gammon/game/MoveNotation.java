package uk.co.davidlomas.gammon.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Says what a turn did in backgammon's usual notation, such as "24/18 13/9*": each checker's move from point to point,
 * by the numbers on the board window, so black moves down them and red up. "bar" is coming in from the bar, "off" is
 * bearing off, and "*" marks a hit.
 * <p>
 * The game only keeps the board a turn ends on, so the moves are worked out again from the boards before and after.
 * That's too slow for the GA's games, but fine for one being watched.
 */
public final class MoveNotation {

	static final String NO_MOVE = "can't move";

	private record Step(int from, int to, boolean hit) {
	}

	private MoveNotation() {
	}

	/**
	 * @param before the board at the start of the turn
	 * @param after  the board the turn ended on
	 * @param black  who moved
	 * @param dice   the dice they rolled
	 *
	 * @return the moves, "can't move" if the board didn't change, or "?" if no way of using the dice gets from one to the
	 * other, which shouldn't happen
	 */
	public static String describe(final Board before, final Board after, final boolean black, final List<Integer> dice) {
		if (before.equals(after)) {
			return NO_MOVE;
		}
		final List<Integer> moves = new ArrayList<>(dice);
		if (moves.size() == 2 && moves.get(0).equals(moves.get(1))) {
			moves.addAll(dice);
		}
		final List<Step> steps = new ArrayList<>();
		if (!find(before, after, black, moves, steps)) {
			return "?";
		}
		return steps.stream()
				.sorted(Comparator.comparingInt((Step s) -> point(s.from(), black)).reversed())
				.map(step -> name(step.from(), false) + "/" + name(step.to(), true)
						+ (step.hit() ? "*" : ""))
				.collect(Collectors.joining(" "));
	}

	/**
	 * Tries each way of moving a checker with each die left, depth first, until one reaches the board the turn ended on
	 */
	private static boolean find(final Board board, final Board target, final boolean black, final List<Integer> diceLeft,
								final List<Step> steps) {
		if (board.equals(target)) {
			return true;
		}
		for (int index = 0; index < diceLeft.size(); index++) {
			final int die = diceLeft.get(index);
			if (diceLeft.subList(0, index).contains(die)) {
				continue;
			}
			final List<Integer> rest = new ArrayList<>(diceLeft);
			rest.remove(index);
			for (int from = 0; from < Board.POINTS; from++) {
				final int to = black ? from - die : from + die;
				final AiPlayer player = new AiPlayer(black, null);
				player.movesLeft.setTo(List.of(die));
				if (player.isMovePossible(from, to, board)) {
					final Board next = new Board(board);
					final boolean onBoard = to > 0 && to < Board.POINTS - 1;
					final boolean hit = onBoard && next.numEither(to) == 1 && next.getCol(to) != black;
					player.movePiece(from, to, next);
					steps.add(new Step(from, Math.max(0, Math.min(Board.POINTS - 1, to)), hit));
					if (find(next, target, black, rest, steps)) {
						return true;
					}
					steps.remove(steps.size() - 1);
				}
			}
		}
		return false;
	}

	/**
	 * @return how far the point is from where the mover bears off, so their moves can be listed furthest back first
	 */
	private static int point(final int index, final boolean black) {
		return black ? index : Board.POINTS - 1 - index;
	}

	/**
	 * @param to whether it's where the checker ends up, as the mover's bar is where they bear off on the other side
	 */
	private static String name(final int index, final boolean to) {
		if (index == 0 || index == Board.POINTS - 1) {
			return to ? "off" : "bar";
		}
		return String.valueOf(index);
	}
}
