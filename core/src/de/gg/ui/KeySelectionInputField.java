package de.gg.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.gg.input.ButtonClickListener;
import de.gg.setting.GameSettings;

/**
 * An input field with which a keyboard key can get queried.
 */
public class KeySelectionInputField extends ImageTextButton {

	/**
	 * @param text
	 *            The text of the input field. Normally the currently set key
	 *            bind.
	 * @param skin
	 *            The ui skin.
	 * @param stage
	 *            The stage this input field gets added to.
	 * @param assetManager
	 *            The game's asset manager.
	 * @param settings
	 *            The game's settings.
	 * @param listener
	 *            The listener for the key selection event.
	 */
	public KeySelectionInputField(String text, Skin skin, Stage stage,
			AssetManager assetManager, GameSettings settings,
			KeySelectionEventListener listener) {
		super(text, skin, "small");

		addListener(new ButtonClickListener(assetManager, settings) {
			@Override
			protected void onClick() {
				AnimationlessDialog dialog = new AnimationlessDialog(
						"Taste belegen", skin) {
				};
				dialog.text("Drücke eine Taste").button("Zurück", false)
						.key(Keys.ESCAPE, false);
				dialog.addListener(new InputListener() {
					@Override
					public boolean keyDown(InputEvent event, int keycode) {
						listener.onKeySelection(keycode);

						setText(Keys.toString(keycode));
						dialog.hide();

						return true;
					}
				});
				dialog.show(stage);
			}
		});
	}

	public interface KeySelectionEventListener {
		/**
		 * Called when a key is selected by the {@link KeySelectionInputField}
		 * 
		 * @param key
		 *            The selected key's code
		 */
		public void onKeySelection(int key);
	}

}
