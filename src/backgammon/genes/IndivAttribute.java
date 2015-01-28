package backgammon.genes;

public class IndivAttribute {
	


	private String name = "";
	private int value = 0;
	
	public IndivAttribute(String n){
		name = n;
		value = (int) (Math.random()*100);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
