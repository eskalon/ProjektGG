package de.gg.util;

import java.util.Random;

/**
 * A few utility methods for dealing with random numbers and chances.
 */
public class RandomUtils {

	private static Random random = new Random();

	private RandomUtils() {
	}

	/**
	 * Generates a random number within the specified range.
	 * 
	 * @param min
	 *            Included minimal value.
	 * @param max
	 *            Included maximum value.
	 * @return The random integer.
	 */
	public static int getRandomNumber(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	/**
	 * Generates a random number within the specified range.
	 * 
	 * @param random
	 *            The random generator used.
	 * @param min
	 *            Included minimal value.
	 * @param max
	 *            Included maximum value.
	 * @return The random integer.
	 */
	public static int getRandomNumber(Random random, int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	/**
	 * Rolls the dice and returns true with the chance {@code 1/x }
	 * 
	 * @param x
	 *            The reciprocal of the chance.
	 * @return Whether the roll succeeded.
	 */
	public static boolean rollTheDice(int x) {
		return getRandomNumber(1, x) == 1;
	}

	/**
	 * Rolls the dice and returns true with the chance {@code 1/x }
	 * 
	 * @param random
	 *            The random generator used.
	 * @param x
	 *            The reciprocal of the chance.
	 * @return Whether the roll succeeded.
	 */
	public static boolean rollTheDice(Random random, int x) {
		return getRandomNumber(1, x) == 1;
	}

}
