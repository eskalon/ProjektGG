package de.gg.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.I18NBundle;

import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;
import de.gg.engine.lang.Lang;
import de.gg.engine.ui.screens.BaseScreen;
import de.gg.game.core.ProjektGG;
import de.gg.game.utils.DiscordGGHandler;

/**
 * This screen is the first screen shown to the user when he starts the game.
 */
public class SplashScreen extends BaseScreen<ProjektGG> {

	protected long startTime = -1;
	protected long duration = 1330;
	private int xPos;
	private int yPos;

	@InjectAsset("lang/lang")
	private I18NBundle langBundle;
	@InjectAsset("ui/images/eskalon.png")
	private Texture titleImage;
	// private final String TITLE_ANIMATION_PATH = "ui/images/eskalon.gif";

	// Animation<TextureRegion> animation;

	@Override
	protected void onInit() {
		// animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.NORMAL,
		// Gdx.files.internal(TITLE_ANIMATION_PATH).read());

		xPos = (game.getViewportWidth() - titleImage.getWidth()) / 2;
		yPos = (game.getViewportHeight() - titleImage.getHeight()) / 2 + 15;

		// Set the localization
		I18NBundle.setExceptionOnMissingKey(false);
		Lang.setBundle(langBundle);

		// Enable discord integration
		if (game.isDiscordIntegrationEnabled())
			DiscordGGHandler.getInstance().connect();
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
