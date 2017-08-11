package dev.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import dev.gg.core.Player;
import dev.gg.network.event.ClientEventHandler;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen is the main game screen and is rendered when the player is in the
 * city map.
 */
public class GameMapScreen extends BaseScreen implements ClientEventHandler {
	
	@Asset(Texture.class)
	private final String TITLE_IMAGE_PATH = "ui/images/title.png";
	private Texture titleImage;

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub
		titleImage = assetManager.get(TITLE_IMAGE_PATH);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.getSpriteBatch().begin();
		game.getSpriteBatch().setProjectionMatrix(game.getUICamera().combined);

		game.getSpriteBatch().draw(this.titleImage, 100, 100);

		game.getSpriteBatch().end();

		//game.getCurrentSession().renderMap(delta, 0, 0);
	}

	@Override
	public void show() {
		//game.setInputProcessor(this);
		game.getCurrentMultiplayerSession().setClientEventHandler(this);
	}

	@Override
	public void hide() {
		//game.setInputProcessor(null);
		game.getCurrentMultiplayerSession().setClientEventHandler(null);
	}

	@Override
	public void onNewChatMessage(short senderId, String message) {
		// TODO
	}

	@Override
	public void onPlayerDisconnect(Player player) {
		// TODO
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
