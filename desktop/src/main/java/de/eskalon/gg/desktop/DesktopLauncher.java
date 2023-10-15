package de.eskalon.gg.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import de.damios.guacamole.gdx.StartOnFirstThreadHelper;
import de.eskalon.commons.core.EskalonApplicationStarter;
import de.eskalon.commons.core.StartArguments;
import de.eskalon.commons.core.StartArguments.StartArgumentsBuilder;
import de.eskalon.gg.core.ProjektGGApplication;

/**
 * Starts the application for the desktop-based builds.
 */
public class DesktopLauncher {

	public static void main(String[] args) {
		StartOnFirstThreadHelper.executeIfJVMValid(() -> {
			/* Start arguments */
			StartArgumentsBuilder startArgs = StartArguments.create();

			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					if (args[i].equalsIgnoreCase("--debug")) {
						startArgs.enableDebugLogging();
						continue;
					}

					if (args[i].equalsIgnoreCase("--skip")) {
						startArgs.skipSplashScreen();
						continue;
					}
				}
			}

			/* libGDX config */
			Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
			config.setTitle(ProjektGGApplication.GAME_NAME);
			config.setWindowedMode(1280, 720);
			config.setResizable(false);
			config.useVsync(false);
			config.setForegroundFPS(120);
			config.setWindowIcon(FileType.Internal, "icon16.png", "icon32.png",
					"icon48.png");

			/* Start the app */
			try {
				new Lwjgl3Application(
						new EskalonApplicationStarter(
								ProjektGGApplication.GAME_NAME,
								ProjektGGApplication.class, startArgs.build()),
						config);
			} catch (Exception e) {
				System.err.println(
						"An unexpected error occurred while starting the game:");
				e.printStackTrace();
				System.exit(-1);
			}
		});
	}

}
