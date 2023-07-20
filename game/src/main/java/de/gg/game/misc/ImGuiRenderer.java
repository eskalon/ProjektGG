package de.gg.game.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;

import de.damios.guacamole.Preconditions;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class ImGuiRenderer {

	private static ImGuiImplGlfw imGuiGlfw;
	private static ImGuiImplGl3 imGuiGl3;

	private static InputProcessor tmpProcessor;

	private ImGuiRenderer() {
		throw new UnsupportedOperationException();
	}

	public static void init() {
		Preconditions.checkState(imGuiGlfw == null && imGuiGl3 == null,
				"ImGuiRenderer was already initialised!");

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
		long windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow()
				.getWindowHandle();
		ImGui.createContext();
		ImGuiIO io = ImGui.getIO();
		io.setIniFilename(null);
		io.getFonts().addFontDefault();
		io.getFonts().build();

		imGuiGlfw.init(windowHandle, true);
		imGuiGl3.init("#version 150");
	}

	public static void start() {
		if (tmpProcessor != null) {
			Gdx.input.setInputProcessor(tmpProcessor);
			tmpProcessor = null;
		}

		imGuiGlfw.newFrame();
		ImGui.newFrame();
	}

	public static void end() {
		ImGui.render();
		imGuiGl3.renderDrawData(ImGui.getDrawData());

		if (ImGui.getIO().getWantCaptureKeyboard()
				|| ImGui.getIO().getWantCaptureMouse()) {
			tmpProcessor = Gdx.input.getInputProcessor();
			Gdx.input.setInputProcessor(null);
		}
	}

	public static void dispose() {
		imGuiGl3.dispose();
		imGuiGl3 = null;
		imGuiGlfw.dispose();
		imGuiGlfw = null;
		ImGui.destroyContext();
	}

}
