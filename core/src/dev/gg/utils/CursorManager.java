package dev.gg.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * Manages the cursor image.
 */
public class CursorManager {

	/**
	 * Contains the already used and therefore cached cursors.
	 */
	protected Map<Pixmap, Cursor> cursorCacheMap = new ConcurrentHashMap<>();

	/**
	 * Sets the cursor image.
	 * 
	 * @param image
	 *            The image.
	 */
	public void setCursorImage(Pixmap image) {
		if (image == null) {
			throw new IllegalArgumentException("Cursor image cannot be null.");
		}

		if (!cursorCacheMap.containsKey(image)) {
			Cursor newCursor = Gdx.graphics.newCursor(image, 0, 0);
			this.cursorCacheMap.put(image, newCursor);

			Gdx.graphics.setCursor(newCursor);
		} else {
			Gdx.graphics.setCursor(cursorCacheMap.get(image));
		}
	}

	/**
	 * Resets the cursor image to the default one.
	 * 
	 * @see SystemCursor#Arrow
	 */
	public void resetCursorImage() {
		Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
	}

}
