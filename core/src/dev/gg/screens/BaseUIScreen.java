package dev.gg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * The base class of all UI screens. It automatically loads the {@linkplain #skin skin} and sets
 * the {@link #stage} as {@linkplain InputProcessor input processor}. All actors
 * have to be added to the {@link #mainTable}.
 * 
 */
public abstract class BaseUIScreen extends BaseScreen {

	private Stage stage;
	/**
	 * The main table, to which all {@link Actor}s will be added.
	 */
	protected Table mainTable;
	protected Skin skin;
	/**
	 * An easy way to add a backgroundImage is to set this variable. The screen
	 * automatically takes care of rendering it.
	 */
	protected Texture backgroundTexture;

	@Override
	protected void onInit() {
		stage = new Stage();
		mainTable = new Table();
		mainTable.setFillParent(true);
		stage.addActor(mainTable);

		mainTable.setDebug((boolean) game.getDataStore().get("debug"));

		skin = game.getUISkin();

		initUI();
	}

	/**
	 * This method is the UI equivalent of {@link #onInit()}. The stage will be
	 * constructed in this method.
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
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
