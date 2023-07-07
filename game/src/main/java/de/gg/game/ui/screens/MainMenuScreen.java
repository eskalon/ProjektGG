package de.gg.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.lang.Lang;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.input.ButtonClickListener;

/**
 * This screen represents the main menu.
 */
public class MainMenuScreen extends AbstractGGUIScreen {

	@Asset("ui/backgrounds/main_menu_screen.png")
	private Texture backgroundImage;

	private boolean shownForFirstTime = true;

	public MainMenuScreen(ProjektGGApplication application) {
		super(application);
	}

	@Override
	protected void create() {
		super.create();

		setImage(backgroundImage);

		ImageTextButton multiplayerButton = new ImageTextButton(
				Lang.get("screen.main_menu.multiplayer"), skin, "large");
		multiplayerButton.addListener(
				new FadeOutUIClickListener(application.getSoundManager(), stage,
						"server_browser", "blendingTransition"));

		ImageTextButton settingsButton = new ImageTextButton(
				Lang.get("screen.main_menu.settings"), skin, "large");
		settingsButton.addListener(
				new FadeOutUIClickListener(application.getSoundManager(), stage,
						"settings", "blendingTransition"));

		ImageTextButton creditsButton = new ImageTextButton(
				Lang.get("screen.main_menu.credits"), skin, "large");
		creditsButton.addListener(
				new FadeOutUIClickListener(application.getSoundManager(), stage,
						"credits", "longBlendingTransition"));

		ImageTextButton exitButton = new ImageTextButton(
				Lang.get("screen.main_menu.quit"), skin, "large");
		exitButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						Gdx.app.exit();
					}
				});

		Image logoImage = new Image(skin.getDrawable("logo"));

		ImageButton githubRepoButton = new ImageButton(
				skin.getDrawable("icon_github"));
		githubRepoButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.net.openURI("https://github.com/eskalon/ProjektGG");
				return true;
			}
		});

		mainTable.add(logoImage).padBottom(25f).padTop(-80f).row();
		mainTable.add(multiplayerButton).padBottom(11f).row();
		mainTable.add(settingsButton).padBottom(11f).row();
		mainTable.add(creditsButton).padBottom(11f).row();
		mainTable.add(exitButton).row();

		githubRepoButton.padLeft(3).padBottom(3).bottom().left();
		stage.addActor(githubRepoButton);
		stage.addAction(Actions.touchable(Touchable.disabled));
	}

	@Override
	protected void setUIValues() {
		// Fade in the UI & enable the input
		SequenceAction sequence2 = new SequenceAction();
		sequence2.addAction(Actions.delay(1.1F));
		sequence2.addAction(Actions.touchable(Touchable.enabled));

		SequenceAction sequence = new SequenceAction();
		sequence.addAction(Actions.delay(shownForFirstTime ? 0.85F : 0.17F));
		sequence.addAction(Actions.parallel(
				Actions.alpha(1F, 1.6F, Interpolation.pow2In), sequence2));

		stage.addAction(Actions.alpha(0F));
		stage.addAction(sequence);

		if (shownForFirstTime)
			shownForFirstTime = false;
	}

	public class FadeOutUIClickListener extends ButtonClickListener {

		private Stage stageToFadeOut;
		private String nextScreen, transition;

		public FadeOutUIClickListener(ISoundManager soundManager,
				Stage stageToFadeOut, String nextScreen, String transition) {
			super(soundManager);
			this.stageToFadeOut = stageToFadeOut;
			this.nextScreen = nextScreen;
			this.transition = transition;
		}

		@Override
		protected void onClick() {
			SequenceAction sequence2 = new SequenceAction();
			sequence2.addAction(Actions.touchable(Touchable.disabled));
			sequence2.addAction(Actions.delay(0.35F));
			sequence2.addAction(Actions.run(() -> application.getScreenManager()
					.pushScreen(nextScreen, transition)));

			stageToFadeOut.addAction(Actions.parallel(
					Actions.alpha(0F, 0.45F, Interpolation.pow2In), sequence2));
		}

	}

}
