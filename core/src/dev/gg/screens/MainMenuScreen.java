package dev.gg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen represents the main menu.
 *
 */
public class MainMenuScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/castle.jpg";
	@Asset(Sound.class)
	private final String BUTTON_SOUND = "audio/button-tick.mp3";

	@Override
	protected void initUI() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		Sound clickSound = assetManager.get(BUTTON_SOUND);

		ImageTextButton multiplayerButton = new ImageTextButton("Multiplayer",
				skin);
		multiplayerButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.pushScreen("serverBrowser");
				clickSound.play(1F);
				return true;
			}
		});

		ImageTextButton exitButton = new ImageTextButton("Beenden", skin);
		exitButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);

				Gdx.app.exit();
				return true;
			}
		});

		/*
		 * multiplayerButton .addListener(new TextTooltip("You can press this",
		 * skin));
		 */
		mainTable.add(multiplayerButton).padBottom(11f);
		mainTable.row();
		mainTable.add(exitButton);
	}

}
