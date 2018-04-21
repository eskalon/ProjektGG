package de.gg.util;

public class MeasuringUtil {

	private long time;

	public void start() {
		time = System.currentTimeMillis();
	}

	public long stop() {
		return System.currentTimeMillis() - time;
	}

}
