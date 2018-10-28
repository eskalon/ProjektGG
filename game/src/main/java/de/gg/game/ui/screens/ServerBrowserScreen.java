package de.gg.game.ui.screens;

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

import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;
import de.gg.engine.lang.Lang;
import de.gg.engine.network.BaseGameServer;
import de.gg.engine.network.IClientConnectCallback;
import de.gg.engine.network.ServerDiscoveryHandler;
import de.gg.engine.network.ServerDiscoveryHandler.HostDiscoveryListener;
import de.gg.engine.network.message.DiscoveryResponsePacket;
import de.gg.engine.ui.components.OffsetableTextField;
import de.gg.game.events.GameDataReceivedEvent;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.network.GameClient;
import de.gg.game.ui.components.BasicDialog;

public class ServerBrowserScreen extends BaseUIScreen {

	@InjectAsset("ui/backgrounds/town.jpg")
	private Texture backgroundImage;
	@InjectAsset("ui/icons/ready.png")
	private Texture tickTexture;
	private Dialog connectingDialog;
	private Table serverTable;
	/**
	 * This list holds all local LAN servers that were discovered.
	 */
	private List<String> dicoveredServers = new ArrayList<>();
	private Runnable discoveryThread;

	@Override
	protected void onInit() {
		super.onInit();

		super.backgroundTexture = backgroundImage;
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
								String.valueOf(BaseGameServer.DEFAULT_PORT),
								skin, 5);
						portInputField.setTextFieldFilter(
								new TextField.TextFieldFilter.DigitsOnlyFilter());
						OffsetableTextField ipInputField = new OffsetableTextField(
								"127.0.0.1", skin, 5);

						BasicDialog dialog = new BasicDialog(Lang
								.get("screen.server_browser.connect_directly"),
								skin) {
							@Override
							public void result(Object obj) {
								if ((Boolean) obj) {
									// Connect to client
									game.setClient(
											new GameClient(game.getEventBus()));
									game.getClient().connect(
											new IClientConnectCallback() {
												@Override
												public void onClientConnected(
														String errorMessage) {
													ServerBrowserScreen.this
															.onClientConnected(
																	errorMessage);
												}
											}, game.VERSION,
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
				ServerDiscoveryHandler<DiscoveryResponsePacket> serverDiscoveryHandler = new ServerDiscoveryHandler<>(
						DiscoveryResponsePacket.class, 4500);
				serverDiscoveryHandler.discoverHosts(
						BaseGameServer.UDP_DISCOVER_PORT,
						new HostDiscoveryListener<>() {
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
						game.getClient().connect(new IClientConnectCallback() {
							@Override
							public void onClientConnected(String errorMessage) {
								ServerBrowserScreen.this
										.onClientConnected(errorMessage);
							}
						}, game.VERSION, address, packet.getPort());
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
	public void onGameDataReceived(GameDataReceivedEvent event) {
		((LobbyScreen) game.getScreen("lobby")).setupLobby(event);
		game.pushScreen("lobby");
	}

	private void onClientConnected(String errorMessage) {
		if (errorMessage != null) {
			connectingDialog.setVisible(false);
			game.setClient(null);

			showInfoDialog(Lang.get("ui.generic.error"), errorMessage, true);
		} else {
			connectingDialog.setVisible(false);
			showInfoDialog(Lang.get("ui.generic.connecting"),
					Lang.get("screen.server_browser.receiving"), true);
		}
	}

}
