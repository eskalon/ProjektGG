package de.gg.util;

public class ColorUtils {

	private ColorUtils() {
	}

	/**
	 * Interpolates a value.
	 * 
	 * @param v1
	 * @param v2
	 * @param percent
	 * @return
	 */
	public static float interpolateValue(float v1, float v2, float percent) {
		return v1 + percent * (v2 - v1);
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
