package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.google.common.eventbus.Subscribe;

import de.gg.event.HouseEnterEvent;
import de.gg.event.HouseSelectionEvent;
import de.gg.input.GameSpeedInputProcessor;
import de.gg.input.MapMovementInputController;
import de.gg.input.MapSelectionInputController;
import de.gg.render.SceneRenderer;
import de.gg.render.TestShader;
import de.gg.ui.AnimationlessDialog;
import de.gg.util.Log;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen is the main game screen and is rendered when the player is in the
 * city view.
 */
public class GameMapScreen extends BaseGameScreen {

	@Asset(Text.class)
	private static final String FRAGMENT_SHADER_PATH = "shaders/single_color.fragment.glsl";

	private SceneRenderer sceneRenderer;

	private MapMovementInputController movementInputController;
	private MapSelectionInputController selectionInputController;
	private InputMultiplexer gameInputProcessors;

	private Renderable renderable;

	private BitmapFont font;

	/**
	 * The pause dialog.
	 */
	private AnimationlessDialog pauseDialog;
	private AnimationlessDialog houseSelectionDialog;
	private boolean pauseShown = false;

	// Sphere stuff (temp)
	private Shader shader;
	private RenderContext renderContext;

	@Override
	protected void onInit() {
		super.onInit();
		font = skin.getFont("main-19");

		Text t = game.getAssetManager().get(FRAGMENT_SHADER_PATH);
		sceneRenderer = new SceneRenderer(game.getGameCamera().getCamera(),
				game.getCurrentSession().getCity(), t.getString());

		// SPHERE (temp)
		ModelBuilder modelBuilder = new ModelBuilder();
		Model model = modelBuilder.createSphere(2f, 2f, 2f, 20, 20,
				new Material(),
				Usage.Position | Usage.Normal | Usage.TextureCoordinates);

		NodePart blockPart = model.nodes.get(0).parts.get(0);

		renderable = new Renderable();
		blockPart.setRenderable(renderable);
		renderable.environment = sceneRenderer.environment;
		renderable.worldTransform.idt();

		renderContext = new RenderContext(
				new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
		shader = new TestShader(game.getAssetManager());
		shader.init();

		this.gameInputProcessors = new InputMultiplexer();
		this.selectionInputController = new MapSelectionInputController(
				game.getSettings(), game.getEventBus(),
				game.getGameCamera().getCamera(),
				game.getCurrentSession().getCity());
		this.gameInputProcessors.addProcessor(selectionInputController);

		this.movementInputController = new MapMovementInputController(
				game.getGameCamera().getCamera(), game.getSettings());
		this.gameInputProcessors.addProcessor(movementInputController);

		GameSpeedInputProcessor gameSpeedInputProcessor = new GameSpeedInputProcessor(
				game.getNetworkHandler());
		this.gameInputProcessors.addProcessor(gameSpeedInputProcessor);
	}

	@Override
	protected void initUI() {
		// TODO

		pauseDialog = new AnimationlessDialog("", skin) {
			protected void result(Object object) {
				pauseShown = false;
				if (object == (Integer) 1) {
					((SettingsScreen) game.getScreen("settings"))
							.setCaller(GameMapScreen.this);
					game.pushScreen("settings");
				} else {
					game.getNetworkHandler().disconnect();
					game.setCurrentSession(null);
					game.pushScreen("mainMenu");
				}
			};
		};
		pauseDialog.button("Settings", 1).button("Verbindung trennen", 2);

		houseSelectionDialog = new AnimationlessDialog("", skin);
		houseSelectionDialog.button("Test");
	}

	@Override
	public void renderGame(float delta) {
		movementInputController.update();
		selectionInputController.update();

		// Render city
		sceneRenderer.render();

		// Render sphere with shader (temp)
		renderContext.begin();
		shader.begin(game.getGameCamera().getCamera(), renderContext);
		shader.render(renderable);
		shader.end();
		renderContext.end();

		// FPS counter
		if (game.showFPSCounter()) {
			game.getSpriteBatch().begin();
			font.draw(game.getSpriteBatch(),
					String.valueOf(Gdx.graphics.getFramesPerSecond()), 6,
					game.getViewportHeight() - 4);
			game.getSpriteBatch().end();
		}
	}

	@Subscribe
	public void onHouseSelectionEvent(HouseSelectionEvent ev) {
		Log.debug("Input", "Gebäude ausgewählt: %d", ev.getId());

		if (ev.getId() == -1) {
			houseSelectionDialog.hide();
			return;
		}

		houseSelectionDialog.show(stage);
		houseSelectionDialog.setModal(false);
		// houseSelectionDialog.setPosition(ev.getClickX(),
		// game.getViewportHeight() - ev.getClickY());
		houseSelectionDialog.setPosition(
				game.getViewportWidth() - houseSelectionDialog.getWidth(),
				game.getViewportHeight() - 230
						- houseSelectionDialog.getHeight());
	}

	@Subscribe
	public void onHouseEnterEvent(HouseEnterEvent ev) {
		((GameInHouseScreen) game.getScreen("house"))
				.setSelectedHouseId(ev.getId());
		game.pushScreen("house");
	}

	@Override
	public void show() {
		super.show();
		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ESCAPE) {
					if (pauseShown)
						pauseDialog.hide();
					else {
						pauseDialog.show(stage);
						// selectionInputController.resetSelection();
					}

					pauseShown = !pauseShown;

					return true;
				}
				return false;
			}
		});

		movementInputController.resetInput();
		game.getInputMultiplexer().addProcessor(gameInputProcessors);
	}

	@Override
	public void hide() {
		super.hide();

		selectionInputController.resetSelection();
	}

	@Override
	public void dispose() {
		super.dispose();

		if (isLoaded())
			sceneRenderer.dispose();
	}

}
