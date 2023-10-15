package de.eskalon.gg.graphics.ui.actors.dialogs;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import javax.annotation.Nullable;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * A basic dialog.
 * <p>
 * The dialog has convenience methods for adding buttons on the bottom of the
 * window and keyboard listeners ({@link #button(String, Object)},
 * {@link #key(int, Object)}). The results of these methods can get processed by
 * overriding {@link #result(Object)}.
 * <p>
 * To display a dialog the {@link #show(Stage)}-method is used.
 */
public class BasicDialog extends Dialog {

	public BasicDialog(String title, Skin skin, String windowStyleName) {
		super(title, skin, windowStyleName);

		this.getTitleTable().getCell(this.getTitleLabel()).padLeft(22)
				.padTop(40);
		this.getContentTable().padTop(26);
		this.getButtonTable().padBottom(18);
		this.getButtonTable().defaults().padBottom(0).padTop(0);

		// this.getTitleTable().getCell(this.getTitleLabel()).padLeft(22)
		// .padTop(40);
		// this.getButtonTable().padBottom(-20);
		// this.getButtonTable().defaults().padBottom(0).padTop(12);
	}

	public BasicDialog(String title, Skin skin) {
		this(title, skin, "default");
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
		if (getSkin() == null)
			throw new IllegalStateException(
					"This method may only be used if the dialog was constructed with a Skin.");
		return text(text, getSkin().get("dark_text", LabelStyle.class));
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
		if (getSkin() == null)
			throw new IllegalStateException(
					"This method may only be used if the dialog was constructed with a Skin.");

		return button(text, object, getSkin().get(ImageTextButtonStyle.class));
	}

}
