package de.gg.utils;

import com.badlogic.gdx.graphics.Color;

public class ColorUtils {

	private ColorUtils() {
		// not used
	}

	/**
	 * Returns a color, that is located in between the given hue values.
	 * Deciding parameter is the <code>currentPercantage</code>.
	 *
	 * @param startHue
	 *            The minimum hue.
	 * @param endHue
	 *            The maximum hue.
	 * @param currentPercentage
	 *            Where this color should be located in between the hue values.
	 * @param saturation
	 *            The saturation.
	 * @param value
	 *            The hue.
	 * @return A color in between the min an max hue values, denoted by the
	 *         <code>currentPercantage</code>.
	 */
	public static Color getInterpolatedColor(float minHue, float maxHue,
			float currentPercentage, float saturation, float value) {
		float[] tmp = ColorUtils.hsvToRgb(
				MathUtils.lerp(minHue, maxHue, currentPercentage), saturation,
				value);

		return new Color(tmp[0], tmp[1], tmp[2], 1);
	}

	/**
	 * Converts a hsv color to a rgb one.
	 *
	 * @param hue
	 * @param saturation
	 * @param value
	 * @return
	 */
	public static float[] hsvToRgb(float hue, float saturation, float value) {
		hue /= 360f;
		saturation /= 100f;
		value /= 100f;

		int h = (int) (hue * 6);
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		switch (h) {
		case 0:
			return new float[] { value, t, p };
		case 1:
			return new float[] { q, value, p };
		case 2:
			return new float[] { p, value, t };
		case 3:
			return new float[] { p, q, value };
		case 4:
			return new float[] { t, p, value };
		case 5:
			return new float[] { value, p, q };
		default:
			throw new IllegalArgumentException(
					"Something went wrong when converting from HSV to RGB. Input was "
							+ hue + ", " + saturation + ", " + value);
		}
	}

}
