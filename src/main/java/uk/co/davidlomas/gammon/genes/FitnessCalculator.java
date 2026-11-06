package uk.co.davidlomas.gammon.genes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.game.GameManager;
import uk.co.davidlomas.gammon.game.GameStats;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.LongSupplier;

/**
 * FitnessCalculator
 * <p>
 * Allows the calculation of fitness of an individual or population
 *
 * @author David Lomas
 */
class FitnessCalculator {

	private final static Logger logger = LoggerFactory.getLogger(FitnessCalculator.class);

	/**
	 * calculateFitnessOfPopulation
	 * <p>
	 * calculates the fitness of every individual in the population: the average score from every game it plays.
	 * <p>
	 * Every pair of players meets GenAlgSettings.getGamesPerPairing() times. Each meeting is two games with the same
	 * dice, one with each of them as player one, so they both get the luckier seat, and both players' scores count.
	 * That takes out much of the luck backgammon's dice bring, which would otherwise decide more of the fitness than
	 * the players' skill.
	 * <p>
	 * With GameSettings' multiThreading on, the meetings are played at the same time, one per core.
	 *
	 * @param pop - population we are measuring
	 */
	static void calculateFitnessOfPopulation(final Population pop) {
		calculateFitnessOfPopulation(pop, RandomSource::nextLong);
	}

	/**
	 * As calculateFitnessOfPopulation, with the seed for each meeting taken from meetingSeeds, so tests can choose
	 * them
	 */
	static void calculateFitnessOfPopulation(final Population pop, final LongSupplier meetingSeeds) {

		final int size = pop.individuals.length;
		final int meetings = GenAlgSettings.getGamesPerPairing();

		// every meeting and its seed, worked out first and in order, so the result is the same however many
		// threads play them and in whatever order they finish
		final List<Meeting> plan = new ArrayList<>();
		for (int first = 0; first < size; first++) {
			for (int second = first + 1; second < size; second++) {
				for (int meeting = 0; meeting < meetings; meeting++) {
					plan.add(new Meeting(first, second, meetingSeeds.getAsLong()));
				}
			}
		}

		logger.trace("Round robin started: {} meetings", plan.size());
		final List<MeetingGames> played = play(pop, plan);

		final double[] totals = new double[size];
		final GameTally tally = new GameTally();
		for (int i = 0; i < plan.size(); i++) {
			totals[plan.get(i).first()] += played.get(i).firstScore();
			totals[plan.get(i).second()] += played.get(i).secondScore();
			tally.add(played.get(i).firstAsPlayerOne());
			tally.add(played.get(i).secondAsPlayerOne());
		}
		pop.setGameTally(tally);

		// each player plays two games a meeting against each of the others
		final int gamesEach = 2 * meetings * (size - 1);
		for (int i = 0; i < size; i++) {
			pop.individuals[i].setFitness(gamesEach == 0 ? 0 : totals[i] / gamesEach);
		}
	}

	/**
	 * The player's average points a game against the opponent, a meeting for each seed. It's played on threads of its
	 * own, like the fitness games, so the caller's random numbers aren't touched
	 */
	static double averagePoints(final Individual player, final Individual opponent, final long[] meetingSeeds) {
		final Population pair = new Population(2, false);
		pair.saveIndividual(0, player);
		pair.saveIndividual(1, opponent);

		final List<Meeting> plan = new ArrayList<>();
		for (final long seed : meetingSeeds) {
			plan.add(new Meeting(0, 1, seed));
		}
		double total = 0;
		for (final MeetingGames games : play(pair, plan)) {
			total += games.firstScore();
		}
		return total / (2.0 * meetingSeeds.length);
	}

	/**
	 * Plays every meeting in the plan, on as many threads as the settings allow, and gives back each one's games
	 * in the plan's order. Even with one thread they're played on a thread of their own, so the caller's random
	 * numbers aren't touched either way
	 */
	private static List<MeetingGames> play(final Population pop, final List<Meeting> plan) {
		// the settings are read now, before any game, so the threads don't each read them from the file
		GameSettings.loadAll();
		final int wanted = !playsInParallel() ? 1
				: GameSettings.getThreads() == 0 ? Runtime.getRuntime().availableProcessors() : GameSettings.getThreads();
		// no more threads than meetings, as any more would have nothing to play
		final int threads = Math.max(1, Math.min(wanted, plan.size()));
		final ExecutorService pool = Executors.newFixedThreadPool(threads);
		try {
			final List<Future<MeetingGames>> playing = new ArrayList<>();
			for (final Meeting meeting : plan) {
				final Individual first = pop.individuals[meeting.first()];
				final Individual second = pop.individuals[meeting.second()];
				playing.add(pool.submit(() -> playMeeting(new GameManager(), first, second, meeting.seed())));
			}
			final List<MeetingGames> played = new ArrayList<>();
			for (final Future<MeetingGames> meeting : playing) {
				played.add(meeting.get());
			}
			return played;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Interrupted while the games were played", e);
		} catch (final ExecutionException e) {
			throw new IllegalStateException("A game failed", e.getCause());
		} finally {
			// stops the meetings still to start, and interrupts those being played, which then stop at the end of
			// the turn, so after a failure the others don't carry on in the background
			pool.shutdownNow();
		}
	}

	/**
	 * Whether games can be played at the same time. They can't when they're shown in the one window, or when a person
	 * is playing them on the one console. The settings have to have been read
	 */
	static boolean playsInParallel() {
		return GameSettings.getMultiThreading() && !GameSettings.getDisplayGUI() && GameSettings.getAreBothAI();
	}

	/**
	 * A meeting to play: the two players, by their place in the population, and the meeting's seed
	 */
	private record Meeting(int first, int second, long seed) {
	}

	/**
	 * The two games of a meeting
	 */
	private record MeetingGames(GameStats firstAsPlayerOne, GameStats secondAsPlayerOne) {

		double firstScore() {
			return firstAsPlayerOne.getPlayerOneScore() + secondAsPlayerOne.getPlayerTwoScore();
		}

		double secondScore() {
			return firstAsPlayerOne.getPlayerTwoScore() + secondAsPlayerOne.getPlayerOneScore();
		}
	}

	/**
	 * One meeting: two games with the same dice, one with each player as player one. The seed sets the players'
	 * choices and the dice, so a meeting plays the same whichever thread plays it
	 *
	 * @return each player's total score from the two games, first then second
	 */
	static double[] meet(final GameManager gameManager, final Individual first, final Individual second,
						 final long seed) {
		final MeetingGames games = playMeeting(gameManager, first, second, seed);
		return new double[]{games.firstScore(), games.secondScore()};
	}

	private static MeetingGames playMeeting(final GameManager gameManager, final Individual first,
											final Individual second, final long seed) {
		RandomSource.setSeed(seed);
		final long diceSeed = RandomSource.nextLong();

		RandomSource.setDiceSeed(diceSeed);
		final GameStats firstAsPlayerOne = gameManager.playIndividualsVsEachOther(first, second);

		RandomSource.setDiceSeed(diceSeed);
		final GameStats secondAsPlayerOne = gameManager.playIndividualsVsEachOther(second, first);

		return new MeetingGames(firstAsPlayerOne, secondAsPlayerOne);
	}
}
