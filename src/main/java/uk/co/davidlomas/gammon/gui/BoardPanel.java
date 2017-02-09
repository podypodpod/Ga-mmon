/*
  GNU General Public License

  This file is part of GA-mmon.

  GA-mmon is free software: you can redistribute it and/or modify it under the
  terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
  A PARTICULAR PURPOSE. See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with
  GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import uk.co.davidlomas.gammon.game.Point;

/**
 * BoardPanel
 *
 * @author David Lomas
 */
public class BoardPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private Point[] points = null;
  private int redBore, blackBore;
  private Image backgroundImage;

  BoardPanel() {
    setSize(1169, 637);
  }

  @Override
  public void paintComponent(final Graphics graphics) {

    super.paintComponent(graphics);
    setBackground(Color.WHITE);
    final Graphics2D graphics2D = (Graphics2D) graphics;

    // setting font for the drawn strings on the game
    graphics2D.setFont(new Font("default", Font.BOLD, 16));

    // Drawing background
    try {
      final InputStream is = getClass().getResourceAsStream("/blank_board.png");
      backgroundImage = ImageIO.read(is);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    graphics2D.drawImage(backgroundImage, 0, 0, this);

    // Drawing checkers on board
    // loop through all points
    for (int i = 0; i < points.length; i++) {
      // check if its blank, end if it is
      if (points[i].getBlackCount() != 0 || points[i].getRedCount() != 0) {
        // check colour
        if (points[i].getBlackCount() != 0) {
          graphics2D.setColor(Color.BLACK);
        } else {
          graphics2D.setColor(Color.RED);
        }

        // print 0 point checkers
        if (i == 0 || i == 25) {

          // deciding on the side
          if (i == 0) {

            graphics2D.fillOval(510, 150, 50, 50);
            graphics2D.setColor(Color.WHITE);
            graphics2D.drawOval(510, 150, 50, 50);
            graphics2D.drawString(points[i].numEither() + "", 532, 180);

          } else {

            graphics2D.fillOval(510, 300, 50, 50);
            graphics2D.setColor(Color.WHITE);
            graphics2D.drawOval(510, 300, 50, 50);
            graphics2D.drawString(points[i].numEither() + "", 532, 330);

          }

          // print top right half checkers
        } else if (i > 0 && i < 7) {

          graphics2D.fillOval(990 - i * 70, 80, 50, 50);
          graphics2D.setColor(Color.WHITE);
          graphics2D.drawOval(990 - i * 70, 80, 50, 50);
          graphics2D.drawString(points[i].numEither() + "", 1010 - i * 70, 110);

          // print top left half checkers
        } else if (i > 6 && i < 13) {

          graphics2D.fillOval(500 - (i - 6) * 70, 80, 50, 50);
          graphics2D.setColor(Color.WHITE);
          graphics2D.drawOval(500 - (i - 6) * 70, 80, 50, 50);
          graphics2D.drawString(points[i].numEither() + "", 520 - (i - 6) * 70, 110);

          // print bottom left half checkers
        } else if (i > 12 && i < 19) {

          graphics2D.fillOval(20 + (i - 12) * 70, 430, 50, 50);
          graphics2D.setColor(Color.WHITE);
          graphics2D.drawOval(20 + (i - 12) * 70, 430, 50, 50);
          graphics2D.drawString(points[i].numEither() + "", 40 + (i - 12) * 70, 460);

          // print bottom right half checkers
        } else if (i > 18 && i < 25) {

          graphics2D.fillOval(500 + (i - 18) * 70, 430, 50, 50);
          graphics2D.setColor(Color.WHITE);
          graphics2D.drawOval(500 + (i - 18) * 70, 430, 50, 50);
          graphics2D.drawString(points[i].numEither() + "", 520 + (i - 18) * 70, 460);
        }
      }
    }

    // Drawing checkers that have been bored
    if (blackBore != 0) {
      graphics2D.setColor(Color.BLACK);
      graphics2D.fillOval(1050, 150, 50, 50);
      graphics2D.setColor(Color.WHITE);
      graphics2D.drawOval(1050, 150, 50, 50);
      graphics2D.drawString(blackBore + "", 1070, 180);

    }
    if (redBore != 0) {
      graphics2D.setColor(Color.RED);
      graphics2D.fillOval(1050, 390, 50, 50);
      graphics2D.setColor(Color.WHITE);
      graphics2D.drawOval(1050, 390, 50, 50);
      graphics2D.drawString(redBore + "", 1070, 420);
    }
  }

  /**
   * printCheckers
   *
   * method to call to print board
   */
  public void printCheckers(final Point[] points, final int redBore, final int blackBore) {
    this.points = points;
    this.redBore = redBore;
    this.blackBore = blackBore;
    repaint();
  }

}
