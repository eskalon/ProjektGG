package de.gg.util.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * This is a dialog without animations.
 * 
 * @see Dialog
 */
public class AnimationlessDialog extends Dialog {

	public AnimationlessDialog(String title, Skin skin) {
		super(title, skin);
	}

	/**
	 * {@link #pack() Packs} the dialog and adds it to the stage, centered but
	 * without a fade in action.
	 */
	public Dialog show(Stage stage) {
		show(stage, null);
		setPosition(Math.round((stage.getWidth() - getWidth()) / 2),
				Math.round((stage.getHeight() - getHeight()) / 2));
		return this;
	}

	/**
	 * Hides the dialog. Called automatically when a button is clicked. No fade
	 * out animation is played.
	 */
	public void hide() {
		hide(null);
	}

}