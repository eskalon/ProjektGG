package de.gg.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Log {

	private Log() {
	}

	public static void enableDebugLogging() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		com.esotericsoftware.minlog.Log.INFO();
	}

	public static void disableDebugLogging() {
		Gdx.app.setLogLevel(Application.LOG_INFO);
		com.esotericsoftware.minlog.Log.ERROR();
	}

	public static void info(String tag, String message, Object... args) {
		Gdx.app.log(tag.toUpperCase(), String.format(message, args));
	}

	public static void error(String tag, String message, Object... args) {
		Gdx.app.error(tag.toUpperCase(), String.format(message, args));
	}

	public static void debug(String tag, String message, Object... args) {
		Gdx.app.debug(tag.toUpperCase(), String.format(message, args));
	}

}
