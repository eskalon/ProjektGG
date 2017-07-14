package dev.gg.screens;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class ServerBrowserScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town.jpg";
	@Asset(Texture.class)
	private final String TICK_IMAGE_PATH = "ui/icons/tick.png";
	@Asset(Sound.class)
	private final String BUTTON_SOUND = "audio/button-tick.mp3";

	@Override
	protected void initUI() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		Sound clickSound = assetManager.get(BUTTON_SOUND);

		Table contentTable = new Table();
		contentTable.setBackground(skin.getDrawable("parchment-small"));
		contentTable.setDebug(true);
		Table serverTable = new Table();
		ScrollPane pane = new ScrollPane(serverTable);

		ImageTextButton joinButton = new ImageTextButton("Beitreten", skin,
				"small");
		joinButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				// game.setCurrentGame(game);
				game.pushScreen("lobby");
				clickSound.play(1F);
				return true;
			}
		});

		ImageTextButton backLobbyButton = new ImageTextButton("Zurück", skin,
				"small");
		backLobbyButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.pushScreen("mainMenu");
				clickSound.play(1F);
				return true;
			}
		});

		ImageTextButton createLobbyButton = new ImageTextButton(
				"Spiel erstellen", skin, "small");
		createLobbyButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.pushScreen("lobbyCreation");
				clickSound.play(1F);
				return true;
			}
		});

		Table buttonTable = new Table();
		buttonTable.add(backLobbyButton);
		buttonTable.add(createLobbyButton);

		serverTable.add(new Image((Texture) assetManager.get(TICK_IMAGE_PATH)))
				.padRight(15).padLeft(12);
		serverTable.add(new Label("Spiel 2", skin)).expandX();
		serverTable.add(joinButton).padRight(12);
		serverTable.row().padTop(20);
		
		contentTable.add(serverTable).expandX();
		contentTable.row().expandY().align(Align.bottom);
		contentTable.add(buttonTable);

		mainTable.add(contentTable);

	}

}
