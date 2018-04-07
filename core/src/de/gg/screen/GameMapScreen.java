package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.google.common.eventbus.Subscribe;

import de.gg.event.HouseEnterEvent;
import de.gg.event.HouseSelectionEvent;
import de.gg.input.MapMovementInputController;
import de.gg.input.MapSelectionInputController;
import de.gg.render.SceneRenderer;
import de.gg.render.TestShader;
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

	private Renderable renderable;

	private BitmapFont font;

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

		this.selectionInputController = new MapSelectionInputController(
				game.getSettings(), game.getEventBus(),
				game.getGameCamera().getCamera(),
				game.getCurrentSession().getCity());

		this.movementInputController = new MapMovementInputController(
				game.getGameCamera().getCamera());
	}

	@Override
	protected void initUI() {
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
		Log.debug("Input", "Single selection: %d", ev.getId());

		// TODO show house selection dialog
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
		game.getInputMultiplexer().addProcessor(selectionInputController);
		game.getInputMultiplexer().addProcessor(movementInputController);
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
