package uk.co.davidlomas.gammon.genes;

import uk.co.davidlomas.gammon.game.Board;
import uk.co.davidlomas.gammon.game.GameObserver;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.game.WinType;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

/**
 * How a search is going, for showing while it runs. The search's threads write to this as they go, and whatever is
 * showing it reads it whenever it likes, so a slow window can't hold up the games. None of it is here unless something
 * is watching: a search with nothing watching, such as the benchmark's, does none of this work.
 */
public final class SearchProgress {

	public enum State {
		RUNNING, FINISHED, FAILED
	}

	/**
	 * What a set of games is being played for
	 */
	public enum Round {
		FITNESS("working out fitness"), REFERENCE("playing the best against the reference player");

		private final String description;

		Round(final String description) {
			this.description = description;
		}

		public String describe() {
			return description;
		}
	}

	/**
	 * A game as it stood after its last turn
	 */
	public record LiveGame(String black, String red, int turns, int blackPips, int redPips, int blackOff, int redOff) {
	}

	/**
	 * What a finished generation came to, the same as its row in runStats.csv
	 *
	 * @param pointsAgainstReference null if it wasn't measured this generation
	 */
	public record GenerationResult(String generation, double bestFitness, double geneSpread, double turnsPerGame,
								   double drawRate, double singleRate, double gammonRate, double backgammonRate,
								   Double pointsAgainstReference) {
	}

	private static volatile SearchProgress watching;

	private final long startNanos = System.nanoTime();
	private final LongAdder games = new LongAdder();
	private final LongAdder turns = new LongAdder();
	private final LongAdder[] endings = new LongAdder[WinType.values().length];
	private final AtomicInteger gamesThisRound = new AtomicInteger();
	private final List<GenerationResult> results = new CopyOnWriteArrayList<>();

	private volatile State state = State.RUNNING;
	private volatile long endNanos;
	private volatile int generations;
	private volatile String stage = "Starting";
	private volatile Round round = Round.FITNESS;
	private volatile int gamesPlannedThisRound;
	private volatile AtomicReferenceArray<LiveGame> liveGames = new AtomicReferenceArray<>(0);
	private volatile Queue<Integer> freeSlots = new ConcurrentLinkedQueue<>();

	public SearchProgress() {
		for (int ending = 0; ending < endings.length; ending++) {
			endings[ending] = new LongAdder();
		}
	}

	/**
	 * @return what's watching the search running now, or null if nothing is
	 */
	static SearchProgress watching() {
		return watching;
	}

	static void watch(final SearchProgress progress) {
		watching = progress;
	}

	/**
	 * Tells what's watching the search, if anything is
	 */
	static void ifWatching(final Consumer<SearchProgress> tell) {
		final SearchProgress progress = watching;
		if (progress != null) {
			tell.accept(progress);
		}
	}

	public void searchStarted(final int generations) {
		this.generations = generations;
	}

	/**
	 * @param stage what the search has moved on to, such as "Generation 3 of 50"
	 */
	public void stageStarted(final String stage) {
		this.stage = stage;
	}

	/**
	 * @param games how many games this round is
	 * @param slots how many games can be playing at the same time
	 */
	public void roundStarted(final Round round, final int games, final int slots) {
		final Queue<Integer> free = new ConcurrentLinkedQueue<>();
		for (int slot = 0; slot < slots; slot++) {
			free.add(slot);
		}
		this.round = round;
		gamesPlannedThisRound = games;
		gamesThisRound.set(0);
		liveGames = new AtomicReferenceArray<>(slots);
		freeSlots = free;
	}

	public void generationFinished(final GenerationResult result) {
		results.add(result);
	}

	public void finished() {
		end(State.FINISHED);
	}

	public void failed() {
		end(State.FAILED);
	}

	private void end(final State ending) {
		endNanos = System.nanoTime();
		state = ending;
	}

	/**
	 * Watches one meeting, its games showing in a slot of their own until this is closed. Only as many meetings as the
	 * round has slots can be playing at once
	 *
	 * @param first      the player the meeting's first name is for
	 * @param firstName  what to call first when showing their games
	 * @param secondName what to call their opponent when showing their games
	 */
	public MeetingWatch watchMeeting(final Individual first, final String firstName, final String secondName) {
		return new MeetingWatch(first, firstName, secondName);
	}

	public State getState() {
		return state;
	}

	public String getStage() {
		return stage;
	}

	public Round getRound() {
		return round;
	}

	public int getGenerations() {
		return generations;
	}

	public long getGames() {
		return games.sum();
	}

	public int getGamesThisRound() {
		return gamesThisRound.get();
	}

	public int getGamesPlannedThisRound() {
		return gamesPlannedThisRound;
	}

	public double getSeconds() {
		return ((state == State.RUNNING ? System.nanoTime() : endNanos) - startNanos) / 1e9;
	}

	/**
	 * @return the share of the search that's done, 0 to 1, by how many generations have finished and how far through
	 * its fitness games the current one is
	 */
	public double getShareDone() {
		if (state == State.FINISHED) {
			return 1;
		}
		// the initial population's round counts as well as the generations
		final int stages = generations + 1;
		final int planned = gamesPlannedThisRound;
		final double thisStage = round == Round.REFERENCE ? 1 : planned == 0 ? 0 : (double) gamesThisRound.get() / planned;
		return Math.min(1, (results.size() + thisStage) / stages);
	}

	/**
	 * @return the average turns in a game so far, 0 if none have finished
	 */
	public double getTurnsPerGame() {
		final long played = games.sum();
		return played == 0 ? 0 : (double) turns.sum() / played;
	}

	/**
	 * @return the share of the games so far, 0 to 1, that ended this way, 0 if none have finished
	 */
	public double getRate(final WinType ending) {
		final long played = games.sum();
		return played == 0 ? 0 : (double) endings[ending.ordinal()].sum() / played;
	}

	/**
	 * @return a place for each game that can be playing now, null where none is
	 */
	public List<LiveGame> getLiveGames() {
		final AtomicReferenceArray<LiveGame> live = liveGames;
		final List<LiveGame> games = new ArrayList<>(live.length());
		for (int slot = 0; slot < live.length(); slot++) {
			games.add(live.get(slot));
		}
		return games;
	}

	/**
	 * @return the generations finished so far, oldest first
	 */
	public List<GenerationResult> getResults() {
		return List.copyOf(results);
	}

	/**
	 * Watches the games of one meeting. Each meeting is played by one thread, which is the only one to use this
	 */
	public final class MeetingWatch implements GameObserver, AutoCloseable {

		private final Individual first;
		private final String firstName;
		private final String secondName;
		private final AtomicReferenceArray<LiveGame> live = liveGames;
		private final Queue<Integer> free = freeSlots;
		private final Integer slot = free.poll();
		private String black;
		private String red;

		private MeetingWatch(final Individual first, final String firstName, final String secondName) {
			if (slot == null) {
				throw new IllegalStateException("More meetings playing at once than the round has slots for");
			}
			this.first = first;
			this.firstName = firstName;
			this.secondName = secondName;
		}

		@Override
		public void gameStarted(final Individual playerOne, final Individual playerTwo, final boolean playerOneIsBlack) {
			final String playerOneName = playerOne == first ? firstName : secondName;
			final String playerTwoName = playerOne == first ? secondName : firstName;
			black = playerOneIsBlack ? playerOneName : playerTwoName;
			red = playerOneIsBlack ? playerTwoName : playerOneName;
			turnPlayed(new Board(), 0);
		}

		@Override
		public void turnPlayed(final Board board, final int turns) {
			live.set(slot, new LiveGame(black, red, turns, board.pipCount(true), board.pipCount(false),
					board.getBlackBore(), board.getRedBore()));
		}

		@Override
		public void gameFinished(final GameStats stats) {
			games.increment();
			turns.add(stats.getTurns());
			endings[stats.getWinType().ordinal()].increment();
			gamesThisRound.incrementAndGet();
		}

		@Override
		public void close() {
			live.set(slot, null);
			free.add(slot);
		}
	}
}
