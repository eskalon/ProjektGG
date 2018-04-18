package de.gg.screen;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.gg.input.ButtonClickListener;
import de.gg.ui.KeySelectionInputField;
import de.gg.ui.KeySelectionInputField.KeySelectionEventListener;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class SettingsScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town3.jpg";
	@Asset(Sound.class)
	private final String BUTTON_SOUND = "audio/button-tick.mp3";
	
	private BaseScreen caller;

	@Override
	protected void initUI() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);

		// VOLUME
		Label masterVolume = new Label("Master Volume: ", skin);
		Slider masterSlider = new Slider(0, 1, 0.05F, false, skin);
		masterSlider.setValue(game.getSettings().getMasterVolume());
		masterSlider.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer,
					int button) {
				game.getSettings().setMasterVolume(masterSlider.getValue());
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}
		});

		Label effectVolume = new Label("Effect Volume: ", skin);
		Slider effectSlider = new Slider(0, 1, 0.05F, false, skin);
		effectSlider.setValue(game.getSettings().getEffectVolume());
		effectSlider.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer,
					int button) {
				game.getSettings().setEffectVolume(effectSlider.getValue());
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}
		});

		Label musicVolume = new Label("Music Volume: ", skin);
		Slider musicSlider = new Slider(0, 1, 0.05F, false, skin);
		musicSlider.setValue(game.getSettings().getMusicVolume());
		musicSlider.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer,
					int button) {
				game.getSettings().setMusicVolume(musicSlider.getValue());
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}
		});

		// KEYS
		Label forwardLabel = new Label("Forward: ", skin);
		KeySelectionInputField forwardButton = new KeySelectionInputField(
				Keys.toString(game.getSettings().getForwardKey()), skin, stage,
				assetManager, new KeySelectionEventListener() {
					@Override
					public void onKeySelection(int key) {
						game.getSettings().setForwardKey(key);
					}
				});
		Label leftLabel = new Label("Left: ", skin);
		KeySelectionInputField leftButton = new KeySelectionInputField(
				Keys.toString(game.getSettings().getLeftKey()), skin, stage,
				assetManager, new KeySelectionEventListener() {
					@Override
					public void onKeySelection(int key) {
						game.getSettings().setLeftKey(key);
					}
				});
		Label backwardLabel = new Label("Backward: ", skin);
		KeySelectionInputField backwardButton = new KeySelectionInputField(
				Keys.toString(game.getSettings().getBackwardKey()), skin, stage,
				assetManager, new KeySelectionEventListener() {
					@Override
					public void onKeySelection(int key) {
						game.getSettings().setBackwardKey(key);
					}
				});
		Label rightLabel = new Label("Right: ", skin);
		KeySelectionInputField rightButton = new KeySelectionInputField(
				Keys.toString(game.getSettings().getRightKey()), skin, stage,
				assetManager, new KeySelectionEventListener() {
					@Override
					public void onKeySelection(int key) {
						game.getSettings().setRightKey(key);
					}
				});

		ImageTextButton backButton = new ImageTextButton("Zurück", skin,
				"small");
		backButton.addListener(new ButtonClickListener(assetManager) {
			@Override
			protected void onClick() {
				if(caller instanceof BaseGameScreen)
					game.pushScreen("map");
				else if(caller instanceof MainMenuScreen)
					game.pushScreen("mainMenu");
			}
		});

		Table settingsTable = new Table();
		Table settings2ColTable = new Table();
		settings2ColTable.padTop(60);
		Table buttonTable = new Table();

		settings2ColTable.add(forwardLabel).padBottom(30);
		settings2ColTable.add(forwardButton).padBottom(30);

		settings2ColTable.add(masterVolume).padBottom(30).padLeft(70);
		settings2ColTable.add(masterSlider).padBottom(30).row();

		settings2ColTable.add(leftLabel).padBottom(30);
		settings2ColTable.add(leftButton).padBottom(30);

		settings2ColTable.add(effectVolume).padBottom(30).padLeft(70);
		settings2ColTable.add(effectSlider).padBottom(30).row();

		settings2ColTable.add(backwardLabel).padBottom(30);
		settings2ColTable.add(backwardButton).padBottom(30);

		settings2ColTable.add(musicVolume).padBottom(30).padLeft(70);
		settings2ColTable.add(musicSlider).padBottom(30).row();

		settings2ColTable.add(rightLabel).padBottom(30);
		settings2ColTable.add(rightButton).padBottom(30);

		settingsTable.left().top().add(settings2ColTable).padBottom(40).row();

		buttonTable.add(backButton);

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment2"));
		mTable.add(settingsTable).width(580).height(405).row();
		mTable.add(buttonTable).height(50).bottom();

		mainTable.add(mTable);
	}

	public BaseScreen getCaller() {
		return caller;
	}
	
	public void setCaller(BaseScreen caller) {
		this.caller = caller;
	}
}
