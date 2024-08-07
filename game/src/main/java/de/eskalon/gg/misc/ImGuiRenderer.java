package de.eskalon.gg.misc;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;

import de.damios.guacamole.Preconditions;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class ImGuiRenderer {

	private static ImGuiImplGlfw imGuiGlfw;
	private static ImGuiImplGl3 imGuiGl3;

	private static boolean drawing;
	private static InputProcessor tmpProcessor;

	private ImGuiRenderer() {
		throw new UnsupportedOperationException();
	}

	public static void init() {
		Preconditions.checkState(imGuiGlfw == null && imGuiGl3 == null,
				"ImGuiRenderer was already initialised. Call dispose() first!");
		Preconditions.checkState(!(Gdx.app.getType() == ApplicationType.Desktop
				|| Gdx.app.getType() == ApplicationType.HeadlessDesktop)
				|| !UIUtils.isMac || Gdx.gl30 != null,
				"ImGui requires OpenGL >= 3.0. Since the default OpenGL profile on macOS only supports 2.1, the 3.2 core profile needs to be enabled via Lwjgl3ApplicationConfiguration#setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2).");

		imGuiGlfw = new ImGuiImplGlfw();
		imGuiGl3 = new ImGuiImplGl3();
		/*
		 * Method getWindow = ClassReflection.getMethod(
		 * ClassReflection.forName(
		 * "com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics"), "getWindow",
		 * null); Method getWindowHandle = ClassReflection.getMethod(
		 * ClassReflection.forName(
		 * "com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window"), "getWindowHandle",
		 * null); Object window = getWindow.invoke(Gdx.graphics); Object
		 * windowHandle = getWindowHandle.invoke(window, null);
		 */
		ImGui.createContext();
		ImGuiIO io = ImGui.getIO();
		io.setIniFilename(null);
		io.getFonts().addFontDefault();
		io.getFonts().build();

		imGuiGlfw.init(
				((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle(),
				true);
		imGuiGl3.init("#version 150");
	}

	public static void start() {
		Preconditions.checkState(imGuiGlfw != null && imGuiGl3 != null,
				"ImGuiRenderer needs to be initialised first!");
		Preconditions.checkState(!drawing, "You need to call end() first!");

		drawing = true;

		if (tmpProcessor != null) {
			Gdx.input.setInputProcessor(tmpProcessor);
			tmpProcessor = null;
		}

		imGuiGlfw.newFrame();
		ImGui.newFrame();
	}

	public static void end() {
		Preconditions.checkState(drawing, "You need to call start() first!");

		drawing = false;

		ImGui.render();
		imGuiGl3.renderDrawData(ImGui.getDrawData());

		if (ImGui.getIO().getWantCaptureKeyboard()
				|| ImGui.getIO().getWantCaptureMouse()) {
			tmpProcessor = Gdx.input.getInputProcessor();
			Gdx.input.setInputProcessor(null);
		}
	}

	public static void dispose() {
		if (imGuiGl3 != null)
			imGuiGl3.dispose();
		imGuiGl3 = null;
		if (imGuiGlfw != null)
			imGuiGlfw.dispose();
		imGuiGlfw = null;
		ImGui.destroyContext();
	}

}
