public class Player {

	Boolean black;
	Die Die1, Die2;
	
	public Player(boolean b){
		
		black = b;
		
		Die1 = new Die();
		Die2 = new Die();
	
	}
	
	public void MovePeice(int from, int to){
		if(black = true){
			Board.Points[from].removeBlackPeice();
			Board.Points[to].addBlackPeice();
		}else{
			Board.Points[from].removeRedPeice();
			Board.Points[to].addRedPeice();
		}
	}
}
