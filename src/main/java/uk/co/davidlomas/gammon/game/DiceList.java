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

package uk.co.davidlomas.gammon.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Class DiceList.
 *
 * Container for the Dice rolls, allows the looping through the moves left
 * during possible move creation
 *
 * could have created this code inside another class but I moved it out to make
 * space.
 *
 * @author David Lomas
 */
class DiceList {

  /**
   * The dice.
   */
  private final Die dice1;
  private final Die dice2;

  /**
   * Instantiates a new dice list.
   */
  DiceList() {
    dice1 = new Die();
    dice2 = new Die();
  }

  int rollOneDie() {
    return dice1.RollDie();
  }

  /**
   * Roll dice. Rolls two random dice and returns the results
   *
   * @return list of Dice rolls
   */
  List<Integer> RollTwoDice() {

    final List<Integer> diceRolls = new ArrayList<>();

    // roll dice
    diceRolls.add(dice1.RollDie());
    diceRolls.add(dice2.RollDie());

    // add 2 extra if there is a double roll
    if (Objects.equals(diceRolls.get(0), diceRolls.get(1))) {
      diceRolls.add(diceRolls.get(0));
      diceRolls.add(diceRolls.get(0));
    }
    return diceRolls;
  }
}