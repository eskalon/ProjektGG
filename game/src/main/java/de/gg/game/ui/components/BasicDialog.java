package de.gg.game.ui.components;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import javax.annotation.Nullable;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.utils.ISimpleListener;

/**
 * A basic dialog.
 * <p>
 * The dialog has convenience methods for adding buttons on the bottom of the
 * window and keyboard listeners ({@link #button(String, Object)},
 * {@link #key(int, Object)}). The results of these methods can get processed by
 * overriding {@link #result(Object)}.
 * <p>
 * To display a dialog the {@link #show(Stage)}-method is used.
 * 
 * @see #createAndShow(Stage, Skin, String, String)
 * @see #createAndShow(Stage, Skin, String, String, boolean, ISimpleListener)
 */
public class BasicDialog extends Dialog {

	protected Skin skin;

	public BasicDialog(String title, Skin skin, String windowStyleName) {
		this(title, skin, skin.get(windowStyleName, WindowStyle.class));
	}

	public BasicDialog(String title, Skin skin) {
		this(title, skin, skin.get(WindowStyle.class));
	}

	private BasicDialog(String title, Skin skin, WindowStyle windowStyle) {
		super(title, skin);
		// setStyle(windowStyle);
		this.skin = skin;
		getContentTable().defaults().space(15);
		getButtonTable().defaults().space(15);
	}

	@Override
	public Dialog show(Stage stage) {
		show(stage, sequence(Actions.alpha(0),
				Actions.fadeIn(0.2f, Interpolation.fade)));
		return this;
	}

	@Override
	public void hide() {
		hide(fadeOut(0.2f, Interpolation.fade));
	}

	/**
	 * Adds a button to the button table.
	 *
	 * @param text
	 *            The text of the button.
	 * @param object
	 *            The object that will be passed to {@link #result(Object)} if
	 *            this button is clicked. May be {@code null}.
	 */
	@Override
	public Dialog button(String text, @Nullable Object object) {
		return button(text, object,
				skin.get("small", ImageTextButtonStyle.class));
	}

	public static BasicDialog createAndShow(Stage stage, Skin skin,
			String title, String text, boolean showOkButton,
			@Nullable ISimpleListener listener) {
		BasicDialog dialog = new BasicDialog(title, skin) {
			@Override
			public void result(Object obj) {
				if (listener != null)
					listener.listen(obj);
				else
					super.result(obj);
			}
		};
		dialog.text(text);
		if (showOkButton) {
			dialog.key(Keys.ENTER, true);
			dialog.button(Lang.get("ui.generic.ok"));
		}

		dialog.show(stage);
		return dialog;
	}

	public static BasicDialog createAndShow(Stage stage, Skin skin,
			String title, String text) {
		return createAndShow(stage, skin, title, text, true, null);
	}

}
