package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.gg.input.ButtonClickListener;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen represents the main menu.
 */
public class MainMenuScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/castle.jpg";
	@Asset(Texture.class)
	private final String LOGO_IMAGE_PATH = "ui/images/logo.png";
	@Asset(Texture.class)
	private final String GITHUB_ICON_PATH = "ui/icons/github.png";
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
				clickSound.play(1F);
				game.pushScreen("serverBrowser");
				return true;
			}
		});

		ImageTextButton settingsButton = new ImageTextButton("Einstellungen",
				skin);
		settingsButton.addListener(new ButtonClickListener(assetManager) {
			@Override
			protected void onClick() {
				game.pushScreen("settings");
			}
		});

		ImageTextButton creditsButton = new ImageTextButton("Credits", skin);
		creditsButton.addListener(new ButtonClickListener(assetManager) {
			@Override
			protected void onClick() {
				if (!game.isInDevEnv())
					game.pushScreen("credits");
			}
		});

		ImageTextButton exitButton = new ImageTextButton("Beenden", skin);
		exitButton.addListener(new ButtonClickListener(assetManager) {
			@Override
			protected void onClick() {
				Gdx.app.exit();
			}
		});

		Image logoImage = new Image(
				(Texture) assetManager.get(LOGO_IMAGE_PATH));

		ImageButton githubRepoButton = new ImageButton(
				new TextureRegionDrawable(new TextureRegion(
						assetManager.get(GITHUB_ICON_PATH, Texture.class))));
		githubRepoButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.net.openURI("https://github.com/eskalon/ProjektGG");
				return true;
			}
		});

		Label versionLabel = new Label(game.getVersion(), skin);
		Table versionTable = new Table();
		versionTable.add(versionLabel);

		// githubRepoButton.addListener(
		// new TextTooltip("Zu unserem Gihtub-Repository", skin));

		mainTable.add(logoImage).padBottom(25f).padTop(-120f).row();
		mainTable.add(multiplayerButton).padBottom(11f).row();
		mainTable.add(settingsButton).padBottom(11f).row();
		mainTable.add(creditsButton).padBottom(11f).row();
		mainTable.add(exitButton).row();

		githubRepoButton.padLeft(3).padBottom(3).bottom().left();

		GlyphLayout layout = new GlyphLayout(skin.getFont("main-19"),
				game.getVersion());
		versionTable.padBottom(28)
				.padLeft(game.getViewportWidth() * 2 - layout.width - 8);

		stage.addActor(githubRepoButton);
		stage.addActor(versionTable);
	}

}
