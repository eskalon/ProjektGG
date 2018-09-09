package de.gg.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.google.common.eventbus.Subscribe;

import de.gg.events.ConnectionEstablishedEvent;
import de.gg.events.ConnectionFailedEvent;
import de.gg.input.ButtonClickListener;
import de.gg.lang.Lang;
import de.gg.network.GameClient;
import de.gg.network.GameServer;
import de.gg.network.ServerDiscoveryHandler;
import de.gg.network.ServerDiscoveryHandler.HostDiscoveryListener;
import de.gg.network.messages.DiscoveryResponsePacket;
import de.gg.ui.components.AnimationlessDialog;
import de.gg.ui.components.OffsetableTextField;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class ServerBrowserScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town.jpg";
	@Asset(Texture.class)
	private final String TICK_IMAGE_PATH = "ui/icons/ready.png";
	private Texture tickTexture;
	private Dialog connectingDialog;
	private Table serverTable;
	/**
	 * This list holds all local LAN servers that were discovered.
	 */
	private List<String> dicoveredServers = new ArrayList<>();
	private Runnable discoveryThread;

	@Override
	protected void onInit(AnnotationAssetManager assetManager) {
		super.onInit(assetManager);

		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		tickTexture = assetManager.get(TICK_IMAGE_PATH);
	}

	@Override
	protected void initUI() {
		serverTable = new Table();

		ScrollPane pane = new ScrollPane(serverTable);

		ImageTextButton backButton = new ImageTextButton(
				Lang.get("ui.generic.back"), skin, "small");
		backButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						game.pushScreen("mainMenu");
					}
				});

		ImageTextButton createLobbyButton = new ImageTextButton(
				Lang.get("screen.server_browser.create_game"), skin, "small");
		createLobbyButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						game.pushScreen("lobbyCreation");
					}
				});

		ImageTextButton directConnectButton = new ImageTextButton(
				Lang.get("screen.server_browser.connect_directly"), skin,
				"small");
		directConnectButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						OffsetableTextField portInputField = new OffsetableTextField(
								String.valueOf(GameServer.DEFAULT_PORT), skin,
								5);
						portInputField.setTextFieldFilter(
								new TextField.TextFieldFilter.DigitsOnlyFilter());
						OffsetableTextField ipInputField = new OffsetableTextField(
								"127.0.0.1", skin, 5);

						AnimationlessDialog dialog = new AnimationlessDialog(
								Lang.get(
										"screen.server_browser.connect_directly"),
								skin) {
							@Override
							public void result(Object obj) {
								if ((Boolean) obj) {
									// Connect to client
									game.setClient(
											new GameClient(game.getEventBus()));
									game.getClient().connect(game.VERSION,
											ipInputField.getText(),
											Integer.valueOf(
													portInputField.getText()));

									connectingDialog = showInfoDialog(
											Lang.get("ui.generic.connecting"),
											Lang.get(
													"screen.server_browser.joining"),
											false);
								}
							}
						};
						dialog.text(Lang.get("screen.server_browser.ip"))
								.button(Lang.get("ui.generic.back"), false)
								.button(Lang.get("ui.generic.connect"), true)
								.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
						dialog.getContentTable().add(ipInputField).width(170)
								.row();
						dialog.getContentTable().add(new Label(
								Lang.get("screen.server_browser.port"), skin));
						dialog.getContentTable().add(portInputField).width(90)
								.left();
						dialog.show(stage);
					}
				});

		Table buttonTable = new Table();
		buttonTable.add(backButton);
		buttonTable.add(createLobbyButton).width(132).padLeft(47);
		buttonTable.add(directConnectButton).width(152).padLeft(47);

		discoverLanServers();

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment2"));
		mTable.add(pane).width(580).height(405).row();
		mTable.add(buttonTable).height(50).bottom();

		mainTable.add(mTable);
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
				ServerDiscoveryHandler serverDiscoveryHandler = new ServerDiscoveryHandler(
						4500);
				serverDiscoveryHandler.discoverHosts(
						GameServer.UDP_DISCOVER_PORT,
						new HostDiscoveryListener() {
							@Override
							public void onHostDiscovered(String address,
									DiscoveryResponsePacket datagramPacket) {
								if (!dicoveredServers
										.contains(datagramPacket.getGameName()
												+ datagramPacket.getPort())) {
									dicoveredServers
											.add(datagramPacket.getGameName()
													+ datagramPacket.getPort());
									addServerToUI(serverTable, address,
											datagramPacket);
								}
							}
						});
			}
		};
		(new Thread(discoveryThread)).start();
	}

	private void addServerToUI(Table serverTable, String address,
			DiscoveryResponsePacket packet) {
		ImageTextButton joinButton = new ImageTextButton(
				Lang.get("screen.server_browser.join"), skin, "small");
		joinButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						game.setClient(new GameClient(game.getEventBus()));
						game.getClient().connect(game.VERSION, address,
								packet.getPort());
						connectingDialog = showInfoDialog(
								Lang.get("ui.generic.connecting"),
								Lang.get("screen.server_browser.joining"),
								false);
					}
				});

		serverTable.left().top().add(new Image(tickTexture)).padRight(15)
				.padLeft(12);
		serverTable.add(new Label(Lang.get("screen.server_browser.server_title",
				packet.getGameName(), packet.getPlayerCount(),
				packet.getMaxPlayerCount()), skin)).expandX();
		serverTable.add(joinButton).padRight(12);
		serverTable.row().padTop(20);
	}

	@Subscribe
	public void onClientConnected(ConnectionEstablishedEvent event) {
		((LobbyScreen) game.getScreen("lobby")).setupLobby(event);
		game.pushScreen("lobby");
	}

	@Subscribe
	public void onConnectionFailed(ConnectionFailedEvent event) {
		connectingDialog.setVisible(false);
		game.setClient(null);

		if (event.getException() != null)
			showInfoDialog(Lang.get("ui.generic.error"),
					event.getException().getMessage(), true);
		else
			showInfoDialog(Lang.get("ui.generic.error"),
					event.getServerRejectionMessage().getMessage(), true);
	}

}
