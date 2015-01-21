package backgammon.genes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


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
		FitnessCalculator.getFitnessOf(this);
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
		
	    BufferedReader br = null;
	    String retreived;
	    
		try {
			br = new BufferedReader(new FileReader("savedPlayers/"+fileName+".txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        
	        retreived = sb.toString();
	    } finally {
	        try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    //Got the data and closed the connection
	    String[] sections = retreived.split(": ");
	    System.out.println(sections[0]);
	    System.out.println(sections[1]);
	    System.out.println(sections[2]);
	    System.out.println(sections[3]);
	    System.out.println(sections[4]);
	    System.out.println(sections[5]);
	    //TODO: Actually set the vars to the indv, currently changing the vars so do this after
	}
	
	
	public void saveToFile(String fileName){
		
		if(fileName.equals("")||fileName==null){
			fileName="";
		}
		
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter(new FileWriter("savedPlayers/"+fileName+".txt"));
		    writer.write(toString());

		}catch (IOException e){}
		finally{
		    try{
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( IOException e){}
		}
	}
}