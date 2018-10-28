package de.gg.game.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.gg.engine.ui.components.AnimationlessDialog;

public class BasicDialog extends AnimationlessDialog {

	public BasicDialog(String title, Skin skin) {
		super(title, skin);
	}

	public BasicDialog(String title, Skin skin, String windowStyleName) {
		super(title, skin, windowStyleName);
	}

	/**
	 * Adds a button to the button table.
	 *
	 * @param text
	 *            The text of the button.
	 * @param object
	 *            The object that will be passed to {@link #result(Object)} if
	 *            this button is clicked. May be <code>null</code>.
	 */
	@Override
	public Dialog button(String text, Object object) {
		return button(text, object,
				skin.get("small", ImageTextButtonStyle.class));
	}

}
