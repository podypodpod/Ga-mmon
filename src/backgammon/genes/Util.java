package backgammon.genes;

public class Util {
	
	public static String convertFromIntToBinaryStr (int numberToConvert){
		StringBuffer binaryString = new StringBuffer();
		
		binaryString.append(Integer.toBinaryString(numberToConvert));
		for(int n=binaryString.length(); n<7; n++) {
		                        binaryString.insert(0, "0");
		}
		return binaryString.toString();
	}
	
	public static char[] convertFromIntToBinaryCharAry (int[] numbersToConvert){
		StringBuffer binaryString = new StringBuffer();
		for(int x = 0; x<numbersToConvert.length;x++){
			binaryString.append(convertFromIntToBinaryStr(numbersToConvert[x]));
		}
		return binaryString.toString().toCharArray();
	}
	
	public static int convertFromBinaryStrToInt(String binaryStr){
		int x = Integer.parseInt(binaryStr, 2);
		if(x<=100){return x;}
		else{return 100;}
	}
	
	public static int[] convertFromBinaryStringsToIntAr(String binaryStr,int numOfBlocks, int lengthOfBlocks){
		int[] nums = new int[numOfBlocks];
		
		int beginIndex =0;
		int endIndex =lengthOfBlocks;
		
		for(int x=0; x<numOfBlocks; x++){
			nums[x] = convertFromBinaryStrToInt(binaryStr.substring(beginIndex, endIndex));
			beginIndex+=lengthOfBlocks;
			endIndex+=lengthOfBlocks;
		}
		
		return nums;
	}
}
