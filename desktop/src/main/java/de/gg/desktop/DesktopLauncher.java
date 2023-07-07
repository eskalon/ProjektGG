package de.gg.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import de.damios.guacamole.gdx.StartOnFirstThreadHelper;
import de.gg.game.core.ProjektGGApplication;

/**
 * Starts the application for the desktop-based builds.
 */
public class DesktopLauncher {

	public static void main(String[] args) {
		StartOnFirstThreadHelper.executeIfJVMValid(() -> {
			Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
			config.setTitle(ProjektGGApplication.GAME_NAME);
			config.setWindowedMode(1280, 720);
			config.setResizable(false);
			config.useVsync(false);
			config.setForegroundFPS(120);
			config.setWindowIcon(FileType.Internal, "icon16.png", "icon32.png",
					"icon48.png");

			try {
				new Lwjgl3Application(new ProjektGGApplication(), config);
			} catch (Exception e) {
				System.err.println(
						"An unexpected error occurred while starting the game:");
				e.printStackTrace();
				System.exit(-1);
			}
		});
	}

}
