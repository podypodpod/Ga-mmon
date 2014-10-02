
public class Point {

	int blackCount;
	int redCount;
	
	public Point(){
		blackCount = 0;
		redCount = 0;
	}
	
	//setBlackCount
	public void setBlackCount(int bc){
		blackCount = bc;
	}
	//setRedCount
	public void setRedCount(int rc){
		redCount = rc;
	}
	//removeBlackPeice
	public void removeBlackPeice(){
		blackCount--;
	}
	//removeRedPeice
	public void removeRedPeice(){
		redCount--;
	}
	//addBlackPeice
	public void addBlackPeice(){
		blackCount++;
	}
	//addRedPeice
	public void addRedPeice(){
		redCount++;
	}
	//getBlackCount
	public int getBlackCount(){
		return blackCount;
	}
	//getRedCount
	public int getRedCount(){
		return redCount;
	}
}
