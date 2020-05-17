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
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
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
				Actions.fadeIn(0.3F, Interpolation.fade)));
		setPosition(Math.round((stage.getWidth() - getWidth()) / 2),
				Math.round((stage.getHeight() - getHeight()) / 2));
		return this;
	}

	@Override
	public void hide() {
		hide(fadeOut(0.25F, Interpolation.fade));
	}

	/**
	 * Adds a label to the content table. The dialog must have been constructed
	 * with a skin to use this method.
	 * 
	 * @param text
	 */
	public Dialog text(String text) {
		if (skin == null)
			throw new IllegalStateException(
					"This method may only be used if the dialog was constructed with a Skin.");
		return text(text, skin.get("text-white-20", LabelStyle.class));
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
