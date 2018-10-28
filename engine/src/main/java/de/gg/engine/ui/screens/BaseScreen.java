package de.gg.engine.ui.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import de.gg.engine.core.BaseGame;
import de.gg.engine.input.SettableKeysProcessor;

/**
 * A basic screen that takes care of registering input and event listeners when
 * used in conjunction with {@link BaseGame}.
 * 
 * @see #addInputProcessor(InputProcessor)
 * @see BaseGame#getEventBus()
 */
public abstract class BaseScreen<G extends BaseGame> extends LoadableScreen {

	protected G game;
	protected Color backgroundColor = Color.BLACK;
	/**
	 * Input processors added to this list get automatically registered when the
	 * screen is {@linkplain #show() shown} and unregistered when the screen is
	 * {@linkplain #hide() hidden}.
	 *
	 * @see #addInputProcessor(InputProcessor)
	 */
	private Array<InputProcessor> inputProcessors = new Array<>(3);

	/**
	 * Initializes the screen. Is automatically called by {@link BaseGame}.
	 *
	 * @param game
	 *            the game this screen is a part of.
	 */
	public final void init(G game) {
		this.game = game;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void show() {
		game.getEventBus().register(this);
		game.getInputMultiplexer().setProcessors(new Array<>(inputProcessors));

		for (InputProcessor p : inputProcessors) {
			if (p instanceof SettableKeysProcessor)
				((SettableKeysProcessor) p).loadKeybinds(game.getSettings());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hide() {
		game.getEventBus().unregister(this);
		game.getInputMultiplexer().removeInputProcessors(inputProcessors);
	}

	@Override
	public void pause() {
		// unused
	}

	@Override
	public void resume() {
		// unused
	}

	@Override
	public void resize(int width, int height) {
		// isn't needed as the game can't be resized
	}

	/**
	 * Adds an input processor that is automatically registered and unregistered
	 * whenever the screen is {@linkplain #show() shown}/{@linkplain #hide()
	 * hidden}. If the processor implements {@link SettableKeysProcessor} the
	 * key binds are set automatically as well.
	 *
	 * @param processor
	 *            The processor to add.
	 */
	protected void addInputProcessor(InputProcessor processor) {
		inputProcessors.add(processor);
	}

}
