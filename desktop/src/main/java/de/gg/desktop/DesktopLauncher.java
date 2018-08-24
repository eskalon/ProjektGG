package de.gg.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.gg.core.ProjektGG;
import de.gg.util.MicroOptions;

/**
 * Starts the application for the desktop-based builds.
 */
public class DesktopLauncher {

	/**
	 * The entry point for the whole application on desktop systems. Currently
	 * supported start arguments are:
	 * <ul>
	 * <li><code>--debug</code>: sets the game to debug mode.
	 * <li><code>--novid</code>: skips the splash screen.
	 * <li><code>--fps</code>: shows a fps counter in-game.
	 * <li><code>--width [width]</code>: sets the window's width
	 * <li><code>--height [height]</code>: sets the window's height
	 * </ul>
	 *
	 * @param args
	 *            The start arguments.
	 */
	public static void main(String[] args) {
		MicroOptions options = new MicroOptions();
		options.option("debug").describedAs("enables debugmode").isUnary();
		options.option("fps").describedAs("enables a fps counter").isUnary();
		options.option("novid").describedAs("no splashscreen").isUnary();
		options.option("width").describedAs("the width of the game's window");
		options.option("height").describedAs("the heigth of the game's window");
		try {
			options.parse(args);
		} catch (MicroOptions.OptionException e) {
			System.err.println("Usage:");
			exitWithError(options.usageString());
		}

		int width = 0, height = 0;
		try {
			width = Integer.valueOf(options.getArg("width", "1280")); // 1600
			height = Integer.valueOf(options.getArg("height", "720")); // 900
		} catch (NumberFormatException e) {
			exitWithError("the width and height parameter have to be integers");
		}

		if (width < 0 || height < 0)
			exitWithError(
					"the width and height parameter have to be positive integers");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = ProjektGG.NAME;
		config.height = height;
		config.width = width;
		config.resizable = false;
		config.addIcon("ui/images/icon16.png", Files.FileType.Absolute);
		config.addIcon("ui/images/icon32.png", Files.FileType.Absolute);
		config.addIcon("ui/images/icon48.png", Files.FileType.Absolute);

		try {
			// Start the game
			new LwjglApplication(new ProjektGG(options.has("debug"),
					!options.has("novid"), options.has("fps")), config);
		} catch (Exception e) {
			exitWithError(String.format(
					"An unexpected error occurred while starting the game: %s",
					e));
		}
	}

	private static void exitWithError(String errorMsg) {
		System.err.println(errorMsg);
		System.exit(-1);
	}

}
