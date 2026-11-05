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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.davidlomas.gammon.game.MoveChooser;
import uk.co.davidlomas.gammon.settings.GenAlgSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class Individual.
 * <p>
 * The AI behind a player. Its genes are the weights it gives each feature of a board when choosing a move, one set
 * for while the players can hit each other and one for the race after: see MoveChooser
 *
 * @author David Lomas
 */
public class Individual {

	private final static Logger logger = LoggerFactory.getLogger(Individual.class);
	/**
	 * The saved players folder can be moved with -Dgammon.playersDir, the tests use this to keep out of savedPlayers/
	 */
	public static final String PLAYERS_DIR_PROPERTY = "gammon.playersDir";
	private static final String PLAYERS_DIR = System.getProperty(PLAYERS_DIR_PROPERTY, "savedPlayers");
	private static final String PLAYERS_FILE_PATH = PLAYERS_DIR + "/Attempt - ";
	private static final Pattern ATTEMPT_FOLDER = Pattern.compile("Attempt - (\\d{1,9})");
	private final static String[] AVAILABLE_ATTRIBUTES = MoveChooser.geneNames();


	private final IndividualAttribute[] attributes;
	private final int numOfAttributes;
	private char[] chromosome;
	private double fitness = 0;

	/**
	 * Default Constructor
	 * <p>
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
	 * The highest numbered attempt in the saved players folder
	 *
	 * @return its number, 0 if there are none
	 */
	public static int lastAttempt() {
		final File[] folders = new File(PLAYERS_DIR).listFiles(File::isDirectory);
		int last = 0;
		if (folders != null) {
			for (final File folder : folders) {
				final Matcher attempt = ATTEMPT_FOLDER.matcher(folder.getName());
				if (attempt.matches()) {
					last = Math.max(last, Integer.parseInt(attempt.group(1)));
				}
			}
		}
		return last;
	}

	/**
	 * Gets file path for the players directory from the properties
	 * <p>
	 * If it doesn't exist create the directory
	 */
	public String getFilePathForPlayers() {

		final String savedPlayersDirectory = PLAYERS_FILE_PATH + GenAlgSettings.getAttemptCount();
		final boolean directoryExists = new File(savedPlayersDirectory).exists();

		if (!directoryExists) {
			logger.trace("Directory " + savedPlayersDirectory + " does not exist, making it");

			final boolean fileWasCreated = new File(savedPlayersDirectory).mkdirs();
			if (!fileWasCreated) {
				logger.error("Failed creating file {}", savedPlayersDirectory);
			}
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
	 *
	 * @throws UncheckedIOException if it can't be read, rather than leaving this player with the genes it had, which
	 *                              would look like the saved player but not play like it
	 */
	public void loadFromFile(final String fullFilePath) {
		logger.trace("Loading player from file: " + fullFilePath);
		final Properties properties = new Properties();

		try (InputStream input = new FileInputStream(fullFilePath)) {
			// load the file
			properties.load(input);

			// get the chromosome value. One saved with different genes, such as by the old rule-based players, can't
			// be read as these
			final String saved = properties.getProperty("chromosome");
			if (saved == null || saved.length() != chromosome.length) {
				throw new IllegalArgumentException(fullFilePath + " wasn't saved with these genes: its chromosome has "
						+ (saved == null ? "no" : saved.length()) + " bits, not " + chromosome.length);
			}
			chromosome = saved.toCharArray();

			// update to the rest of the AVAILABLE_ATTRIBUTES
			updateFromBinary();

		} catch (final IOException e) {
			throw new UncheckedIOException("Couldn't read " + fullFilePath, e);
		}
	}

	/**
	 * Save to file. It's written to a temporary file first and moved into place once it's complete, so if the program
	 * stops part way through, such as by closing the board window, there's never half a player in the file.
	 *
	 * @param fullFilePath the file name
	 *
	 * @throws UncheckedIOException if it can't be written, so a run stops rather than carrying on with nothing to show
	 *                              for it
	 */
	public void saveToFile(final String fullFilePath) {
		logger.trace("Saving player to file: " + fullFilePath);

		updateToBinary();
		final Properties properties = new Properties();

		// set the properties value
		properties.setProperty("chromosome", String.valueOf(chromosome));
		properties.setProperty("fitness", getFitness() + "");

		for (final IndividualAttribute individualAttribute : attributes) {
			properties.setProperty(individualAttribute.getName(), individualAttribute.getValue() + "");
		}

		final Path file = Path.of(fullFilePath);
		final Path partial = Path.of(fullFilePath + ".partial");
		try {
			try (OutputStream output = Files.newOutputStream(partial)) {
				// save the file to the savedPlayers folder
				properties.store(output, null);
			}
			Files.move(partial, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException e) {
			throw new UncheckedIOException("Couldn't save " + fullFilePath, e);
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
	 *
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
	 * <p>
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
	 * overridden toString
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
	/**
	 * Sets the genes from the chromosome, after its bits have been changed directly, as mutation does. 7 bits can
	 * hold up to 127 but a gene only goes to 100, so the chromosome is then rebuilt from the genes to match them
	 */
	void updateFromBinary() {
		final int[] values = Util.convertFromBinaryStringsToIntAr(String.valueOf(chromosome), numOfAttributes);
		for (int x = 0; x < values.length; x++) {
			attributes[x].setValue(values[x]);
		}
		updateToBinary();
	}

	/**
	 * Update from values to binary.
	 */
	private void updateToBinary() {
		chromosome = Util.convertFromIntArrayToBinaryCharAry(attributes);
	}

}
