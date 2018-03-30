package dev.gg.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import dev.gg.core.ProjektGG;
import dev.gg.util.CrashLogUtils;
import dev.gg.util.MicroOptions;

/**
 * Starts the application for the desktop-based builds.
 */
public class DesktopLauncher {

	/**
	 * The start-method for the whole application. Currently supported start
	 * arguments:
	 * <ul>
	 * <li>--debug: sets the game to debug mode.
	 * <li>--novid: skips the splash screen.
	 * </ul>
	 *
	 * @param args
	 *            The start arguments.
	 */
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = ProjektGG.name;
		config.height = 720;
		config.width = 1280;
		config.resizable = false;
		config.addIcon("ui/images/icon.png", Files.FileType.Absolute);

		boolean debug = false;
		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("debug")) {
				debug = true;
			}
		}

		MicroOptions options = new MicroOptions();
		options.option("novid").describedAs("no splashscreen").isUnary();
		options.option("debug").describedAs("enables debugmode").isUnary();
		try {
			options.parse(args);
		} catch (MicroOptions.OptionException e) {
			System.err.println("Usage:");
			System.err.println(options.usageString());
			System.exit(-1);
		}

		// options.getArg("file", "/tmp/out");

		try {
			// Start the game
			new LwjglApplication(
					new ProjektGG(options.has("debug"), !options.has("novid")),
					config);
		} catch (Exception e) {
			Gdx.app.error(ProjektGG.name,
					"An unexpected error occurred while starting the game", e);

			CrashLogUtils.writeCrashLogToFile(e, true);
		}
	}

}
