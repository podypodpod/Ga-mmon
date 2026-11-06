package uk.co.davidlomas.gammon.genes;

/**
 * The Class Util.
 *
 * @author David Lomas
 */
class Util {

	private static final int LENGTH_OF_BLOCKS = 7;


	/**
	 * Convert from binary strings to int ar.
	 *
	 * @param binaryStr   the binary str
	 * @param numOfBlocks the num of blocks
	 *
	 * @return the int[]
	 */
	static int[] convertFromBinaryStringsToIntAr(final String binaryStr, final int numOfBlocks) {
		final int[] numbers = new int[numOfBlocks];

		int beginIndex = 0;
		int endIndex = LENGTH_OF_BLOCKS; // blocks = size of binary chars per var

		// loop the string
		for (int x = 0; x < numOfBlocks; x++) {
			numbers[x] = convertFromBinaryStrToInt(binaryStr.substring(beginIndex, endIndex));
			beginIndex += LENGTH_OF_BLOCKS;
			endIndex += LENGTH_OF_BLOCKS;
		}
		return numbers;
	}

	/**
	 * Convert from binary str to int.
	 *
	 * @param binaryStr the binary str
	 *
	 * @return the int
	 */
	private static int convertFromBinaryStrToInt(final String binaryStr) {
		final int number = Integer.parseInt(binaryStr, 2);
		if (number <= 100) {
			return number;
		} else {
			return 100;
		}
	}

	/**
	 * Convert from int to binary char ary.
	 *
	 * @param numbersToConvert the numbers to convert
	 *
	 * @return the char[]
	 */
	static char[] convertFromIntArrayToBinaryCharAry(final IndividualAttribute[] numbersToConvert) {
		final StringBuilder binaryString = new StringBuilder();
		for (final IndividualAttribute element : numbersToConvert) {
			binaryString.append(convertFromIntToBinaryStr(element.getValue()));
		}
		return binaryString.toString().toCharArray();
	}

	/**
	 * Convert from int to binary str.
	 *
	 * @param numberToConvert the number to convert
	 *
	 * @return the string
	 */
	private static String convertFromIntToBinaryStr(final int numberToConvert) {
		final StringBuilder binaryString = new StringBuilder();

		binaryString.append(Integer.toBinaryString(numberToConvert));
		for (int n = binaryString.length(); n < 7; n++) {
			binaryString.insert(0, "0");
		}
		return binaryString.toString();
	}
}
