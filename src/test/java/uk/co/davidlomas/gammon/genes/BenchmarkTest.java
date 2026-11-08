package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.davidlomas.gammon.game.ReferencePlayer;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;
import uk.co.davidlomas.gammon.util.RandomSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BenchmarkTest {

	@AfterEach
	public void resetSettings() {
		// the benchmark sets its own, which mustn't reach the other tests
		SettingsUtil.resetSettings();
	}

	@Test
	public void anOpponentIsMadeAfterItsSeedIsSetSoTheMeetingsAreAsTheyAlwaysWere() {
		RandomSource.setSeed(4);
		final Individual player = new Individual();

		final double measured = Benchmark.against(player, ReferencePlayer::new, 5, 2000);

		// as the benchmark has always done it: the seed, then the reference player, then the meetings' seeds
		RandomSource.setSeed(2000);
		final ReferencePlayer reference = new ReferencePlayer();
		final long[] meetingSeeds = new long[5];
		for (int meeting = 0; meeting < meetingSeeds.length; meeting++) {
			meetingSeeds[meeting] = RandomSource.nextLong();
		}
		assertEquals(FitnessCalculator.averagePoints(player, reference, meetingSeeds, null), measured);
	}

	@Test
	public void aShortRunGivesARowWithEveryColumn() throws Exception {
		final String[] row = Benchmark.run(1, 2, 2, 2).split(",", -1);
		final String[] header = Benchmark.HEADER.split(",");

		assertEquals(header.length, row.length);
		for (final String column : row) {
			assertFalse(column.isEmpty());
		}
		// one result for each seed
		assertEquals(2, row[List.of(header).indexOf("perSeed")].split(" ").length);
	}

	@Test
	public void theFirstRowGetsAHeaderAndLaterOnesAreAdded(@TempDir final Path dir) throws Exception {
		final Path csv = dir.resolve("benchmarks.csv");

		Benchmark.append(csv, "first");
		Benchmark.append(csv, "second");

		assertEquals(List.of(Benchmark.HEADER, "first", "second"), Files.readAllLines(csv));
	}
}
