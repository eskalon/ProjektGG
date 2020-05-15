package de.gg.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.lang.Lang;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.input.ButtonClickListener;

/**
 * This screen represents the main menu.
 */
public class MainMenuScreen extends AbstractGGUIScreen {

	@Asset("ui/backgrounds/main_menu_screen.png")
	private Texture backgroundImage;
	@Asset("ui/images/logo.png")
	private Texture logoTexture;
	@Asset("ui/icons/github.png")
	private Texture githubLogoTexture;

	public MainMenuScreen(ProjektGGApplication application) {
		super(application);
	}

	@Override
	protected void create() {
		super.create();

		setImage(backgroundImage);

		ImageTextButton multiplayerButton = new ImageTextButton(
				Lang.get("screen.main_menu.multiplayer"), skin);
		multiplayerButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						SequenceAction sequence2 = new SequenceAction();
						sequence2.addAction(Actions.delay(0.35F));
						sequence2.addAction(Actions.run(() -> application
								.getScreenManager().pushScreen("server_browser",
										"blendingTransition")));

						stage.addAction(Actions.alpha(1F));
						stage.addAction(Actions.parallel(
								Actions.alpha(0F, 0.45F, Interpolation.pow2In),
								sequence2));
					}
				});

		ImageTextButton settingsButton = new ImageTextButton(
				Lang.get("screen.main_menu.settings"), skin);
		settingsButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						SequenceAction sequence2 = new SequenceAction();
						sequence2.addAction(Actions.delay(0.35F));
						sequence2.addAction(Actions.run(
								() -> application.getScreenManager().pushScreen(
										"settings", "blendingTransition")));

						stage.addAction(Actions.alpha(1F));
						stage.addAction(Actions.parallel(
								Actions.alpha(0F, 0.45F, Interpolation.pow2In),
								sequence2));
					}
				});

		ImageTextButton creditsButton = new ImageTextButton(
				Lang.get("screen.main_menu.credits"), skin);
		creditsButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						SequenceAction sequence2 = new SequenceAction();
						sequence2.addAction(Actions.delay(0.35F));
						sequence2.addAction(Actions.run(
								() -> application.getScreenManager().pushScreen(
										"credits", "longBlendingTransition")));

						stage.addAction(Actions.alpha(1F));
						stage.addAction(Actions.parallel(
								Actions.alpha(0F, 0.45F, Interpolation.pow2In),
								sequence2));
					}
				});

		ImageTextButton exitButton = new ImageTextButton(
				Lang.get("screen.main_menu.quit"), skin);
		exitButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
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

		// githubRepoButton.addListener(
		// new TextTooltip("Zu unserem Gihtub-Repository", skin));

		mainTable.add(logoImage).padBottom(25f).padTop(-80f).row();
		mainTable.add(multiplayerButton).padBottom(11f).row();
		mainTable.add(settingsButton).padBottom(11f).row();
		mainTable.add(creditsButton).padBottom(11f).row();
		mainTable.add(exitButton).row();

		githubRepoButton.padLeft(3).padBottom(3).bottom().left();
		stage.addActor(githubRepoButton);
	}

	@Override
	protected void setUIValues() {
		// not needed as there are no changeable values
		SequenceAction sequence = new SequenceAction();
		sequence.addAction(Actions.alpha(0F));
		sequence.addAction(Actions.delay(0.87F));
		sequence.addAction(Actions.alpha(1F, 1.6F, Interpolation.pow2In));
		stage.addAction(sequence);
	}

}
