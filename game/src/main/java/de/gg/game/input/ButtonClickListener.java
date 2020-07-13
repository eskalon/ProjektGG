package de.gg.game.input;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.common.base.Preconditions;

import de.eskalon.commons.audio.ISoundManager;

/**
 * This class provides an easy way of handling a button click. Furthermore, it
 * plays a sound upon clicking.
 */
public abstract class ButtonClickListener extends ClickListener {

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
	public void clicked(InputEvent event, float x, float y) {
		if (arePreconditionsMet()) {
			soundManager.playSoundEffect("button_click");
			onClick();
			event.cancel();
		}
	}

	/**
	 * If the {@linkplain #onClick() button click event} should only trigger
	 * under certain circumstances this behavior can be taken care of by
	 * overriding this method.
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
