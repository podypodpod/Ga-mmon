package backgammon.genes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


public class Individual {

	
	private int agressionChance = 0;
	private int defensiveChance = 0;
	private int technicalChance = 0;
	private int randomChance = 0;
	
	private char[] chromosome;
	
	private int numOfAtrributes = 4;
	
	private double fitness = 0;
	
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
	
	public double getFitness(){
		return fitness;
	}
	
	public void setFitness(double fit){
		fitness = fit;
	}

	public int getAgressionChance() {
		updateFromBinary();
		return agressionChance;
	}
	
	public int getRandomChance(){
		updateFromBinary();
		return randomChance;
	}
	
	public int getTechnicalChance(){
		updateFromBinary();
		return technicalChance;
	}
	
	public int getDefensiveChance(){
		updateFromBinary();
		return defensiveChance;
	}
	
	public int getNumOfAttributes(){
		return numOfAtrributes;
	}

	public void setAgressionChance(int ac) {
		agressionChance = ac;
		updateToBinary();
	}


	public void setDefensiveChance(int dc) {
		defensiveChance = dc;
		updateToBinary();
	}
	
	public void setTechnicalChance(int tc){
		technicalChance = tc;
		updateToBinary();
	}
	
	public void setRandomChance(int rc){
		randomChance = rc;
		updateToBinary();
	}
	
	public String toString(){
		return "Indiviudal with chromosome string: "+String.valueOf(chromosome) +" and fitness of: "+fitness+". |Agr: "+getAgressionChance()+"|Def: "+getDefensiveChance() + "|Tech: "+getTechnicalChance()+"|Ran: "+getRandomChance()+"|";
	}

	public char[] getChromosome() {
		return chromosome;
	}
	
	private void updateToBinary() {
		int[] perArray = {agressionChance,defensiveChance,technicalChance,randomChance};
		chromosome = Util.convertFromIntToBinaryCharAry(perArray);
	}
	private void updateFromBinary() {
		int[] newAtrributes = Util.convertFromBinaryStringsToIntAr(String.valueOf(chromosome), 4, 7);
		agressionChance = newAtrributes[0];
		defensiveChance = newAtrributes[1];
		technicalChance = newAtrributes[2];
		randomChance = newAtrributes[3];
	}
	
	
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