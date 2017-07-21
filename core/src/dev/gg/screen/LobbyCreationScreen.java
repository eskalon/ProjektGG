package dev.gg.screen;

import java.io.IOException;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import dev.gg.callback.IHostCallback;
import dev.gg.core.GameSession.GameDifficulty;
import dev.gg.network.MultiplayerSession;
import dev.gg.network.event.ClientEventHandler;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class LobbyCreationScreen extends BaseUIScreen implements IHostCallback {

	@Asset(Sound.class)
	private final String BUTTON_SOUND = "audio/button-tick.mp3";
	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town.jpg";
	private Dialog connectingDialog;

	@Override
	protected void initUI() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		Sound clickSound = assetManager.get(BUTTON_SOUND);

		Label nameLabel = new Label("Name: ", skin);
		Label portLabel = new Label("Port: ", skin);

		TextField nameField = new TextField("", skin);
		TextField portField = new TextField("55789", skin);
		portField.setTextFieldFilter(
				new TextField.TextFieldFilter.DigitsOnlyFilter());

		Label difficultyLabel = new Label("Schwierigkeit: ", skin);
		CheckBox easyDifficultyCheckbox = new CheckBox("Einfach", skin);
		CheckBox normalDifficultyCheckbox = new CheckBox("Normal", skin);
		CheckBox hardDifficultyCheckbox = new CheckBox("Schwer", skin);
		ButtonGroup speedGroup = new ButtonGroup();
		speedGroup.add(easyDifficultyCheckbox);
		speedGroup.add(normalDifficultyCheckbox);
		speedGroup.add(hardDifficultyCheckbox);
		normalDifficultyCheckbox.setChecked(true);

		ImageTextButton backButton = new ImageTextButton("Zur�ck", skin);
		backButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.pushScreen("serverBrowser");
				clickSound.play(1F);
				return true;
			}
		});

		IHostCallback callback = this;

		ImageTextButton createButton = new ImageTextButton("Erstellen", skin);
		createButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (!nameField.getText().isEmpty()
						&& !portField.getText().isEmpty()) {
					clickSound.play(1F);

					GameDifficulty difficulty = GameDifficulty.NORMAL;

					if (speedGroup.getChecked()
							.equals(easyDifficultyCheckbox)) {
						difficulty = GameDifficulty.EASY;
					} else if (speedGroup.getChecked()
							.equals(normalDifficultyCheckbox)) {
						difficulty = GameDifficulty.NORMAL;
					} else if (speedGroup.getChecked()
							.equals(hardDifficultyCheckbox)) {
						difficulty = GameDifficulty.HARD;
					}

					MultiplayerSession session = new MultiplayerSession();
					session.setClientEventHandler(
							(ClientEventHandler) game.getScreen("lobby"));
					game.setCurrentSession(session);

					// Sever & Client starten
					session.setUpAsHost(Integer.valueOf(portField.getText()),
							difficulty, callback);
					connectingDialog = new Dialog("Starten...", skin);
					connectingDialog.text("Server startet...");
					connectingDialog.show(stage);
				} else {
					Dialog dialog = new Dialog("Felder unausgef�llt", skin);
					dialog.text(
							"Zum Starten m�ssen alle Felder ausgef�llt sein");
					dialog.button("Ok", true);
					dialog.key(Keys.ENTER, true);
					dialog.show(stage);
				}

				return true;
			}
		});

		Table settingsTable = new Table();
		Table settings2ColTable = new Table();
		Table settings3ColTable = new Table();
		Table buttonTable = new Table();

		settings2ColTable.add(nameLabel).padBottom(30);
		settings2ColTable.add(nameField).padBottom(30).row();
		settings2ColTable.add(portLabel);
		settings2ColTable.add(portField).row();

		settings3ColTable.add(difficultyLabel).colspan(3).row();
		settings3ColTable.add(easyDifficultyCheckbox);
		settings3ColTable.add(normalDifficultyCheckbox);
		settings3ColTable.add(hardDifficultyCheckbox);

		settingsTable.left().top().add(settings2ColTable).padBottom(40).row();
		settingsTable.add(settings3ColTable).row();

		buttonTable.add(backButton);
		buttonTable.add(createButton).padLeft(65);

		mainTable.add(settingsTable).width(580).height(405);
		mainTable.row();
		mainTable.add(buttonTable).height(50).bottom();

	}

	@Override
	public void onHostStarted(IOException e) {
		connectingDialog.setVisible(false);
		if (e == null) {
			game.pushScreen("lobby");
		} else {
			game.setCurrentSession(null);
			Dialog dialog = new Dialog("Fehler", skin);
			dialog.text(e.getMessage());
			dialog.button("Ok", true);
			dialog.key(Keys.ENTER, true);
			dialog.show(stage);
		}
	}

}