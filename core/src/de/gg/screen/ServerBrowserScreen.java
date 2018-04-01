package de.gg.screen;

import java.util.ArrayList;
import java.util.List;

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
import de.gg.network.NetworkHandler;
import de.gg.network.NetworkHandler.HostDiscoveryListener;
import de.gg.network.message.DiscoveryResponsePacket;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class ServerBrowserScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town.jpg";
	@Asset(Texture.class)
	private final String TICK_IMAGE_PATH = "ui/icons/ready.png";
	@Asset(Sound.class)
	private final String BUTTON_SOUND = "audio/button-tick.mp3";
	private Dialog connectingDialog;
	private Sound clickSound;
	private Table serverTable;
	/**
	 * This list holds all local LAN servers that were discovered.
	 */
	private List<String> dicoveredServers = new ArrayList<>();
	private Runnable discoveryThread;

	@Override
	protected void initUI() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		clickSound = assetManager.get(BUTTON_SOUND);

		// mainTable.setBackground(skin.getDrawable("parchment-small"));
		serverTable = new Table();
		ScrollPane pane = new ScrollPane(serverTable);

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

				TextField portInputField = new TextField(
						String.valueOf(NetworkHandler.DEFAULT_PORT), skin);
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

		discoverLanServers();

		mainTable.add(serverTable).width(580).height(405).row();
		mainTable.add(buttonTable).height(50).bottom();
	}

	/**
	 * Discovers available servers in the local network and adds them to the ui.
	 */
	private void discoverLanServers() {
		serverTable.clear();
		dicoveredServers.clear();
		discoveryThread = new Runnable() {
			@Override
			public void run() {
				game.getNetworkHandler()
						.discoverHosts(new HostDiscoveryListener() {
							@Override
							public void onHostDiscovered(String address,
									DiscoveryResponsePacket datagramPacket) {
								if (!dicoveredServers
										.contains(datagramPacket.getGameName()
												+ datagramPacket.getPort())) {
									dicoveredServers
											.add(datagramPacket.getGameName()
													+ datagramPacket.getPort());
									addServerToUI(serverTable,
											datagramPacket.getGameName(),
											address, datagramPacket.getPort());
								}
							}
						});
			}
		};
		(new Thread(discoveryThread)).start();
	}

	private void addServerToUI(Table serverTable, String gameName, String ip,
			int port) {
		// TODO gegen echte Server-Daten austauschen
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

		serverTable.left().top()
				.add(new Image((Texture) assetManager.get(TICK_IMAGE_PATH)))
				.padRight(15).padLeft(12);
		serverTable.add(new Label(gameName, skin)).expandX();
		serverTable.add(joinButton).padRight(12);
		serverTable.row().padTop(20);
	}

	@Subscribe
	public void onClientConnected(ConnectionEstablishedEvent event) {
		connectingDialog.setVisible(false);
		if (event.getException() == null) {
			((LobbyScreen) game.getScreen("lobby")).setupLobby(event);
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
