package de.gg.game.ui.components;

import javax.annotation.Nullable;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.utils.ISimpleListener;

/**
 * A small dialog displaying a text, a button and nothing more.
 * 
 * @see #createAndShow(Stage, Skin, String, String)
 * @see #createAndShow(Stage, Skin, String, String, boolean, ISimpleListener)
 */
public class SimpleTextDialog extends BasicDialog {

	public SimpleTextDialog(String title, Skin skin, String windowStyleName) {
		super(title, skin, windowStyleName);

		this.setWidth(290);
		this.setHeight(177);
		this.getTitleTable().getCell(this.getTitleLabel()).padLeft(16)
				.padTop(38);
		this.getContentTable().defaults().padLeft(7).padTop(11).padRight(5)
				.center().width(270);
		this.getButtonTable().defaults().padBottom(17);
	}

	public SimpleTextDialog(String title, Skin skin) {
		this(title, skin, "default");
	}

	/**
	 * Adds a label to the content table. The dialog must have been constructed
	 * with a skin to use this method.
	 */
	@Override
	public Dialog text(String text) {
		if (getSkin() == null)
			throw new IllegalStateException(
					"This method may only be used if the dialog was constructed with a Skin.");
		return text(text, getSkin().get("text", LabelStyle.class));
	}

	/** Adds a label to the content table. */
	@Override
	public Dialog text(String text, LabelStyle labelStyle) {
		Label l = new Label(text, labelStyle);
		l.setWrap(true);
		l.setAlignment(Align.center);
		return text(l);
	}

	public static SimpleTextDialog createAndShow(Stage stage, Skin skin,
			String title, String text, boolean showOkButton,
			@Nullable ISimpleListener listener) {
		SimpleTextDialog dialog = new SimpleTextDialog(title, skin) {
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

	public static SimpleTextDialog createAndShow(Stage stage, Skin skin,
			String title, String text) {
		return createAndShow(stage, skin, title, text, true, null);
	}

}
