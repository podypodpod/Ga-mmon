package uk.co.davidlomas.gammon.gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.gui.SearchSettingsDialog.SearchSettings;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class SearchSettingsDialogTest {

	private static final SearchSettings CHOSEN = new SearchSettings(25, 12, 3, 4, 0.4, 0.05, false);

	@BeforeEach
	public void setUp() {
		SettingsUtil.resetSettings();
	}

	@AfterEach
	public void tearDown() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void theSettingsChosenAreTheOnesSearchesUse() {
		CHOSEN.apply();

		assertEquals(CHOSEN, SearchSettings.current());
		assertEquals(25, GenAlgSettings.getGenerations());
		assertEquals(3, GenAlgSettings.getGamesPerPairing());
	}

	@Test
	public void itSaysRoughlyHowManyGamesThereWillBe() {
		// 26 populations of 12, each with 66 pairs meeting 3 times, two games a meeting
		assertEquals("About 10,296 games for the fitness of 26 populations", SearchSettingsDialog.gamesText(CHOSEN));
	}

	@Test
	public void itStartsWithTheSettingsGivenAndStartChoosesWhatItShows() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		SwingUtilities.invokeAndWait(() -> {
			final SearchSettingsDialog dialog = new SearchSettingsDialog(null, CHOSEN);
			try {
				assertEquals(CHOSEN, dialog.settings());
				assertNull(dialog.chosen(), "nothing is chosen until Start is pressed");

				final SearchSettings changed = new SearchSettings(5, 6, 1, 2, 0.5, 0.01, true);
				dialog.show(changed);
				dialog.choose();

				assertEquals(changed, dialog.chosen());
			} finally {
				dialog.dispose();
			}
		});
	}

	@Test
	public void cancellingChoosesNothing() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		SwingUtilities.invokeAndWait(() -> {
			final SearchSettingsDialog dialog = new SearchSettingsDialog(null, CHOSEN);
			dialog.dispose();
			assertNull(dialog.chosen());
		});
	}
}
