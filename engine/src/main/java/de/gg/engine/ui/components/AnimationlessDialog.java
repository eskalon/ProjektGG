package de.gg.engine.ui.components;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * This is a dialog without fade-out and fade-in animations. It should always be
 * used instead of {@link Dialog}.
 * <p>
 * The dialog has convenience methods for adding buttons on the bottom of the
 * window and keyboard listeners ({@link #button(String, Object)},
 * {@link #key(int, Object)}). The results of these methods can get processed by
 * overriding {@link #result(Object)}.
 * <p>
 * To display a dialog the {@link #show(Stage)}-method is used.
 */
public class AnimationlessDialog extends Dialog {

	protected Skin skin;

	public AnimationlessDialog(String title, Skin skin,
			String windowStyleName) {
		this(title, skin, skin.get(windowStyleName, WindowStyle.class));
	}

	public AnimationlessDialog(String title, Skin skin) {
		this(title, skin, skin.get(WindowStyle.class));
	}

	private AnimationlessDialog(String title, Skin skin,
			WindowStyle windowStyle) {
		super(title, skin);
		setStyle(windowStyle);
		this.skin = skin;
		getContentTable().defaults().space(15);
		getButtonTable().defaults().space(15);
	}

	/**
	 * {@link #pack() Packs} the dialog and adds it to the stage, centered but
	 * without a fade-in action.
	 */
	@Override
	public Dialog show(Stage stage) {
		show(stage, null);
		setPosition(Math.round((stage.getWidth() - getWidth()) / 2),
				Math.round((stage.getHeight() - getHeight()) / 2));
		return this;
	}

	/**
	 * Hides the dialog. Is called automatically when a button is clicked. No
	 * fade-out animation is played.
	 */
	@Override
	public void hide() {
		hide(null);
	}

}
