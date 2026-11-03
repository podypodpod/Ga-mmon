package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

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
	public void aShortRunGivesARowWithEveryColumn() throws Exception {
		final String[] row = Benchmark.run(1, 2, 2).split(",", -1);
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
