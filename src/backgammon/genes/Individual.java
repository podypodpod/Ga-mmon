package backgammon.genes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


/**
 * The Class Individual.
 */
public class Individual {

	
	/** The agression chance. */
	private int agressionChance = 0;
	
	/** The defensive chance. */
	private int defensiveChance = 0;
	
	/** The technical chance. */
	private int technicalChance = 0;
	
	/** The random chance. */
	private int randomChance = 0;
	
	/** The chromosome. */
	private char[] chromosome;
	
	/** The num of atrributes. */
	private int numOfAtrributes = 4;
	
	/** The fitness. */
	private double fitness = 0;
	
	/**
	 * Instantiates a new individual.
	 */
	public Individual(){
		
		//randomly generate their attributes
		agressionChance = (int) (Math.random()*100);
		defensiveChance = (int) (Math.random()*100);
		technicalChance = (int) (Math.random()*100);
		randomChance = (int) (Math.random()*100);
		
		//convert to block of bite strings
		int[] perArray = {agressionChance,defensiveChance,technicalChance,randomChance};
		chromosome = Util.convertFromIntToBinaryCharAry(perArray);
		
	}
	
	/**
	 * Gets the fitness.
	 *
	 * @return the fitness
	 */
	public double getFitness(){
		return fitness;
	}
	
	/**
	 * Sets the fitness.
	 *
	 * @param fit the new fitness
	 */
	public void setFitness(double fit){
		fitness = fit;
	}

	/**
	 * Gets the agression chance.
	 *
	 * @return the agression chance
	 */
	public int getAgressionChance() {
		updateFromBinary();
		return agressionChance;
	}
	
	/**
	 * Gets the random chance.
	 *
	 * @return the random chance
	 */
	public int getRandomChance(){
		updateFromBinary();
		return randomChance;
	}
	
	/**
	 * Gets the technical chance.
	 *
	 * @return the technical chance
	 */
	public int getTechnicalChance(){
		updateFromBinary();
		return technicalChance;
	}
	
	/**
	 * Gets the defensive chance.
	 *
	 * @return the defensive chance
	 */
	public int getDefensiveChance(){
		updateFromBinary();
		return defensiveChance;
	}
	
	/**
	 * Gets the num of attributes.
	 *
	 * @return the num of attributes
	 */
	public int getNumOfAttributes(){
		return numOfAtrributes;
	}

	/**
	 * Sets the agression chance.
	 *
	 * @param ac the new agression chance
	 */
	public void setAgressionChance(int ac) {
		agressionChance = ac;
		updateToBinary();
	}


	/**
	 * Sets the defensive chance.
	 *
	 * @param dc the new defensive chance
	 */
	public void setDefensiveChance(int dc) {
		defensiveChance = dc;
		updateToBinary();
	}
	
	/**
	 * Sets the technical chance.
	 *
	 * @param tc the new technical chance
	 */
	public void setTechnicalChance(int tc){
		technicalChance = tc;
		updateToBinary();
	}
	
	/**
	 * Sets the random chance.
	 *
	 * @param rc the new random chance
	 */
	public void setRandomChance(int rc){
		randomChance = rc;
		updateToBinary();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "Indiviudal with chromosome string: "+String.valueOf(chromosome) +" and fitness of: "+fitness+". |Agr: "+getAgressionChance()+"|Def: "+getDefensiveChance() + "|Tech: "+getTechnicalChance()+"|Ran: "+getRandomChance()+"|";
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
	 * Update to binary.
	 */
	private void updateToBinary() {
		int[] perArray = {agressionChance,defensiveChance,technicalChance,randomChance};
		chromosome = Util.convertFromIntToBinaryCharAry(perArray);
	}
	
	/**
	 * Update from binary.
	 */
	private void updateFromBinary() {
		int[] newAtrributes = Util.convertFromBinaryStringsToIntAr(String.valueOf(chromosome), 4, 7);
		agressionChance = newAtrributes[0];
		defensiveChance = newAtrributes[1];
		technicalChance = newAtrributes[2];
		randomChance = newAtrributes[3];
	}
	
	
	/**
	 * Load from file.
	 *
	 * @param fileName the file name
	 */
	public void loadFromFile(String fileName){
	
		Properties properties = new Properties();
		InputStream input = null;
	 
		try {
			input = new FileInputStream("savedPlayers/"+fileName+".properties");
	 
			// load the file
			properties.load(input);
	 
			//get the chromosome value
			chromosome = properties.getProperty("chromosome").toCharArray();
			//update to the rest of the attributes
			updateFromBinary();
	 
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Save to file.
	 *
	 * @param fileName the file name
	 */
	public void saveToFile(String fileName){
		
		
		Properties properties = new Properties();
		OutputStream output = null;
	 
		try {
	 
			output = new FileOutputStream("savedPlayers/"+fileName+".properties");
	
			// set the properties value
			properties.setProperty("chromosome", String.valueOf(chromosome));
			properties.setProperty("fitness", getFitness()+"");
			properties.setProperty("agressionChance", agressionChance+"");
			properties.setProperty("defensiveChance", defensiveChance+"");
			properties.setProperty("technicalChance", technicalChance+"");
			properties.setProperty("randomChance", randomChance+"");
			
			//save the file to the savedPlayers folder
			properties.store(output, null);
	 
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
		}
	}
}
