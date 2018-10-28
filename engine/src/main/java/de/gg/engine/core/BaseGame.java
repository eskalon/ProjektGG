package de.gg.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.google.common.base.Preconditions;

import de.gg.engine.data.DataStore;
import de.gg.engine.input.BaseInputMultiplexer;
import de.gg.engine.log.Log;
import de.gg.engine.setting.ConfigHandler;
import de.gg.engine.ui.screens.BaseScreen;
import de.gg.engine.ui.screens.LoadableScreen;
import de.gg.engine.utils.EventQueueBus;

/**
 * This is a basic screen taking care of registering input and event listeners.
 * <p>
 * Only sub classes of {@link BaseScreen} which use this game class as their
 * generic type are supported, otherwise the
 * {@linkplain #onScreenInitialization(LoadableScreen) screen initialization}
 * fails.
 */
public abstract class BaseGame extends ScreenGame {

	/**
	 * the version the application is running on. Set via the jar manifest. Is
	 * <code>Development</code> if the game is started in a development
	 * environment.
	 */
	public final String VERSION;
	/**
	 * Whether the application is running in a development environment. Checks
	 * if a {@linkplain #VERSION version} is set in the jar manifest.
	 */
	public final boolean IN_DEV_ENV;

	/**
	 * The game's settings.
	 */
	protected ConfigHandler configHandler;
	/**
	 * A data store containing the game's data.
	 */
	private final DataStore data;

	private BaseInputMultiplexer inputProcessor;

	/**
	 * Event bus. All events are queued first and then taken care of in the
	 * rendering thread.
	 */
	private EventQueueBus eventBus;

	private String configName;

	private boolean debug;

	public BaseGame(String configName, boolean debug) {
		Preconditions.checkNotNull(configName);
		Preconditions.checkArgument(!configName.isEmpty());
		this.IN_DEV_ENV = getClass().getPackage()
				.getImplementationVersion() == null;
		this.VERSION = IN_DEV_ENV ? "Development"
				: getClass().getPackage().getImplementationVersion();
		this.configName = configName;
		this.debug = debug;
		this.data = new DataStore();
	}

	@Override
	public final void create() {
		super.create();

		if (debug)
			Log.enableDebugLogging();
		else
			Log.disableDebugLogging();

		Log.info("Start ", "Version: '%s', In Dev Environment: '%b'", VERSION,
				IN_DEV_ENV);

		this.viewportWidth = Gdx.graphics.getWidth();
		this.viewportHeight = Gdx.graphics.getHeight();

		// Config
		this.configHandler = new ConfigHandler(configName);

		// Input multiplexer
		setInputMultiplexer(new BaseInputMultiplexer());

		// Create the event bus
		this.eventBus = new EventQueueBus();

		onGameInitialization();
	}

	protected void setInputMultiplexer(BaseInputMultiplexer inputProcessor) {
		this.inputProcessor = inputProcessor;
		Gdx.input.setInputProcessor(inputProcessor);

	}

	protected abstract void onGameInitialization();

	@Override
	public void render() {
		// Takes care of posting the events in the rendering thread
		eventBus.distributeEvents();

		super.render();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void onScreenInitialization(LoadableScreen screen) {
		((BaseScreen) screen).init(this);
	}

	/**
	 * @return the game's settings.
	 */
	public ConfigHandler getSettings() {
		return configHandler;
	}

	/**
	 * @return the game's data.
	 */
	public DataStore getData() {
		return data;
	}

	/**
	 * @return the events bus. See {@link EventQueueBus}. Events are processed
	 *         in the rendering thread.
	 */
	public EventQueueBus getEventBus() {
		return eventBus;
	}

	/**
	 * Returns the input multiplexer of the game. Should be used to add input
	 * listeners instead of {@link Input#setInputProcessor(InputProcessor)}.
	 *
	 * @return the game's input multiplexer.
	 */
	public BaseInputMultiplexer getInputMultiplexer() {
		return inputProcessor;
	}

	/**
	 * @return whether the debug flag is set and thus debug stuff should get
	 *         rendered.
	 */
	public boolean isInDebugMode() {
		return debug;
	}

}
