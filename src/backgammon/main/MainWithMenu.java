package backgammon.main;

import backgammon.gui.MainMenuContainerFrame;

public class MainWithMenu {

	public static void main(final String args[]) {

		// The Gui Thread
		final MainMenuContainerFrame menu = new MainMenuContainerFrame();
		final Thread guiThread = new Thread(menu);
		guiThread.start();
	}
}