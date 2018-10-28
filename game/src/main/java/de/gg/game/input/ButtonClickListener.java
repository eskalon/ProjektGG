package de.gg.game.input;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.google.common.base.Preconditions;

import de.gg.engine.setting.ConfigHandler;
import de.gg.engine.utils.MathUtils;

/**
 * This class takes care of a button click. It especially plays the click sound.
 */
public abstract class ButtonClickListener extends InputListener {

	private Sound clickSound;
	private ConfigHandler settings;

	/**
	 * @param clickSound
	 *            The used click sound.
	 * @param settings
	 *            The game's settings.
	 */
	public ButtonClickListener(Sound clickSound, ConfigHandler settings) {
		Preconditions.checkNotNull(clickSound);
		Preconditions.checkNotNull(settings);

		this.clickSound = clickSound;
		this.settings = settings;
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		if (arePreconditionsMet()) {
			clickSound.play(getUIVolumeLevel());
			onClick();

			return true;
		}
		return false;
	}

	private float getUIVolumeLevel() {
		return (float) MathUtils.linToExp(settings.getFloat("effectVolume", 1F)
				* settings.getFloat("masterVolume", 1F), 2);
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
