package de.gg.engine.log;

import java.util.Date;

import com.badlogic.gdx.ApplicationLogger;

public class AppLogger implements ApplicationLogger {

	private final String INFO_LOG_FORMAT = "%tT - [INFO ] [%S]:  %s";
	private final String ERROR_LOG_FORMAT = "%tT - [ERROR] [%S]:  %s";
	private final String DEBUG_LOG_FORMAT = "%tT - [DEBUG] [%S]:  %s";

	private static final String formatMessage(String formatString, String tag,
			String message) {
		return String.format(formatString, new Date(), tag, message);
	}

	@Override
	public void log(String tag, String message) {
		System.out.println(formatMessage(INFO_LOG_FORMAT, tag, message));
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		System.out.println(formatMessage(INFO_LOG_FORMAT, tag, message));
		exception.printStackTrace(System.out);
	}

	@Override
	public void error(String tag, String message) {
		System.out.println(formatMessage(ERROR_LOG_FORMAT, tag, message));
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		System.out.println(formatMessage(ERROR_LOG_FORMAT, tag, message));
		exception.printStackTrace(System.err);
	}

	@Override
	public void debug(String tag, String message) {
		System.out.println(formatMessage(DEBUG_LOG_FORMAT, tag, message));
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		System.out.println(formatMessage(DEBUG_LOG_FORMAT, tag, message));
		exception.printStackTrace(System.out);
	}

}
