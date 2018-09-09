package de.gg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen is the first screen shown to the user when he starts the game.
 */
public class SplashScreen extends BaseScreen {

	protected long startTime = -1;
	protected long duration = 1330;
	private Texture titleImage;
	private int xPos;
	private int yPos;

	@Asset(Texture.class)
	private final String TITLE_IMAGE_PATH = "ui/images/eskalon.png";
	// private final String TITLE_ANIMATION_PATH = "ui/images/eskalon.gif";

	// Animation<TextureRegion> animation;

	@Override
	protected void onInit(AnnotationAssetManager assetManager) {
		titleImage = assetManager.get(TITLE_IMAGE_PATH);

		// animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.NORMAL,
		// Gdx.files.internal(TITLE_ANIMATION_PATH).read());

		xPos = (game.getViewportWidth() - titleImage.getWidth()) / 2;
		yPos = (game.getViewportHeight() - titleImage.getHeight()) / 2 + 15;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.getSpriteBatch().begin();
		game.getSpriteBatch().setProjectionMatrix(game.getUICamera().combined);

		game.getSpriteBatch().draw(this.titleImage, xPos, yPos);

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
		xPos = (width - titleImage.getWidth()) / 2;
		yPos = (height - titleImage.getHeight()) / 2 + 40;
	}

	@Override
	public void show() {
		// unused
	}

	@Override
	public void hide() {
		// unused
	}

	@Override
	public void dispose() {
		// unused
	}

}
