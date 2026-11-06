package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.davidlomas.gammon.genes.SavedPlayers.SavedPlayer;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SavedPlayersTest {

	@TempDir
	File folder;

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void itPicksTheHighestPointsAgainstTheReferenceAcrossAttempts() throws IOException {
		attempt(1, "initial,-0.100", "0,", "10,0.300");
		attempt(2, "initial,0.200", "10,0.500");
		save(1, "initial", 1);
		save(1, "10", 2);
		save(2, "initial", 3);
		save(2, "10", 4);

		final List<SavedPlayer> best = SavedPlayers.best(folder, 2);

		assertEquals(List.of("Attempt 2, generation 10", "Attempt 1, generation 10"), names(best));
		assertEquals(0.5, best.get(0).pointsAgainstReference());
		assertEquals(4, best.get(0).player().getAttribute(0).getValue(), "it should be the saved player's genes");
	}

	@Test
	public void itCanPickTheInitialPopulationsBest() throws IOException {
		attempt(1, "initial,0.100");
		save(1, "initial", 1);

		assertEquals(List.of("Attempt 1, initial population"), names(SavedPlayers.best(folder, 2)));
	}

	@Test
	public void theSameGenesOnlyCountOnce() throws IOException {
		attempt(1, "10,0.500", "20,0.400", "30,0.300");
		save(1, "10", 5);
		save(1, "20", 5);
		save(1, "30", 6);

		assertEquals(List.of("Attempt 1, generation 10", "Attempt 1, generation 30"), names(SavedPlayers.best(folder, 2)));
	}

	@Test
	public void playersThatCantBeUsedAreLeftOut() throws IOException {
		attempt(1, "10,0.900", "20,0.800", "30,0.700");
		// generation 10's file is missing
		Files.writeString(new File(folder, "Attempt - 1/" + SavedPlayers.fileName("20")).toPath(), "chromosome=101\n");
		save(1, "30", 1);
		// not stats at all, and stats with a mistake in
		Files.createDirectories(new File(folder, "Attempt - 2").toPath());
		Files.writeString(new File(folder, "Attempt - 2/runStats.csv").toPath(), "something,else\n1,2\n");
		attempt(3, "10,not a number");
		Files.createDirectories(new File(folder, "Not an attempt").toPath());

		assertEquals(List.of("Attempt 1, generation 30"), names(SavedPlayers.best(folder, 2)));
	}

	@Test
	public void withNothingSavedThereAreNoBestPlayers() {
		assertTrue(SavedPlayers.best(folder, 2).isEmpty());
		assertTrue(SavedPlayers.best(new File(folder, "not there"), 2).isEmpty());
	}

	private void attempt(final int number, final String... generationAndPoints) throws IOException {
		final File attempt = new File(folder, "Attempt - " + number);
		Files.createDirectories(attempt.toPath());
		final StringBuilder stats = new StringBuilder("generation,pointsAgainstReference\n");
		for (final String row : generationAndPoints) {
			stats.append(row).append('\n');
		}
		Files.writeString(new File(attempt, "runStats.csv").toPath(), stats.toString());
	}

	private void save(final int attempt, final String generation, final int firstGene) {
		final Individual player = new Individual();
		// a new player's genes differ, so these are all set, for the same firstGene to mean the same genes
		for (int gene = 0; gene < player.getNumOfAttributes(); gene++) {
			player.getAttribute(gene).setValue(50);
		}
		player.getAttribute(0).setValue(firstGene);
		player.saveToFile(new File(folder, "Attempt - " + attempt + "/" + SavedPlayers.fileName(generation)).getPath());
	}

	private static List<String> names(final List<SavedPlayer> players) {
		return players.stream().map(SavedPlayer::name).toList();
	}
}
