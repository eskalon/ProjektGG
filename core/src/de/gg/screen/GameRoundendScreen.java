package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import de.gg.input.DefaultInputProcessor;

/**
 * This screen is rendered after a round ends.
 */
public class GameRoundendScreen extends BaseGameScreen {

	@Override
	protected void onInit() {
		this.backgroundColor = Color.DARK_GRAY;

	}

	@Override
	public void render(float delta) {
		// bewusst kein updateGame()-Call, um Session nicht (sinnlos) zu updaten
		game.getNetworkHandler().updateServer();

		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.getSpriteBatch().begin();
		game.getSpriteBatch().setProjectionMatrix(game.getUICamera().combined);

		//

		game.getSpriteBatch().end();
	}

	@Override
	public void show() {
		super.show();
		game.getInputMultiplexer().addProcessor(new DefaultInputProcessor() {
			@Override
			public boolean keyDown(int keycode) {
				System.out.println("Key pressed: " + keycode);

				game.getNetworkHandler().readyUp();

				return true;
			}
		});
	}

	@Override
	public void hide() {
		super.hide();
		game.getInputMultiplexer().removeInputProcessors();
	}

	@Override
	public void dispose() {
		//
	}

}
