package backgammon.genes;


/**
 * IndivAttribute
 * 
 * attribute for the individual, each will have a number of these e.g. bear a piece
 * 
 * Controls the particular chance of doing a certain strategy
 * 
 * @author David Lomas - 11035527
 */
public class IndivAttribute {

	private String name = "";
	private int value = 0;
	
	/**
	 * IndivAttribute
	 * 
	 * @param n - the name
	 */
	public IndivAttribute(String n){
		name = n;
		value = (int) (Math.random()*100);
	}
	
	/**
	 * getName
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * getValue
	 * 
	 * @return value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * setValue
	 * 
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	@Override
	public String toString(){
		return name+": "+value;
	}
}