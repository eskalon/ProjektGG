
package com.esotericsoftware.minlog;

import de.damios.guacamole.Exceptions;
import de.damios.guacamole.gdx.log.LoggerService;

/**
 * A low overhead, lightweight logging system.
 * 
 * @author Nathan Sweet <misc@n4te.com>
 */
public class Log {
	/** No logging at all. */
	static public final int LEVEL_NONE = 6;
	/** Critical errors. The application may no longer work correctly. */
	static public final int LEVEL_ERROR = 5;
	/** Important warnings. The application will continue to work correctly. */
	static public final int LEVEL_WARN = 4;
	/** Informative messages. Typically used for deployment. */
	static public final int LEVEL_INFO = 3;
	/** Debug messages. This level is useful during development. */
	static public final int LEVEL_DEBUG = 2;
	/**
	 * Trace messages. A lot of information is logged, so this level is usually
	 * only needed when debugging a problem.
	 */
	static public final int LEVEL_TRACE = 1;

	/**
	 * The level of messages that will be logged. Compiling this and the
	 * booleans below as "final" will cause the compiler to remove all "if
	 * (Log.info) ..." type statements below the set level.
	 */
	static private int level = LEVEL_TRACE; // Log everything to delegate
											// control to slf4j

	/** True when the ERROR level will be logged. */
	static public boolean ERROR = level <= LEVEL_ERROR;
	/** True when the WARN level will be logged. */
	static public boolean WARN = level <= LEVEL_WARN;
	/** True when the INFO level will be logged. */
	static public boolean INFO = level <= LEVEL_INFO;
	/** True when the DEBUG level will be logged. */
	static public boolean DEBUG = level <= LEVEL_DEBUG;
	/** True when the TRACE level will be logged. */
	static public boolean TRACE = level <= LEVEL_TRACE;

	/**
	 * Sets the level to log. If a version of this class is being used that has
	 * a final log level, this has no affect.
	 */
	static public void set(int level) {
		// Comment out method contents when compiling fixed level JARs.
		Log.level = level;
		ERROR = level <= LEVEL_ERROR;
		WARN = level <= LEVEL_WARN;
		INFO = level <= LEVEL_INFO;
		DEBUG = level <= LEVEL_DEBUG;
		TRACE = level <= LEVEL_TRACE;
	}

	static public void NONE() {
		set(LEVEL_NONE);
	}

	static public void ERROR() {
		set(LEVEL_ERROR);
	}

	static public void WARN() {
		set(LEVEL_WARN);
	}

	static public void INFO() {
		set(LEVEL_INFO);
	}

	static public void DEBUG() {
		set(LEVEL_DEBUG);
	}

	static public void TRACE() {
		set(LEVEL_TRACE);
	}

	/**
	 * Sets the logger that will write the log messages.
	 */
	static public void setLogger(Logger logger) {
		Log.logger = logger;
	}

	static private Logger logger = new Logger();

	static public void error(String message, Throwable ex) {
		if (ERROR)
			logger.log(LEVEL_ERROR, null, message, ex);
	}

	static public void error(String category, String message, Throwable ex) {
		if (ERROR)
			logger.log(LEVEL_ERROR, category, message, ex);
	}

	static public void error(String message) {
		if (ERROR)
			logger.log(LEVEL_ERROR, null, message, null);
	}

	static public void error(String category, String message) {
		if (ERROR)
			logger.log(LEVEL_ERROR, category, message, null);
	}

	static public void warn(String message, Throwable ex) {
		if (WARN)
			logger.log(LEVEL_WARN, null, message, ex);
	}

	static public void warn(String category, String message, Throwable ex) {
		if (WARN)
			logger.log(LEVEL_WARN, category, message, ex);
	}

	static public void warn(String message) {
		if (WARN)
			logger.log(LEVEL_WARN, null, message, null);
	}

	static public void warn(String category, String message) {
		if (WARN)
			logger.log(LEVEL_WARN, category, message, null);
	}

	static public void info(String message, Throwable ex) {
		if (INFO)
			logger.log(LEVEL_INFO, null, message, ex);
	}

	static public void info(String category, String message, Throwable ex) {
		if (INFO)
			logger.log(LEVEL_INFO, category, message, ex);
	}

	static public void info(String message) {
		if (INFO)
			logger.log(LEVEL_INFO, null, message, null);
	}

	static public void info(String category, String message) {
		if (INFO)
			logger.log(LEVEL_INFO, category, message, null);
	}

	static public void debug(String message, Throwable ex) {
		if (DEBUG)
			logger.log(LEVEL_DEBUG, null, message, ex);
	}

	static public void debug(String category, String message, Throwable ex) {
		if (DEBUG)
			logger.log(LEVEL_DEBUG, category, message, ex);
	}

	static public void debug(String message) {
		if (DEBUG)
			logger.log(LEVEL_DEBUG, null, message, null);
	}

	static public void debug(String category, String message) {
		if (DEBUG)
			logger.log(LEVEL_DEBUG, category, message, null);
	}

	static public void trace(String message, Throwable ex) {
		if (TRACE)
			logger.log(LEVEL_TRACE, null, message, ex);
	}

	static public void trace(String category, String message, Throwable ex) {
		if (TRACE)
			logger.log(LEVEL_TRACE, category, message, ex);
	}

	static public void trace(String message) {
		if (TRACE)
			logger.log(LEVEL_TRACE, null, message, null);
	}

	static public void trace(String category, String message) {
		if (TRACE)
			logger.log(LEVEL_TRACE, category, message, null);
	}

	private Log() {
	}

	/**
	 * Performs the actual logging.
	 */
	static public class Logger {
		private static final de.damios.guacamole.gdx.log.Logger LOG = LoggerService
				.getLogger(Log.class);

		public void log(int level, String category, String message,
				Throwable ex) {

			switch (level) {
			case LEVEL_ERROR:
				if (ex == null)
					LOG.error("[" + category + "] " + message);
				else
					LOG.error("[" + category + "] " + message + ": %s",
							Exceptions.getStackTraceAsString(ex));
				break;
			case LEVEL_WARN:
				if (ex == null)
					LOG.info("[" + category + "] " + message);
				else
					LOG.info("[" + category + "] " + message + ": %s",
							Exceptions.getStackTraceAsString(ex));
				break;
			case LEVEL_INFO:
				if (ex == null)
					LOG.debug("[" + category + "] " + message);
				else
					LOG.debug("[" + category + "] " + message + ": %s",
							Exceptions.getStackTraceAsString(ex));
				break;
			case LEVEL_DEBUG:
				if (ex == null)
					LOG.debug("[" + category + "] " + message);
				else
					LOG.debug("[" + category + "] " + message + ": %s",
							Exceptions.getStackTraceAsString(ex));
				break;
			case LEVEL_TRACE:
				//@formatter:off
//				if (ex == null)
//					LOG.debug("[" + category + "] " + message);
//				else
//					LOG.debug("[" + category + "] " + message + ": %s",
//							Exceptions.getStackTraceAsString(ex));
//				break;
				//@formatter:on
			}
		}
	}
}