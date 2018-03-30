package dev.gg.screen;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.google.common.eventbus.Subscribe;

import de.gg.event.ConnectionEstablishedEvent;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class ServerBrowserScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town.jpg";
	@Asset(Texture.class)
	private final String TICK_IMAGE_PATH = "ui/icons/ready.png";
	@Asset(Sound.class)
	private final String BUTTON_SOUND = "audio/button-tick.mp3";
	private Dialog connectingDialog;

	@Override
	protected void initUI() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		Sound clickSound = assetManager.get(BUTTON_SOUND);

		// mainTable.setBackground(skin.getDrawable("parchment-small"));
		Table serverTable = new Table();
		ScrollPane pane = new ScrollPane(serverTable);

		// TODO gegen echte Server-Daten austauschen
		String ip = "127.0.0.1";
		int port = 12345;
		ImageTextButton joinButton = new ImageTextButton("Beitreten", skin
		/* "small" */);
		joinButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);

				game.getNetworkHandler().setUpConnectionAsClient(ip, port);

				connectingDialog = new Dialog("Verbinden...", skin);
				connectingDialog.text("Spiel beitreten...");
				connectingDialog.show(stage);

				return true;
			}
		});

		ImageTextButton backButton = new ImageTextButton("Zurück", skin
		/* "small" */);
		backButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);

				game.pushScreen("mainMenu");
				return true;
			}
		});

		ImageTextButton createLobbyButton = new ImageTextButton(
				"Spiel erstellen", skin /* "small" */);
		createLobbyButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);

				game.pushScreen("lobbyCreation");
				return true;
			}
		});

		ImageTextButton directConnectButton = new ImageTextButton(
				"Direkt verbinden", skin/* "small" */);
		directConnectButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);

				TextField portInputField = new TextField("55789", skin);
				portInputField.setTextFieldFilter(
						new TextField.TextFieldFilter.DigitsOnlyFilter());
				TextField ipInputField = new TextField("127.0.0.1", skin);

				Dialog dialog = new Dialog("Direkt verbinden", skin) {
					public void result(Object obj) {
						if ((Boolean) obj) {
							game.getNetworkHandler().setUpConnectionAsClient(
									ipInputField.getText(),
									Integer.valueOf(portInputField.getText()));

							connectingDialog = new Dialog("Verbinden...", skin);
							connectingDialog.text("Spiel beitreten...");
							connectingDialog.show(stage);
						}
					}
				};
				dialog.text("IP: ").button("Zurück", false)
						.button("Verbinden", true).key(Keys.ENTER, true)
						.key(Keys.ESCAPE, false);
				dialog.getContentTable().add(ipInputField).width(170).row();
				dialog.getContentTable().add(new Label("Port:", skin));
				dialog.getContentTable().add(portInputField).width(90).left();
				dialog.show(stage);

				return true;
			}
		});

		Table buttonTable = new Table();
		buttonTable.add(backButton);
		buttonTable.add(createLobbyButton).padLeft(55);
		buttonTable.add(directConnectButton).padLeft(55);

		serverTable.left().top()
				.add(new Image((Texture) assetManager.get(TICK_IMAGE_PATH)))
				.padRight(15).padLeft(12);
		serverTable.add(new Label("Spiel 2", skin)).expandX();
		serverTable.add(joinButton).padRight(12);
		serverTable.row().padTop(20);

		mainTable.add(serverTable).width(580).height(405).row();
		mainTable.add(buttonTable).height(50).bottom();
	}

	@Subscribe
	public void onClientConnected(ConnectionEstablishedEvent event) {
		connectingDialog.setVisible(false);
		if (event.getException() == null) {
			game.pushScreen("lobby");
		} else {
			game.setCurrentSession(null);

			Dialog dialog = new Dialog("Fehler", skin);
			dialog.text(event.getException().getMessage());
			dialog.button("Ok", true);
			dialog.key(Keys.ENTER, true);
			dialog.show(stage);
		}
	}

}
