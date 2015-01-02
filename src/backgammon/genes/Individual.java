package backgammon.genes;


public class Individual {

	private double agressionChance = 0;
	private double defensiveChance = 0;
	private int numOfAtrributes = 2;
	private double fitness = 0;
	
	public Individual(){
		agressionChance = Math.random();
		defensiveChance = Math.random();
	}
	
	public double getFitness(){
		FitnessCalculator.getFitnessOf(this);
		return fitness;
	}
	
	public void setFitness(double fit){
		fitness = fit;
	}

	public double getAgressionChance() {
		return agressionChance;
	}
	
	public double getDefensiveChance(){
		return defensiveChance;
	}
	
	public int getNumOfAttributes(){
		return numOfAtrributes;
	}

	public void setAgressionChance(double ac) {
		agressionChance = ac;
	}
	public void setDefensiveChance(double dc) {
		defensiveChance = dc;
	}
	
	public String toString(){
		return "Indiviudal with agr:"+agressionChance+" and def:"+defensiveChance+", fitness of: "+fitness;
	}
}

