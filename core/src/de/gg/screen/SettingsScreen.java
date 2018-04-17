package de.gg.screen;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.gg.setting.GameSettings;
import de.gg.util.ui.SettingKeyInputField;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class SettingsScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town3.jpg";
	@Asset(Sound.class)
	private final String BUTTON_SOUND = "audio/button-tick.mp3";

	@Override
	protected void initUI() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);

		Label masterVolume = new Label("Master Volume: ", skin);
		Slider masterSlider = new Slider(0, 100, 1, false, skin);

		Label effectVolume = new Label("Effect Volume: ", skin);
		Slider effectSlider = new Slider(0, 100, 1, false, skin);

		Label musicVolume = new Label("Music Volume: ", skin);
		Slider musicSlider = new Slider(0, 100, 1, false, skin);

		Label forwardLabel = new Label("Forward: ", skin);
		SettingKeyInputField forwardButton = new SettingKeyInputField("W", skin,
				stage, assetManager, game.getSettings(),
				GameSettings.FORWARD_KEY);

		Label leftLabel = new Label("Left: ", skin);
		SettingKeyInputField leftButton = new SettingKeyInputField("A", skin,
				stage, assetManager, game.getSettings(), GameSettings.LEFT_KEY);

		Label backwardLabel = new Label("Backward: ", skin);
		SettingKeyInputField backwardButton = new SettingKeyInputField("S",
				skin, stage, assetManager, game.getSettings(),
				GameSettings.BACKWARD_KEY);

		Label rightLabel = new Label("Right: ", skin);
		SettingKeyInputField rightButton = new SettingKeyInputField("D", skin,
				stage, assetManager, game.getSettings(),
				GameSettings.RIGHT_KEY);

		ImageTextButton backButton = new ImageTextButton("Zurück", skin,
				"small");
		ImageTextButton createButton = new ImageTextButton("Erstellen", skin,
				"small");

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
		buttonTable.add(createButton).padLeft(65);

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment2"));
		mTable.add(settingsTable).width(580).height(405).row();
		mTable.add(buttonTable).height(50).bottom();

		mainTable.add(mTable);
	}

}
