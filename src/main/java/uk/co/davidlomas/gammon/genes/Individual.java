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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

/**
 * The Class Individual.
 *
 * The AI behind a player, contains AVAILABLE_ATTRIBUTES which dictates which decisions
 * they make
 *
 * @author David Lomas
 */
public class Individual {

  private final static Logger logger = LoggerFactory.getLogger(Individual.class);
  private static final String PLAYERS_FILE_PATH = "savedPlayers/Attempt - ";


  private final static String[] AVAILABLE_ATTRIBUTES = {"bearAPiece", "takeAPiece", "doubleUpAPiece", "blockAnOpponent", "movingAPieceSolo",
      "spreadAHomePiece",
      "addACheckerToAStack", "twoOneSplitPlayInitialMove", "twoOneSlotPlayInitialMove", "threeOneInitialMove", "threeTwoSplitInitialMove",
      "threeTwoOffenceInitialMove", "fourOneInitialMove", "fourOneInitialMoveAlt", "fourTwoInitialMove", "fourThreeInitialMoveSplit",
      "fourThreeInitialMoveBlock", "fiveOneInitialMove", "fiveOneInitialMoveAlt", "fiveTwoInitialMove", "fiveTwoInitialMoveRisk",
      "fiveThreeInitialMove", "fiveFourInitialMoveAgr", "fiveFourInitialMoveBal", "sixOneInitialMove", "sixTwoInitialMove", "sixTwoInitialMoveAgr",
      "sixThreeInitialMove", "sixThreeInitialMoveSplit", "sixFourInitialMove", "sixFourInitialMoveSplit", "sixFiveInitialMove"};


  private final IndividualAttribute[] attributes;
  private final int numOfAttributes;
  private char[] chromosome;
  private double fitness = 0;

  /**
   * Default Constructor
   *
   * Instantiates a new individual.
   */
  public Individual() {
    numOfAttributes = AVAILABLE_ATTRIBUTES.length;
    attributes = new IndividualAttribute[numOfAttributes];
    for (int x = 0; x < numOfAttributes; x++) {
      attributes[x] = new IndividualAttribute(AVAILABLE_ATTRIBUTES[x]);
    }

    // convert to binary string
    chromosome = Util.convertFromIntArrayToBinaryCharAry(attributes);
  }

  /**
   * getAttribute
   */
  public IndividualAttribute getAttribute(final int pos) {
    return attributes[pos];
  }

  /**
   * Gets the chromosome.
   *
   * @return the chromosome
   */
  public char[] getChromosome() {
    return chromosome;
  }

  /**
   * Gets file path for the players directory from the properties
   *
   * If it doesn't exist create the directory
   */
  public String getFilePathForPlayers() {
    final String savedPlayersDirectory = PLAYERS_FILE_PATH + GenAlgSettings.getAttemptCount();
    final boolean directoryExists = new File(savedPlayersDirectory).exists();
    if (!directoryExists) {
      logger.trace("Directory " + savedPlayersDirectory + " does not exist, making it");
      new File(savedPlayersDirectory).mkdir();
    }
    return savedPlayersDirectory;
  }

  /**
   * Gets the fitness.
   *
   * @return the fitness
   */
  public double getFitness() {
    return fitness;
  }

  /**
   * Gets the number of AVAILABLE_ATTRIBUTES.
   *
   * @return the number of AVAILABLE_ATTRIBUTES
   */
  public int getNumOfAttributes() {
    return numOfAttributes;
  }

  /**
   * Load from file.
   *
   * @param fullFilePath the file name
   */
  public void loadFromFile(final String fullFilePath) {
    logger.trace("Loading player from file: " + fullFilePath);
    final Properties properties = new Properties();

    try (InputStream input = new FileInputStream(fullFilePath)) {
      // load the file
      properties.load(input);

      // get the chromosome value
      chromosome = properties.getProperty("chromosome").toCharArray();

      // update to the rest of the AVAILABLE_ATTRIBUTES
      updateFromBinary();

    } catch (final IOException ex) {
      logger.error("Failed trying to load from file", ex);
    }
  }

  /**
   * Save to file.
   *
   * @param fullFilePath the file name
   */
  public void saveToFile(final String fullFilePath) {
    logger.trace("Saving player to file: " + fullFilePath);

    updateToBinary();
    final Properties properties = new Properties();

    try (OutputStream output = new FileOutputStream(fullFilePath)) {

      // set the properties value
      properties.setProperty("chromosome", String.valueOf(chromosome));
      properties.setProperty("fitness", getFitness() + "");

      for (final IndividualAttribute individualAttribute : attributes) {
        properties.setProperty(individualAttribute.getName(), individualAttribute.getValue() + "");
      }

      // save the file to the savedPlayers folder
      properties.store(output, null);

    } catch (final IOException io) {
      logger.error("Failed trying to Save to file", io);
    }
  }

  /**
   * setAttribute
   */
  public void setAttribute(final int index, final IndividualAttribute individualAttribute) {
    attributes[index] = individualAttribute;
    updateToBinary();
  }


  /**
   * overridden equals method
   *
   * @param otherObject other object
   * @return the result
   */
  @Override
  public boolean equals(final Object otherObject) {
    if (this == otherObject) {
      return true;
    }
    if (otherObject == null || getClass() != otherObject.getClass()) {
      return false;
    }

    final Individual that = (Individual) otherObject;

    return Double.compare(that.fitness, fitness) == 0 && Arrays.equals(chromosome, that.chromosome);
  }

  /**
   * overridden hashcode
   *
   * auto-generated
   */
  @Override
  public int hashCode() {
    int result;
    final long temp;
    result = Arrays.hashCode(chromosome);
    temp = Double.doubleToLongBits(fitness);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  /**
   * Sets the fitness.
   *
   * @param fit the new fitness
   */
  public void setFitness(final double fit) {
    fitness = fit;
  }

  /**
   * overridden tostring
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String result = "Individual with chromosome string: " + String.valueOf(chromosome) + " and fitness of: "
        + fitness;
    for (final IndividualAttribute listOfAttribute : attributes) {
      result += "| " + listOfAttribute.getName() + ": " + listOfAttribute.getValue();
    }
    return result;
  }

  /**
   * Update from binary to values.
   */
  private void updateFromBinary() {
    final int[] values = Util.convertFromBinaryStringsToIntAr(String.valueOf(chromosome), numOfAttributes, 7);
    for (int x = 0; x < values.length; x++) {
      attributes[x].setValue(values[x]);
    }
  }

  /**
   * Update from values to binary.
   */
  public void updateToBinary() {
    chromosome = Util.convertFromIntArrayToBinaryCharAry(attributes);
  }


}
