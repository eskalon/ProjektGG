package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

import de.gg.entity.Building;
import de.gg.input.MapMovementInputController;
import de.gg.input.MapSelectionInputController;
import de.gg.render.RenderData;
import de.gg.render.SceneRenderer;
import de.gg.render.TestShader;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen is the main game screen and is rendered when the player is in the
 * city view.
 */
public class GameMapScreen extends BaseGameScreen {

	@Asset(Texture.class)
	private final String TITLE_IMAGE_PATH = "ui/images/title.png";
	@Asset(Model.class)
	private final String TEST_SCENE_PATH = "models/invaderscene.g3db";
	private Texture titleImage;

	private SceneRenderer sceneRenderer;

	private Shader shader;
	private RenderContext renderContext;

	private MapMovementInputController movementInputController;
	private MapSelectionInputController selectionInputController;

	private Renderable renderable;

	@Override
	protected void onInit() {
		titleImage = assetManager.get(TITLE_IMAGE_PATH);

		sceneRenderer = new SceneRenderer(game.getGameCamera().getCamera(),
				game.getCurrentSession().getCity());

		// Load the scene
		Model scene = assetManager.get(TEST_SCENE_PATH);
		for (int i = 0; i < scene.nodes.size; i++) {
			String id = scene.nodes.get(i).id;
			RenderData instance = new RenderData(scene, id, true);

			if (id.equals("space")) {
				game.getCurrentSession().getCity().setSkybox(instance);

				/*
				 * Renderable renderable = new Renderable();
				 * instance.getRenderable(renderable);
				 * 
				 * renderable.environment = sceneRenderer.environment;
				 * renderable.worldTransform.idt(); this.shader = new
				 * DefaultShader(renderable);
				 */

				continue;
			}

			game.getCurrentSession().getCity().getBuildings()
					.add(new Building(i, instance));

			/*
			 * if (id.equals("ship")) ship = instance; else if
			 * (id.startsWith("block")) blocks.add(instance); else if
			 * (id.startsWith("invader")) invaders.add(instance);
			 */
		}

		// SPHERE
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
				game.getCurrentSession().getCity().getBuildings());

		this.movementInputController = new MapMovementInputController(
				game.getGameCamera().getCamera());
	}

	@Override
	protected void initUI() {
	}

	@Override
	public void renderGame(float delta) {
		movementInputController.update();

		// Render city
		sceneRenderer.render();

		// Render sphere with shader
		renderContext.begin();
		shader.begin(game.getGameCamera().getCamera(), renderContext);
		shader.render(renderable);
		shader.end();
		renderContext.end();
	}

	@Override
	public void show() {
		super.show();
		game.getInputMultiplexer().addProcessor(selectionInputController);
		game.getInputMultiplexer().addProcessor(movementInputController);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (isLoaded())
			sceneRenderer.dispose();
	}

}
