package uk.co.davidlomas.gammon.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.davidlomas.gammon.genes.Individual;
import uk.co.davidlomas.gammon.genes.SavedAttempts.Attempt;
import uk.co.davidlomas.gammon.genes.SearchProgress.GenerationResult;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class PlayersFrameTest {

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
	public void theSettingsAreSaidInWords() {
		final Map<String, String> settings = new LinkedHashMap<>();
		settings.put("populationSize", "40");
		settings.put("generations", "60");
		settings.put("elitism", "true");
		settings.put("somethingNew", "7");

		assertEquals("Ran with a population of 40, 60 generations, the fittest kept each generation, somethingNew 7",
				PlayersFrame.settingsText(settings));
		assertTrue(PlayersFrame.settingsText(Map.of()).contains("weren't saved"));
	}

	@Test
	public void aGenesFeatureIsSaidInWords() {
		assertEquals("Pip lead", PlayersFrame.words("PipLead"));
		assertEquals("Opponent on bar", PlayersFrame.words("OpponentOnBar"));
	}

	@Test
	public void eachFeatureHasItsContactAndRaceWeights() {
		final Individual player = new Individual();
		final PlayersFrame.GenesModel genes = new PlayersFrame.GenesModel();

		genes.show(player);

		final int contactGene = indexOf(player, "contactPipLead");
		final int raceGene = indexOf(player, "racePipLead");
		final int row = rowOf(genes, "Pip lead");
		assertEquals(String.valueOf(player.getAttribute(contactGene).getValue()), genes.getValueAt(row, 1));
		assertEquals(String.valueOf(player.getAttribute(raceGene).getValue()), genes.getValueAt(row, 2));
		// blots only matter while the players can hit each other
		assertEquals("", genes.getValueAt(rowOf(genes, "Blots"), 2));

		genes.show(null);
		assertEquals(0, genes.getRowCount());
	}

	@Test
	public void theAttemptsTableSaysHowEachWent() {
		final PlayersFrame.AttemptsModel model = new PlayersFrame.AttemptsModel(List.of(attempt(3, Map.of())));

		assertEquals(3, model.getValueAt(0, 0));
		assertEquals(2L, model.getValueAt(0, 2), "the initial population isn't a generation");
		assertEquals("+0.553", model.getValueAt(0, 3));
		assertEquals("1 attempt, 3 generations with a best player each",
				PlayersFrame.summaryText(List.of(attempt(3, Map.of()))));
	}

	@Test
	public void pickingAnAttemptAndAGenerationShowsItsPlayer() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final Individual saved = new Individual();
		saved.saveToFile(new File(folder, "PlayerFromGen1").getPath());
		final AtomicInteger menuShown = new AtomicInteger();
		final PlayersFrame[] frame = new PlayersFrame[1];
		SwingUtilities.invokeAndWait(() -> {
			frame[0] = new PlayersFrame(List.of(attempt(2, Map.of("populationSize", "40")), attempt(1, Map.of())),
					menuShown::incrementAndGet);
			frame[0].setVisible(true);
		});
		try {
			SwingUtilities.invokeAndWait(() -> {
				// the newest attempt and its latest generation, to begin with
				assertEquals("Ran with a population of 40", frame[0].settingsShown());
				assertTrue(frame[0].genesTitle().contains("Attempt 2, generation 1"), frame[0].genesTitle());
				assertEquals(String.valueOf(saved.getAttribute(0).getValue()), frame[0].genes().getValueAt(0, 1));

				// generation 0 wasn't saved
				frame[0].selectGeneration(1);
				assertTrue(frame[0].genesTitle().contains("wasn't saved"), frame[0].genesTitle());
				assertEquals(0, frame[0].genes().getRowCount());

				frame[0].selectAttempt(1);
				assertTrue(frame[0].settingsShown().contains("weren't saved"), frame[0].settingsShown());

				frame[0].back();
			});
			assertEquals(1, menuShown.get());
			assertFalse(frame[0].isDisplayable());
		} finally {
			SwingUtilities.invokeAndWait(frame[0]::dispose);
		}
	}

	private Attempt attempt(final int number, final Map<String, String> settings) {
		return new Attempt(number, folder, settings, Instant.parse("2026-11-07T15:39:44Z"), List.of(
				result("initial", -0.5), result("0", null), result("1", 0.553)));
	}

	private static GenerationResult result(final String generation, final Double points) {
		return new GenerationResult(generation, 1, 2, 3, 0.1, 0.2, 0.3, 0.4, points);
	}

	private static int indexOf(final Individual player, final String gene) {
		for (int index = 0; index < player.getNumOfAttributes(); index++) {
			if (player.getAttribute(index).getName().equals(gene)) {
				return index;
			}
		}
		throw new AssertionError("no " + gene);
	}

	private static int rowOf(final PlayersFrame.GenesModel genes, final String feature) {
		for (int row = 0; row < genes.getRowCount(); row++) {
			if (feature.equals(genes.getValueAt(row, 0))) {
				return row;
			}
		}
		throw new AssertionError("no " + feature);
	}
}
