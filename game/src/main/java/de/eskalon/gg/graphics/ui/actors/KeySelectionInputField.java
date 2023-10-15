package de.eskalon.gg.graphics.ui.actors;

import javax.swing.text.JTextComponent.KeyBinding;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.input.IInputHandler;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.gg.graphics.ui.actors.dialogs.SimpleTextDialog;
import de.eskalon.gg.input.ButtonClickListener;

/**
 * An input field which allows querying {@link Keys}. Is used to set
 * {@linkplain KeyBinding key bindings}.
 */
public class KeySelectionInputField extends ImageTextButton {

	public enum BindingType {
		AXIS_MIN("keycode_min"), AXIS_MAX("keycode_max"), BINARY("keycode");

		private String settingsName;

		private BindingType(String settingsName) {
			this.settingsName = settingsName;
		}

		String getSettingsName() {
			return settingsName;
		}
	}

	public KeySelectionInputField(EskalonSettings settings, Enum id,
			BindingType bindingType, Skin skin, Stage stage,
			ISoundManager soundManager) {
		super(Keys.toString(settings.getIntProperty(IInputHandler
				.getPropertyName(id, bindingType.getSettingsName())).get()),
				skin);

		addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				SimpleTextDialog dialog = new SimpleTextDialog(
						Lang.get("dialog.key_selection.select"), skin);
				dialog.text(Lang.get("dialog.key_selection.press_key"))
						.button(Lang.get("ui.generic.back"), false)
						.key(Keys.ESCAPE, false);
				dialog.addListener(new InputListener() {
					@Override
					public boolean keyDown(InputEvent event, int keycode) {
						if (keycode != Keys.ESCAPE) {
							settings.setIntProperty(
									IInputHandler.getPropertyName(id,
											bindingType.getSettingsName()),
									keycode);

							setText(Keys.toString(keycode));
							dialog.hide();

							return true;
						}
						return false;
					}
				});
				dialog.show(stage);
			}
		});
	}

}
