/**
 * 	GNU General Public License
 * 
 *  This file is part of GA-mmon.
 *  
 *  GA-mmon is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  GA-mmon is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with GA-mmon.  If not, see <http://www.gnu.org/licenses/>.
*/

package backgammon.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * ContainerFrame
 * 
 * contains the other frames
 * 
 * This is not currently needed, I could have put this in to the board panel
 * class but Its here for future changes if I add buttons to the GUI it will go
 * in to another class
 * 
 * @author David Lomas - 11035527
 */
public class BoardContainerFrame extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;

	public BoardPanel bp;
	JPanel mainPanel;
	JFrame frame;

	@Override
	public void run() {
		createGUI();
	}

	public void createGUI() {

		// Creating JFrame and panel
		frame = new JFrame("GA-mmon");
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		// creating and adding the Board Panel
		bp = new BoardPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		mainPanel.add(bp, c);

		frame.add(mainPanel);
		frame.setSize(1166, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}