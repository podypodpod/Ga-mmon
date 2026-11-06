package uk.co.davidlomas.gammon.game;

import uk.co.davidlomas.gammon.genes.Individual;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Class MoveGenerator.
 * <p>
 * generates all possible moves for the player at the current board state
 * <p>
 * adds them all to the board list collection
 *
 * @author David Lomas
 */
public class MoveGenerator {

	private final BoardList boardList;
	private final Individual individual;

	/**
	 * Instantiates a new move generator.
	 */
	public MoveGenerator(final Individual individual) {
		this.individual = individual;
		boardList = new BoardList();
	}

	/**
	 * Generate all possible moves.
	 *
	 * @param cb the current board
	 * @param p  the player
	 */
	public void generateMoves(final Board cb, final AiPlayer p) {

		// Loop all moves
		final List<Integer> moves = p.movesLeft.moves;
		for (int move = 0; move < moves.size(); move++) {
			final int currentMove = moves.get(move);

			// a double has the same die four times, and each would give the same boards, so try each value once
			if (moves.subList(0, move).contains(currentMove)) {
				continue;
			}

			for (int point = 0; point < Board.POINTS; point++) {
				final int to = p.black ? point - currentMove : point + currentMove;

				// if the current point can move the current move, add the board it leads to
				if (p.isMovePossible(point, to, cb)) {
					boardList.addBoardWithNextMove(new Board(cb), to, point, new AiPlayer(p), this);
				}
			}
		}
	}

	public BoardList getBoardList() {
		return boardList;
	}

	/**
	 * Gets the next move board.
	 *
	 * @param cb the current board
	 * @param p  the player
	 *
	 * @return the next move board
	 */
	public Board getNextMoveBoard(final Board cb, final AiPlayer p) {

		generateLegalMoves(cb, p);

		// the player's genes pick the best board, if no possible next moves it
		// will return null and carry on using the same board. The benchmark's reference player has rules of its own
		return individual instanceof ReferencePlayer
				? ReferencePlayer.choose(boardList.getBoards(), p.black)
				: MoveChooser.choose(boardList.getBoards(), p.black, individual);
	}

	/**
	 * Fills the board list with every board the player could finish their turn on, following the rules for using
	 * the dice. This is the one place those rules are applied, for the AI and for a person on the console.
	 *
	 * @param cb the current board
	 * @param p  the player, with the dice they rolled
	 */
	void generateLegalMoves(final Board cb, final AiPlayer p) {
		boardList.clearList();
		final List<Integer> roll = new ArrayList<>(p.movesLeft.moves);

		// generate all possible moves, then drop the ones the rules don't allow
		generateMoves(cb, p);
		boardList.keepOnlyLegalMoves(roll);
	}

	/**
	 * The boards a player with these dice could finish their turn on, following the rules for using the dice
	 *
	 * @param board the board at the start of the turn
	 * @param black the player
	 * @param dice  the dice they rolled
	 *
	 * @return the list of boards, which also knows how many dice they use
	 */
	static BoardList legalMoves(final Board board, final boolean black, final List<Integer> dice) {
		// no Individual is needed, as nothing is chosen from the list
		final MoveGenerator generator = new MoveGenerator(null);
		generator.generateLegalMoves(board, playerWith(black, dice));
		return generator.boardList;
	}

	/**
	 * Every board a player with these dice could reach from this one, part way through the turn as well as at the end,
	 * ignoring the rules for using the dice
	 *
	 * @param board the board to start from
	 * @param black the player
	 * @param dice  the dice they have left
	 *
	 * @return the boards
	 */
	static Set<Board> reachableBoards(final Board board, final boolean black, final List<Integer> dice) {
		final MoveGenerator generator = new MoveGenerator(null);
		generator.generateMoves(board, playerWith(black, dice));
		return new HashSet<>(generator.boardList.getBoards());
	}

	private static AiPlayer playerWith(final boolean black, final List<Integer> dice) {
		final AiPlayer player = new AiPlayer(black, null);
		player.movesLeft.setTo(dice);
		return player;
	}
}