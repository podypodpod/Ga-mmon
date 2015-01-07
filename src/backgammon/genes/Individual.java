package backgammon.genes;


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
}