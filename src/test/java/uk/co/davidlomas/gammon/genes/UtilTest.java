package uk.co.davidlomas.gammon.genes;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {

	@BeforeAll
	public static void beforeClass() {
		SettingsUtil.resetSettings();
	}

	@AfterAll
	public static void afterClass() {
		SettingsUtil.resetSettings();
	}

	@Test
	public void encodingThenDecodingEveryValueGivesTheSameValuesBack() {
		// Given an attribute for every value a gene can hold, 0 to 100
		final IndividualAttribute[] attributes = new IndividualAttribute[101];
		final int[] values = new int[101];
		for (int value = 0; value <= 100; value++) {
			attributes[value] = new IndividualAttribute("gene" + value, value);
			values[value] = value;
		}

		// When they are turned into a chromosome and back
		final char[] chromosome = Util.convertFromIntArrayToBinaryCharAry(attributes);
		final int[] decoded = Util.convertFromBinaryStringsToIntAr(String.valueOf(chromosome), attributes.length);

		// Then nothing is lost
		assertArrayEquals(values, decoded);
	}

	@Test
	public void eachValueIsWrittenAsSevenBinaryDigits() {
		final IndividualAttribute[] attributes = {new IndividualAttribute("small", 5), new IndividualAttribute("large", 100)};

		assertEquals("0000101" + "1100100", String.valueOf(Util.convertFromIntArrayToBinaryCharAry(attributes)));
	}

	@Test
	public void decodedValuesAbove100AreCappedAt100() {
		// Mutation flips bits, so a chromosome can hold 101 to 127, which genes can't
		final int[] decoded = Util.convertFromBinaryStringsToIntAr("1100101" + "1111111" + "1100011", 3);

		assertArrayEquals(new int[]{100, 100, 99}, decoded);
	}
}
