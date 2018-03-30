package de.gg.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;

import de.gg.core.ProjektGG;

/**
 * This class holds utility methods for dealing with crash logs.
 */
public class CrashLogUtils {

	public static final File CRASH_LOG_FILE = new File("./crash.log");
	private static final Charset CHARSET = Charset.isSupported("UTF-8")
			? Charset.forName("UTF-8")
			: Charset.defaultCharset();

	private CrashLogUtils() {
	}

	/**
	 * Writes a crash log to the {@linkplain #CRASH_LOG_FILE crash log file}.
	 * Appends to any existing logs.
	 * 
	 * @param e
	 *            The exception.
	 * @see #writeCrashLogToFile(Exception, boolean)
	 */
	public static void writeCrashLogToFile(Exception e) {
		writeCrashLogToFile(e, false);
	}

	/**
	 * Writes a crash log to the {@linkplain #CRASH_LOG_FILE crash log file}.
	 * Appends to any existing logs.
	 * 
	 * @param e
	 *            The exception.
	 * @param forceExit
	 *            Whether the application should get shut down.
	 */
	public static void writeCrashLogToFile(Exception e, boolean forceExit) {
		try {
			FileUtils.writeStringToFile(CRASH_LOG_FILE, e.getLocalizedMessage(),
					CHARSET, true);
		} catch (IOException e1) {
			Gdx.app.error(ProjektGG.name,
					"An error occurred while saving the crash log", e);
		}

		if (forceExit)
			System.exit(-1);
	}

}
