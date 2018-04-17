package de.gg.util.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.gg.input.ButtonClickListener;
import de.gg.setting.GameSettings;

public class SettingKeyInputField extends ImageTextButton {

	private GameSettings settings;

	public SettingKeyInputField(String text, Skin skin, Stage stage,
			AssetManager assetManager, GameSettings settings, String key) {
		super(text, skin, "small");
		this.settings = settings;
		addListener(new ButtonClickListener(assetManager) {
			@Override
			protected void onClick() {
				AnimationlessDialog dialog = new AnimationlessDialog(
						"Neue Taste", skin) {
				};
				dialog.text("Dr�cke eine Taste").button("Zurück", false)
						.key(Keys.ESCAPE, false);
				dialog.addListener(new InputListener() {
					@Override
					public boolean keyDown(InputEvent event, int keycode) {
						if (!keyIsAlreadySet(keycode)) {
							setText(Keys.toString(keycode));
							dialog.hide();
						} else {

						}
						return super.keyDown(event, keycode);
					}
				});
				dialog.show(stage);
			}
		});
	}

	private boolean keyIsAlreadySet(int key) {
		return settings.getForwardKey() == key || settings.getLeftKey() == key
				|| settings.getBackwardKey() == key
				|| settings.getRightKey() == key;
	}

}
