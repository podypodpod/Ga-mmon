package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.davidlomas.gammon.genes.SavedAttempts.Attempt;
import uk.co.davidlomas.gammon.genes.SearchProgress.GenerationResult;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SavedAttemptsTest {

	@TempDir
	File folder;

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void theyAreNewestFirstWithEveryGenerationsStats() throws IOException {
		stats(1, RunStats.HEADER, "initial,1.8056,26.82,107.7,0.1611,0.4111,0.1333,0.2944,-0.535",
				"0,0.5833,13.71,54.3,0.0000,0.6444,0.3278,0.0278,");
		stats(2, RunStats.HEADER);
		Files.createDirectories(new File(folder, "Not an attempt").toPath());

		final List<Attempt> attempts = SavedAttempts.all(folder);

		assertEquals(List.of(2, 1), attempts.stream().map(Attempt::number).toList());
		assertTrue(attempts.get(0).generations().isEmpty());
		final List<GenerationResult> generations = attempts.get(1).generations();
		assertEquals(new GenerationResult("initial", 1.8056, 26.82, 107.7, 0.1611, 0.4111, 0.1333, 0.2944, -0.535),
				generations.get(0));
		assertNull(generations.get(1).pointsAgainstReference());
		assertEquals(-0.535, attempts.get(1).bestPointsAgainstReference());
		assertNull(attempts.get(0).bestPointsAgainstReference());
	}

	@Test
	public void columnsAreFoundByName() throws IOException {
		stats(1, "generation,pointsAgainstReference", "10,0.300");

		final GenerationResult row = SavedAttempts.all(folder).get(0).generations().get(0);

		assertEquals("10", row.generation());
		assertEquals(0.3, row.pointsAgainstReference());
		assertTrue(Double.isNaN(row.bestFitness()));
	}

	@Test
	public void statsThatCantBeReadGiveTheRowsBeforeTheMistake() throws IOException {
		stats(1, "generation,pointsAgainstReference", "0,0.1", "1,oops", "2,0.3");
		stats(2, "something,else", "1,2");

		final List<Attempt> attempts = SavedAttempts.all(folder);

		assertTrue(attempts.get(0).generations().isEmpty());
		assertEquals(1, attempts.get(1).generations().size());
	}

	@Test
	public void theSettingsSavedAreReadBackInOrder() throws IOException {
		final File attempt = new File(folder, "Attempt - 1");
		Files.createDirectories(attempt.toPath());
		GenAlgSettings.setPopulationSize(12);
		GenAlgSettings.setGenerations(34);
		final Instant started = Instant.parse("2026-11-07T15:39:44Z");

		SavedAttempts.saveSettings(attempt.getPath(), started);

		final Attempt saved = SavedAttempts.all(folder).get(0);
		assertEquals(started, saved.started());
		assertEquals(List.of("populationSize", "generations", "gamesPerPairing", "tournamentSize", "uniformRate",
				"mutationRate", "elitism"), List.copyOf(saved.settings().keySet()));
		assertEquals("12", saved.settings().get("populationSize"));
		assertEquals("34", saved.settings().get("generations"));
	}

	@Test
	public void anAttemptFromBeforeSettingsWereSavedHasNone() throws IOException {
		stats(1, RunStats.HEADER);

		final Attempt attempt = SavedAttempts.all(folder).get(0);

		assertTrue(attempt.settings().isEmpty());
		assertNotNull(attempt.started(), "it should go by when the folder was made");
	}

	@Test
	public void aPlayerThatWasntSavedIsNull() throws IOException {
		stats(1, RunStats.HEADER);

		assertNull(SavedAttempts.all(folder).get(0).player("5"));
	}

	@Test
	public void withNothingSavedThereAreNoAttempts() {
		assertTrue(SavedAttempts.all(folder).isEmpty());
		assertTrue(SavedAttempts.all(new File(folder, "not there")).isEmpty());
	}

	private void stats(final int attempt, final String... lines) throws IOException {
		final File dir = new File(folder, "Attempt - " + attempt);
		Files.createDirectories(dir.toPath());
		Files.writeString(new File(dir, SavedAttempts.STATS_FILE).toPath(), String.join("\n", lines) + "\n");
	}
}
