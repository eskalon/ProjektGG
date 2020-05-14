package de.gg.game.ui.components;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.input.KeyBinding;
import de.eskalon.commons.lang.Lang;
import de.gg.game.input.ButtonClickListener;

/**
 * An input field with which {@link Keys} can be queried.
 */
public class KeySelectionInputField extends ImageTextButton {

	public KeySelectionInputField(final KeyBinding keybind, Skin skin,
			Stage stage, ISoundManager soundManager) {
		super(keybind.toString(), skin, "small");

		addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				BasicDialog dialog = new BasicDialog(
						Lang.get("dialog.key_selection.select"), skin);
				dialog.text(Lang.get("dialog.key_selection.press_key"))
						.button("Zurück", false).key(Keys.ESCAPE, false);
				dialog.addListener(new InputListener() {
					@Override
					public boolean keyDown(InputEvent event, int keycode) {
						keybind.setKeycode(keycode);

						setText(Keys.toString(keycode));
						dialog.hide();

						return true;
					}
				});
				dialog.show(stage);
			}
		});
	}

}
