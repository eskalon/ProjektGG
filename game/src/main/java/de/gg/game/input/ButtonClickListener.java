package de.gg.game.input;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.google.common.base.Preconditions;

import de.eskalon.commons.audio.ISoundManager;

/**
 * This class takes care of a button click. It especially plays the click sound.
 */
public abstract class ButtonClickListener extends InputListener {

	private ISoundManager soundManager;

	/**
	 * @param soundManager
	 *            the used click sound
	 */
	public ButtonClickListener(ISoundManager soundManager) {
		Preconditions.checkNotNull(soundManager);

		this.soundManager = soundManager;
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		if (arePreconditionsMet()) {
			soundManager.playSoundEffect("button_click");
			onClick();

			return true;
		}
		return false;
	}

	/**
	 * If the button click should only trigger in certain circumstances this can
	 * be taken care of by overriding this method.
	 *
	 * @return whether the preconditions for this button click are met
	 */
	protected boolean arePreconditionsMet() {
		return true;
	}

	/**
	 * This method is responsible for handling the input event.
	 */
	protected abstract void onClick();

}
