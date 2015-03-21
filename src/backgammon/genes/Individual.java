	package backgammon.genes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import backgammon.settings.GenAlgSettings;


/**
 * The Class Individual.
 */
public class Individual {
	
	//number of attributes
	private int numOfAttributes;
	
	//Attributes	
	private IndivAttribute[] listOfAttributes = null;
	
	/** The chromosome. */
	private char[] chromosome;
	
	/** The fitness. */
	private double fitness = 0;
	
	/**
	 * Instantiates a new individual.
	 */
	public Individual(){

		//add attributes to the array
		listOfAttributes = new IndivAttribute[]{
				new IndivAttribute("bearAPiece"), 					//0
				new IndivAttribute("takeAPiece"), 					//1
				new IndivAttribute("doubleUpAPiece"), 				//2
				new IndivAttribute("blockAnOpponent"),				//3
				new IndivAttribute("movingAPieceSolo"),				//4
				new IndivAttribute("spreadAHomePiece"),				//5
				new IndivAttribute("addACheckerToAStack"),			//6
				new IndivAttribute("twoOneSplitPlayInitialMove"),	//7
				new IndivAttribute("twoOneSlotPlayInitialMove"),	//8
				new IndivAttribute("threeOneInitialMove"),			//9
				new IndivAttribute("threeTwoSplitInitialMove"),		//10
				new IndivAttribute("threeTwoOffenceInitialMove"),	//11
				new IndivAttribute("fourOneInitialMove"),			//12
				new IndivAttribute("fourOneInitialMoveAlt"),		//13
				new IndivAttribute("fourTwoInitialMove"),			//14
				new IndivAttribute("fourThreeInitialMoveSplit"),	//15
				new IndivAttribute("fourThreeInitialMoveBlock"),	//16
				new IndivAttribute("fiveOneInitialMove"),			//17
				new IndivAttribute("fiveOneInitialMoveAlt"),		//18
				new IndivAttribute("fiveTwoInitialMove"),			//19
				new IndivAttribute("fiveTwoInitialMoveRisk")		//20
		};
		
		numOfAttributes = listOfAttributes.length;
		
		//convert to binary string
		chromosome = Util.convertFromIntToBinaryCharAry(listOfAttributes);
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
	 * Gets the number of attributes.
	 *
	 * @return the number of attributes
	 */
	public int getNumOfAttributes(){
		return numOfAttributes;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String result = "Indiviudal with chromosome string: "+String.valueOf(chromosome) +" and fitness of: "+fitness;
		for(int x = 0; x<listOfAttributes.length; x++){
			result+="| "+listOfAttributes[x].getName()+": "+listOfAttributes[x].getValue();
		}
		return result;
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
	public void updateToBinary() {
		chromosome = Util.convertFromIntToBinaryCharAry(listOfAttributes);
	}
	
	/**
	 * Update from binary.
	 */
	private void updateFromBinary() {
		int[] values = Util.convertFromBinaryStringsToIntAr(String.valueOf(chromosome), numOfAttributes, 7);
		for(int x = 0; x<values.length; x++){
			listOfAttributes[x].setValue(values[x]);
		}
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
			input = new FileInputStream(GenAlgSettings.getSavePath()+fileName+".properties");
	 
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
		
		updateToBinary();
		Properties properties = new Properties();
		OutputStream output = null;
	 
		try {
	 
			output = new FileOutputStream(GenAlgSettings.getSavePath()+fileName+".properties");
	
			// set the properties value
			properties.setProperty("chromosome", String.valueOf(chromosome));
			properties.setProperty("fitness", getFitness()+"");
			for(IndivAttribute x : listOfAttributes){
				properties.setProperty(x.getName(), x.getValue()+"");
			}

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
	
	
	public void setAttribute(int attr, IndivAttribute data){
		listOfAttributes[attr] = data;
		updateToBinary();
	}
	public IndivAttribute getAttribute(int pos){
		return listOfAttributes[pos];
	}
}
