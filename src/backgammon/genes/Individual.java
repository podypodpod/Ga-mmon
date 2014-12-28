package backgammon.genes;

public class Individual {

	double howGood;
	
	
	public Individual(){
		howGood = Math.random();
		System.out.println("Indivdual created with gooness of: "+howGood);
	}
	
	public double getFitness(){
		return howGood;
	}
	
	public String toString(){
		return "Individual with goodness: "+howGood;
	}
}
