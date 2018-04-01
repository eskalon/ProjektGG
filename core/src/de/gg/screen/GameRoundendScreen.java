package de.gg.screen;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import de.gg.input.DefaultInputProcessor;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen is rendered after a round ends.
 */
public class GameRoundendScreen extends BaseGameScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/table.jpg";
	@Asset(Sound.class)
	private final String FLIP_SOUND = "audio/flip-page.mp3";
	private Sound flipSound;

	public GameRoundendScreen() {
		super(false);
	}

	@Override
	protected void onInit() {
		this.backgroundColor = Color.DARK_GRAY;
		this.backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		this.flipSound = assetManager.get(FLIP_SOUND);
	}

	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
	}

	@Override
	public void show() {
		super.show();
		game.getInputMultiplexer().addProcessor(new DefaultInputProcessor() {
			@Override
			public boolean keyDown(int keycode) {
				System.out.println("Key pressed: " + keycode);

				flipSound.play(1F);
				game.getNetworkHandler().readyUp();

				return true;
			}
		});
	}

	@Override
	public void renderGame(float delta) {
		game.getNetworkHandler().updateServer();
	}

	@Override
	public void dispose() {
		super.dispose();

		// if(isLoaded())
		// dispose loaded stuff
	}

}
