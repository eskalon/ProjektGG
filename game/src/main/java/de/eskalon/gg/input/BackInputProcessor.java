package de.eskalon.gg.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import de.damios.guacamole.gdx.DefaultInputProcessor;

/**
 * This input processor takes care of a back action (right click or escape).
 */
public abstract class BackInputProcessor implements DefaultInputProcessor {

	private static final int BACK_KEY = Keys.ESCAPE;
	private static final int BACK_BUTTON = Buttons.RIGHT;

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == BACK_KEY) {
			onBackAction();
			return true;
		}

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		return button == BACK_BUTTON;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == BACK_BUTTON) {
			onBackAction();
			return true;
		}

		return false;
	}

	public abstract void onBackAction();

	/**
	 * This is a back input listener that handles clicks inside of Scene2D
	 * actors. Is usually used in addition to a {@link BackInputProcessor}.
	 */
	public static abstract class BackInputActorListener
			implements DefaultInputProcessor, EventListener {
		private static final Vector2 tmpCoords = new Vector2();

		@Override
		public boolean handle(Event e) {
			if (!(e instanceof InputEvent))
				return false;
			InputEvent event = (InputEvent) e;

			switch (event.getType()) {
			case touchDown: {
				event.toCoordinates(event.getListenerActor(), tmpCoords);
				return touchDown((int) tmpCoords.x, (int) tmpCoords.y,
						event.getPointer(), event.getButton());
			}
			case touchUp: {
				event.toCoordinates(event.getListenerActor(), tmpCoords);
				return touchUp((int) tmpCoords.x, (int) tmpCoords.y,
						event.getPointer(), event.getButton());
			}
			default:
				return false;
			}
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer,
				int button) {
			return button == BACK_BUTTON;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer,
				int button) {
			if (button == BACK_BUTTON) {
				onBackAction();
				return true;
			}

			return false;
		}

		public abstract void onBackAction();

	}

}
