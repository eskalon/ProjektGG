package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen represents the main menu.
 */
public class MainMenuScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/castle.jpg";
	@Asset(Texture.class)
	private final String LOGO_IMAGE_PATH = "ui/images/logo.png";
	@Asset(Sound.class)
	private final String BUTTON_SOUND = "audio/button-tick.mp3";
	private int xPos;
	private int yPos;

	@Override
	protected void initUI() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		Sound clickSound = assetManager.get(BUTTON_SOUND);

		ImageTextButton multiplayerButton = new ImageTextButton("Multiplayer",
				skin);
		multiplayerButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);
				game.pushScreen("serverBrowser");
				return true;
			}
		});

		ImageTextButton settingsButton = new ImageTextButton("Einstellungen",
				skin);
		settingsButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);

				// Gdx.app.exit();
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

		Image logoImage = new Image(
				(Texture) assetManager.get(LOGO_IMAGE_PATH));

		/*
		 * multiplayerButton .addListener(new TextTooltip("You can press this",
		 * skin));
		 */
		mainTable.add(logoImage).padBottom(25f).padTop(-120f);
		mainTable.row();
		mainTable.add(multiplayerButton).padBottom(11f);
		mainTable.row();
		mainTable.add(settingsButton).padBottom(11f);
		mainTable.row();

		mainTable.add(exitButton);
	}

}
