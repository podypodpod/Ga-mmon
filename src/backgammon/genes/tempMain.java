package backgammon.genes;

public class tempMain {

	public static void main(String[] args) {

		Population pop = new Population(100);
		
		System.out.println("Fitess = "+pop.getFittest().toString());
	}
}
