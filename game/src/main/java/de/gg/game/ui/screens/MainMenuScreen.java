package de.gg.game.ui.screens;

import com.badlogic.gdx.Gdx;
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

import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;
import de.gg.engine.lang.Lang;
import de.gg.game.input.ButtonClickListener;

/**
 * This screen represents the main menu.
 */
public class MainMenuScreen extends BaseUIScreen {

	@InjectAsset("ui/backgrounds/castle.jpg")
	private Texture backgroundImage;
	@InjectAsset("ui/images/logo.png")
	private Texture logoTexture;
	@InjectAsset("ui/icons/github.png")
	private Texture githubLogoTexture;

	@Override
	protected void onInit() {
		super.onInit();

		super.backgroundTexture = backgroundImage;
	}

	@Override
	protected void initUI() {
		ImageTextButton multiplayerButton = new ImageTextButton(
				Lang.get("screen.main_menu.multiplayer"), skin);
		multiplayerButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						game.pushScreen("serverBrowser");
					}
				});

		ImageTextButton settingsButton = new ImageTextButton(
				Lang.get("screen.main_menu.settings"), skin);
		settingsButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						((SettingsScreen) game.getScreen("settings"))
								.setCaller(MainMenuScreen.this);
						game.pushScreen("settings");
					}
				});

		ImageTextButton creditsButton = new ImageTextButton(
				Lang.get("screen.main_menu.credits"), skin);
		creditsButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						if (!game.IN_DEV_ENV)
							game.pushScreen("credits");
					}
				});

		ImageTextButton exitButton = new ImageTextButton(
				Lang.get("screen.main_menu.quit"), skin);
		exitButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						Gdx.app.exit();
					}
				});

		Image logoImage = new Image(logoTexture);

		ImageButton githubRepoButton = new ImageButton(
				new TextureRegionDrawable(
						new TextureRegion(githubLogoTexture)));
		githubRepoButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.net.openURI("https://github.com/eskalon/ProjektGG");
				return true;
			}
		});

		Label versionLabel = new Label(game.VERSION, skin);
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
				game.VERSION);
		versionTable.padBottom(28)
				.padLeft(game.getViewportWidth() * 2 - layout.width - 8);

		stage.addActor(githubRepoButton);
		stage.addActor(versionTable);
	}
}
