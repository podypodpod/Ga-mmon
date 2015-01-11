package backgammon.genes;

/**
 * The Class Util.
 */
public class Util {
	
	/**
	 * Convert from int to binary str.
	 *
	 * @param numberToConvert the number to convert
	 * @return the string
	 */
	public static String convertFromIntToBinaryStr (int numberToConvert){
		StringBuffer binaryString = new StringBuffer();
		
		binaryString.append(Integer.toBinaryString(numberToConvert));
		for(int n=binaryString.length(); n<7; n++) {
		                        binaryString.insert(0, "0");
		}
		return binaryString.toString();
	}
	
	/**
	 * Convert from int to binary char ary.
	 *
	 * @param numbersToConvert the numbers to convert
	 * @return the char[]
	 */
	public static char[] convertFromIntToBinaryCharAry (int[] numbersToConvert){
		StringBuffer binaryString = new StringBuffer();
		for(int x = 0; x<numbersToConvert.length;x++){
			binaryString.append(convertFromIntToBinaryStr(numbersToConvert[x]));
		}
		return binaryString.toString().toCharArray();
	}
	
	/**
	 * Convert from binary str to int.
	 *
	 * @param binaryStr the binary str
	 * @return the int
	 */
	public static int convertFromBinaryStrToInt(String binaryStr){
		int x = Integer.parseInt(binaryStr, 2);
		if(x<=100){return x;}
		else{return 100;}
	}
	
	/**
	 * Convert from binary strings to int ar.
	 *
	 * @param binaryStr the binary str
	 * @param numOfBlocks the num of blocks
	 * @param lengthOfBlocks the length of blocks
	 * @return the int[]
	 */
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
