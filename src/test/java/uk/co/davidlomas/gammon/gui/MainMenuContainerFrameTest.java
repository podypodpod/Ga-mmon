package uk.co.davidlomas.gammon.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MainMenuContainerFrameTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void itOpensItselfWithTheMenuInside() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final MainMenuContainerFrame frame = new MainMenuContainerFrame();
		try {
			SwingUtilities.invokeAndWait(frame::run);

			assertTrue(frame.isShowing(), "the frame itself should be the window that opens");
			assertEquals("GA-mmon", frame.getTitle());
			assertSame(frame, SwingUtilities.getWindowAncestor(frame.mmp));
			// exiting wouldn't wait for a search that's still running
			assertEquals(WindowConstants.DISPOSE_ON_CLOSE, frame.getDefaultCloseOperation());
		} finally {
			SwingUtilities.invokeAndWait(frame::dispose);
		}
	}

	@Test
	public void mainOpensTheMenu() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");

		MainMenuContainerFrame.main(new String[0]);
		// main only queues the window to be opened, so this waits for that to have happened
		SwingUtilities.invokeAndWait(() -> {
		});

		boolean opened = false;
		for (final Frame frame : Frame.getFrames()) {
			if (frame instanceof MainMenuContainerFrame && frame.isShowing()) {
				opened = true;
				SwingUtilities.invokeAndWait(frame::dispose);
			}
		}
		assertTrue(opened, "main should have opened the menu");
	}
}
