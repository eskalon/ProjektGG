package dev.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The base class of all UI screens. It automatically loads the
 * {@linkplain #skin skin} and sets the {@link #stage} as
 * {@linkplain InputProcessor input processor}. All actors have to be added to
 * the {@link #mainTable}.
 */
public abstract class BaseUIScreen extends BaseScreen {

	/**
	 * Contains a {@linkplain #mainTable table} by default.
	 */
	protected Stage stage;
	/**
	 * The main table, to which all {@link Actor}s will be added.
	 */
	protected Table mainTable;
	/**
	 * The default UI skin. Is automatically set.
	 */
	protected Skin skin;
	/**
	 * An easy way to add a backgroundImage is to set this variable. The screen
	 * automatically takes care of rendering it.
	 */
	protected Texture backgroundTexture;

	@Override
	protected void onInit() {
		skin = game.getUISkin();
	}

	/**
	 * This method is the called whenever this screen is {@linkplain #show
	 * shown}. The stage's actors will be constructed in this method.
	 */
	protected abstract void initUI();

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.getSpriteBatch().begin();

		if (backgroundTexture != null)
			game.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
					game.getViewportWidth(), game.getViewportHeight());

		game.getSpriteBatch().setProjectionMatrix(game.getUICamera().combined);

		stage.act(delta);
		stage.draw();

		game.getSpriteBatch().end();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		super.show();
		stage = new Stage(new ScreenViewport());
		mainTable = new Table();
		stage.addActor(mainTable);
		mainTable.setFillParent(true);

		mainTable.setDebug((boolean) game.showDebugStuff());

		game.getInputMultiplexer().addProcessor(stage);

		initUI();
	}

	@Override
	public void hide() {
		super.hide();
		game.getInputMultiplexer().removeInputProcessors();
	}

	@Override
	public void dispose() {
		if (stage != null)
			stage.dispose();
	}
}
