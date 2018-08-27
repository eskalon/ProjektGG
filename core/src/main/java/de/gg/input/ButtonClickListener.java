package de.gg.input;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import de.gg.setting.GameSettings;

/**
 * This class takes care of a button click. It especially plays the click sound.
 */
public abstract class ButtonClickListener extends InputListener {

	private Sound clickSound;
	private GameSettings settings;

	/**
	 * @param clickSound
	 *            The used click sound.
	 * @param settings
	 *            The game's settings.
	 */
	public ButtonClickListener(Sound clickSound, GameSettings settings) {
		this.clickSound = clickSound;
		this.settings = settings;
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		if (arePreconditionsMet()) {
			clickSound.play(settings.getUIVolumeLevel());
			onClick();

			return true;
		}
		return false;
	}

	/**
	 * If the button click should only trigger in certain circumstances this can
	 * be handled by overriding this method.
	 *
	 * @return Whether the preconditions for this button click are met. Per
	 *         default this is always <code>true</code>.
	 */
	protected boolean arePreconditionsMet() {
		return true;
	}

	/**
	 * This method is responsible for taking care of the input event.
	 */
	protected abstract void onClick();

}
