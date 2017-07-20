package dev.gg.util;

/**
 * A few utility methods for dealing with random numbers and chances.
 */
public class RandomUtils {

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
		return (int) (Math.random() * (max - min)) + min;
	}

	/**
	 * Rolls the dice and returns true with the chance {@code 1/x }
	 * 
	 * @param x
	 *            The reciprocal of the chance.
	 * @return The random integer.
	 */
	public static boolean rollTheDice(int x) {
		return getRandomNumber(1, x) == 1;
	}

}
