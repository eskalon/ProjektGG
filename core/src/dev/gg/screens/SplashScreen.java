package dev.gg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen is the first screen shown to the user when he starts the game.
 */
public class SplashScreen extends BaseScreen {

	protected long startTime = -1;
	protected long duration = 1250;
	private Texture logoImage;
	private int xPos;
	private int yPos;

	@Asset(Texture.class)
	private final String LOGO_IMAGE_PATH = "ui/images/logo.png";

	@Override
	protected void onInit() {
		logoImage = assetManager.get(LOGO_IMAGE_PATH);
		xPos = (game.getViewportWidth() - logoImage.getWidth()) / 2;
		yPos = (game.getViewportHeight() - logoImage.getHeight()) / 2 + 40;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.getSpriteBatch().begin();
		game.getSpriteBatch().setProjectionMatrix(game.getUICamera().combined);

		game.getSpriteBatch().draw(this.logoImage, xPos, yPos);

		if (startTime == -1) {
			this.startTime = System.currentTimeMillis();
		}

		if ((startTime + duration) < System.currentTimeMillis()) {
			game.pushScreen("loading");
		}

		game.getSpriteBatch().end();
	}

	@Override
	public void resize(int width, int height) {
		xPos = (width - logoImage.getWidth()) / 2;
		yPos = (height - logoImage.getHeight()) / 2 + 40;
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

}
