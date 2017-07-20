package dev.gg.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * This class holds utility methods for dealing with exceptions.
 */
public class ErrorUtils {

	public static final File CRASH_LOG_FILE = new File("./crash.log");

	private ErrorUtils() {
	}

	/**
	 * Writes a crash log to the crash log file.
	 * 
	 * @param e
	 *            The exception.
	 * @see #writeCrashLogToFile(Exception, boolean)
	 */
	public static void writeCrashLogToFile(Exception e) {
		writeCrashLogToFile(e, false);
	}

	/**
	 * Writes a crash log to the crash log file.
	 * 
	 * @param e
	 *            The exception.
	 * @param forceExit
	 *            Whether the application should get shut down.
	 */
	public static void writeCrashLogToFile(Exception e, boolean forceExit) {
		try {
			FileUtils.writeStringToFile(CRASH_LOG_FILE,
					e.getLocalizedMessage());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (forceExit)
			System.exit(-1);
	}

}
