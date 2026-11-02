package uk.co.davidlomas.gammon.gui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
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
			// closing it here only closes it: the real window ends the program, which would end the test run
			SwingUtilities.invokeAndWait(() -> {
				frame.run();
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			});

			assertTrue(frame.isShowing(), "the frame itself should be the window that opens");
			assertEquals("GA-mmon", frame.getTitle());
			assertSame(frame, SwingUtilities.getWindowAncestor(frame.mmp));
		} finally {
			SwingUtilities.invokeAndWait(frame::dispose);
		}
	}
}
