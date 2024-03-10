package de.eskalon.gg.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;

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
		StartOnFirstThreadHelper.executeOnValidJVM(() -> {
			/* Start arguments */
			StartArgumentsBuilder startArgs = StartArguments.create();

			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					if (args[i].equalsIgnoreCase("--debug")) {
						startArgs.enableDebugLogging();
						continue;
					}

					if (args[i].equalsIgnoreCase("--trace")) {
						startArgs.enableTraceLogging();
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
			config.setForegroundFPS(120);
			config.setResizable(false);
			config.setTitle(ProjektGGApplication.GAME_NAME);
			config.useVsync(false);
			config.setWindowedMode(1280, 720);
			config.setWindowIcon(FileType.Internal, "icon16.png", "icon32.png",
					"icon48.png");

			if (UIUtils.isMac) {
				// ImGuiRenderer requires OpenGL >= 3.0, so for macOs we'll have
				// to switch to the 3.2 core profile...
				ShaderProgram.prependVertexCode = "#version 150\n#define attribute in\n#define varying out\n";
				ShaderProgram.prependFragmentCode = "#version 150\n#define varying in\nout vec4 fragColor;\n#define textureCube texture\n#define texture2D texture\n#define gl_FragColor fragColor\n";
				config.setOpenGLEmulation(
						Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2);
			}

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
		}, args);
	}

}
