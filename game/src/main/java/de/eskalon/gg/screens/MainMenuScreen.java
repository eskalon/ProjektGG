package de.eskalon.gg.screens;

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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.AbstractEskalonUIScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.input.ButtonClickListener;

/**
 * This screen represents the main menu.
 */
public class MainMenuScreen extends AbstractEskalonUIScreen {

	private @Inject ProjektGGApplicationContext appContext;
	private @Inject EskalonScreenManager screenManager;
	private @Inject ISoundManager soundManager;
	private @Inject Skin skin;

	@Asset("ui/backgrounds/main_menu_screen.png")
	private @Inject Texture backgroundImage;

	@Override
	public void show() {
		super.show();

		setImage(backgroundImage);

		ImageTextButton multiplayerButton = new ImageTextButton(
				Lang.get("screen.main_menu.multiplayer"), skin, "large");
		multiplayerButton.addListener(new FadeOutUIClickListener(soundManager,
				stage, ServerBrowserScreen.class, "blendingTransition"));

		ImageTextButton settingsButton = new ImageTextButton(
				Lang.get("screen.main_menu.settings"), skin, "large");
		settingsButton.addListener(new FadeOutUIClickListener(soundManager,
				stage, SettingsScreen.class, "blendingTransition"));

		ImageTextButton creditsButton = new ImageTextButton(
				Lang.get("screen.main_menu.credits"), skin, "large");
		creditsButton.addListener(new FadeOutUIClickListener(soundManager,
				stage, CreditsScreen.class, "longBlendingTransition"));

		ImageTextButton exitButton = new ImageTextButton(
				Lang.get("screen.main_menu.quit"), skin, "large");
		exitButton.addListener(new ButtonClickListener(soundManager) {
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

		// Fade in the UI & enable the input
		SequenceAction sequence2 = new SequenceAction();
		sequence2.addAction(Actions.delay(1.1F));
		sequence2.addAction(Actions.touchable(Touchable.enabled));

		SequenceAction sequence = new SequenceAction();
		sequence.addAction(Actions.delay(!appContext.getObjectStorage()
				.containsKey("mainmenu_already_shown") ? 0.85F : 0.17F));
		sequence.addAction(Actions.parallel(
				Actions.fadeIn(1.6F, Interpolation.pow2In), sequence2));

		stage.addAction(Actions.alpha(0F));
		stage.addAction(sequence);

		appContext.getObjectStorage().put("mainmenu_already_shown", 1);
	}

	public class FadeOutUIClickListener extends ButtonClickListener {

		private Stage stageToFadeOut;
		private Class<? extends AbstractEskalonScreen> nextScreen;
		private String transition;

		public FadeOutUIClickListener(ISoundManager soundManager,
				Stage stageToFadeOut,
				Class<? extends AbstractEskalonScreen> nextScreen,
				String transition) {
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
			sequence2.addAction(Actions.run(
					() -> screenManager.pushScreen(nextScreen, transition)));

			// FIXME: shortly before switching the screen, the UI is flickering

			stageToFadeOut.addAction(Actions.parallel(
					Actions.fadeOut(0.45F, Interpolation.pow2In), sequence2));
		}

	}

}
