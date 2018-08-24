package de.gg.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Takes care of saving a screenshot.
 */
public class ScreenshotUtils {

	private ScreenshotUtils() {
		// not used
	}

	/**
	 * Takes a screenshot of the currently rendered screen and saves it on disk
	 * as a PNG. The file's name is the current date and time in the format
	 * "yyyy-MM-dd HH-mm-ss".
	 */
	public static void takeScreenshot() {
		byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0,
				Gdx.graphics.getBackBufferWidth(),
				Gdx.graphics.getBackBufferHeight(), true);

		Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(),
				Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH-mm-ss");

		PixmapIO.writePNG(
				Gdx.files.external(dateFormat.format(new Date()) + ".png"),
				pixmap);
		pixmap.dispose();
	}

}
