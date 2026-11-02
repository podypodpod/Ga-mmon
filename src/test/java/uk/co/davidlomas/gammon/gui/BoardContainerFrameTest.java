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

public class BoardContainerFrameTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void itOpensItselfWithTheBoardPanelInside() throws Exception {
		assumeFalse(GraphicsEnvironment.isHeadless(), "needs a screen");
		final BoardContainerFrame frame = new BoardContainerFrame();
		try {
			// closing it here only closes it: the real window ends the program, which would end the test run
			SwingUtilities.invokeAndWait(() -> {
				frame.run();
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			});

			assertTrue(frame.isShowing(), "the frame itself should be the window that opens");
			assertEquals("GA-mmon", frame.getTitle());
			assertSame(frame, SwingUtilities.getWindowAncestor(frame.bp));
		} finally {
			SwingUtilities.invokeAndWait(frame::dispose);
		}
	}
}
