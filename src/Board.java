
public class Board {

	public static Point[] Points;
	
	public Board(){
		//Creating 26 points
		Points = new Point[26];
		//creating the blank objects
		for(int x = 0; x<Points.length;x++){
			Points[x] = new Point();
		}
		//ressetting all to 0
		setStartPosition();
	}
	
	public void setStartPosition(){
		//Resetting all to 0
		for(int x =0; x<Points.length;x++){
			Points[x].setBlackCount(0);
			Points[x].setRedCount(0);
		}

		//completing the starting position of the checkers
		//Red Checkers
		Points[1].setRedCount(2);
		Points[12].setRedCount(5);
		Points[17].setRedCount(3);
		Points[19].setRedCount(5);
		//Black Checkers
		Points[6].setBlackCount(5);
		Points[8].setBlackCount(3);
		Points[13].setBlackCount(5);
		Points[24].setBlackCount(2);
	}
	
	public void printBoardList(){
		
		for(int x = 0; x<Points.length;x++){
			System.out.println("Point "+x+" || "+ Points[x].getRedCount()+"r-"+Points[x].getBlackCount()+"b");

		}
	}
	public void printBoard(){
		System.out.println("|---------------------------------------|");
		System.out.println("|  Black 0 = "+ Points[0].getBlackCount()+"        |"  + "  Beared: 0      |");
		System.out.println("|---------------------------------------|");
		System.out.println("|NUM| 1| 2| 3| 4| 5| 6| 7| 8| 9|10|11|12|");
		System.out.print("|RED");
		for(int x = 1; x<=12;x++){
			System.out.print("| "+Points[x].getRedCount());
		}
		System.out.println("|");
		System.out.print("|BLK");
		for(int x = 1; x<=12;x++){
			System.out.print("| "+Points[x].getBlackCount());
		}
		System.out.println("|");
		System.out.println("|---------------------------------------|");
		System.out.println("|NUM|13|14|15|16|17|18|19|20|21|22|23|24|");
		System.out.print("|RED");
		for(int x = 13; x<=24;x++){
			System.out.print("| "+Points[x].getRedCount());
		}
		System.out.println("|");
		System.out.print("|BLK");
		for(int x = 13; x<=24;x++){
			System.out.print("| "+Points[x].getBlackCount());
		}
		System.out.println("|");
		System.out.println("|---------------------------------------|");
		System.out.println("|  Red 0 = "+ Points[25].getRedCount()+"          |"+ "  Beared: 0      |");
		System.out.println("|---------------------------------------|");
	}
}
