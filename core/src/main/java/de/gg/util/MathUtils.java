package de.gg.util;

import java.util.NavigableMap;

public class MathUtils {

	private MathUtils() {
	}

	/**
	 * Interpolates a <i>value</i> linearly.
	 *
	 * @param v0
	 * @param v1
	 * @param t
	 * @return
	 */
	public static float lerp(float v0, float v1, float t) {
		return v0 + t * (v1 - v0);
	}

	/**
	 * Interpolates a <i>point</i> linearly.
	 *
	 * @param y0
	 * @param x0
	 * @param y1
	 * @param x1
	 * @param x
	 * @return
	 */
	public static float lerp(float y0, float x0, float y1, float x1, float x) {
		return (y0 * (x1 - x) + y1 * (x - x0)) / (x1 - x0);
	}

	/**
	 * Converts a linear level to an exponential one. Is especially useful for
	 * sound levels.
	 *
	 * @param x
	 *            The linear level. Is usually in the range of <code>0</code> to
	 *            <code>1</code>.
	 * @param base
	 *            The exponential base to use.
	 * @return
	 */
	public static double linToExp(double x, int base) {
		return (Math.pow(base, x) - 1) / (base - 1);
	}

	/**
	 * Calculates the probability for a skill based action to succeed using a
	 * {@linkplain #sigmoid(double) sigmoid function}. This means that the
	 * probability change is smaller towards the end and the beginning of the
	 * range.
	 *
	 * @param skill
	 * @param difficulty
	 * @param steepness
	 * @param offset
	 * @return
	 */
	public static double getProbability(float skill, float difficulty,
			float steepness, float offset) {
		float rawOdds = (1 + skill) / (2 + skill + difficulty);
		return sigmoid(-(rawOdds * steepness + offset));
	}

	private static double sigmoid(double x) {
		return 1 / (1 + Math.exp(-x));
	}

	public static float interpolateFunction(
			NavigableMap<Float, Float> functionValues, float x) {
		if (functionValues.containsKey(x))
			return functionValues.get(x);

		Float higherX = functionValues.higherKey(x);
		Float lowerX = functionValues.lowerKey(x);

		if (higherX == null)
			return functionValues.get(lowerX);

		if (lowerX == null)
			return functionValues.get(higherX);

		return lerp(lowerX, functionValues.get(lowerX), higherX,
				functionValues.get(higherX), x);
	}

}
