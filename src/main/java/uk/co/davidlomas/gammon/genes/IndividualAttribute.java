/*
 * GNU General Public License
 *
 * This file is part of GA-mmon.
 *
 * GA-mmon is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.davidlomas.gammon.genes;

/**
 * IndivAttribute
 *
 * attribute for the individual, each will have a number of these e.g. bear a
 * piece
 *
 * Controls the particular chance of doing a certain strategy
 *
 * @author David Lomas
 */
public class IndividualAttribute {

  private String name = "";
  private int value = 0;

  public IndividualAttribute(final String name) {
    this.name = name;
    value = (int) (Math.random() * 100);
  }

  @SuppressWarnings("SameParameterValue")
  public IndividualAttribute(final String name, final int value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public int getValue() {
    return value;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setValue(final int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return name + ": " + value;
  }
}