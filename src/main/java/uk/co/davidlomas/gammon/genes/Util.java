/**
 * GNU General Public License
 *
 * This file is part of GA-mmon.
 *
 * GA-mmon is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.genes;

/**
 * The Class Util.
 *
 * @author David Lomas
 */
public class Util {

	/**
	 * Convert from binary strings to int ar.
	 *
	 * @param binaryStr
	 *            the binary str
	 * @param numOfBlocks
	 *            the num of blocks
	 * @param lengthOfBlocks
	 *            the length of blocks
	 * @return the int[]
	 */
	public static int[] convertFromBinaryStringsToIntAr(final String binaryStr, final int numOfBlocks,
			final int lengthOfBlocks) {
		final int[] nums = new int[numOfBlocks];

		int beginIndex = 0;
		int endIndex = lengthOfBlocks; // blocks = size of binary chars per var

		// loop the string
		for (int x = 0; x < numOfBlocks; x++) {
			nums[x] = convertFromBinaryStrToInt(binaryStr.substring(beginIndex, endIndex));
			beginIndex += lengthOfBlocks;
			endIndex += lengthOfBlocks;
		}
		return nums;
	}

	/**
	 * Convert from binary str to int.
	 *
	 * @param binaryStr
	 *            the binary str
	 * @return the int
	 */
	public static int convertFromBinaryStrToInt(final String binaryStr) {
		final int x = Integer.parseInt(binaryStr, 2);
		if (x <= 100) {
			return x;
		} else {
			return 100;
		}
	}

	/**
	 * Convert from int to binary char ary.
	 *
	 * @param numbersToConvert
	 *            the numbers to convert
	 * @return the char[]
	 */
	public static char[] convertFromIntArrayToBinaryCharAry(final IndividualAttribute[] numbersToConvert) {
		final StringBuffer binaryString = new StringBuffer();
		for (final IndividualAttribute element : numbersToConvert) {
			binaryString.append(convertFromIntToBinaryStr(element.getValue()));
		}
		return binaryString.toString().toCharArray();
	}

	/**
	 * Convert from int to binary str.
	 *
	 * @param numberToConvert
	 *            the number to convert
	 * @return the string
	 */
	public static String convertFromIntToBinaryStr(final int numberToConvert) {
		final StringBuffer binaryString = new StringBuffer();

		binaryString.append(Integer.toBinaryString(numberToConvert));
		for (int n = binaryString.length(); n < 7; n++) {
			binaryString.insert(0, "0");
		}
		return binaryString.toString();
	}
}
