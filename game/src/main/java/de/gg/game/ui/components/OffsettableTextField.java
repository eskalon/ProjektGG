package de.gg.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * A text field with a text offset.
 */
public class OffsettableTextField extends TextField {

	private final int offset;

	/**
	 * @param text
	 *            The default text.
	 * @param skin
	 *            The ui skin.
	 * @param offset
	 *            The offset applied in front of the input text.
	 */
	public OffsettableTextField(String text, Skin skin, int offset) {
		super(text, skin);

		this.offset = offset;
		this.textOffset = offset;
	}

	/**
	 * @param text
	 *            The default text.
	 * @param skin
	 *            The ui skin.
	 * @param styleName
	 *            The name of the used style.
	 * @param offset
	 *            The offset applied in front of the input text.
	 */
	public OffsettableTextField(String text, Skin skin, String styleName,
			int offset) {
		super(text, skin, styleName);

		this.offset = offset;
		this.textOffset = offset;
	}

	@Override
	protected void calculateOffsets() {
		super.calculateOffsets();

		this.textOffset = offset;
	}

}