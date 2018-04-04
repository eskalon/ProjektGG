package de.gg.util.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * A text field with a text offset.
 */
public class OffsetableTextField extends TextField {

	private final int offset;

	/**
	 * @param text
	 *            The default text.
	 * @param skin
	 *            The ui skin.
	 * @param offset
	 *            The offset applied in front of the input text.
	 */
	public OffsetableTextField(String text, Skin skin, int offset) {
		super(text, skin);

		this.offset = offset;
		this.textOffset = offset;
	}

	protected void calculateOffsets() {
		super.calculateOffsets();

		this.textOffset = offset;
	}

}