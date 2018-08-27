package de.gg.screen;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import de.gg.core.ProjektGG;
import de.gg.input.SettableKeysProcessor;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

/**
 * A basic screen that takes care of registering input and event listeners when
 * used in conjunction with {@link ProjektGG}.
 * 
 * @see #addInputProcessor(InputProcessor)
 * @see ProjektGG#getEventBus()
 */
public abstract class BaseScreen extends LoadableScreen {

	protected ProjektGG game;
	protected Color backgroundColor = Color.BLACK;
	/**
	 * Input processors added to this list get automatically registered when the
	 * screen is {@linkplain #show() shown} and unregistered when the screen is
	 * {@linkplain #hide() hidden}.
	 *
	 * @see #addInputProcessor(InputProcessor)
	 */
	private Array<InputProcessor> inputProcessors = new Array<>(4);

	/**
	 * Initializes the screen. Is automatically called by {@link ProjectGG}.
	 *
	 * @param game
	 * @param assetManager
	 */
	public final void init(ProjektGG game) {
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
