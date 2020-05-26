package de.gg.game.ui.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.google.common.eventbus.Subscribe;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.misc.ThreadHandler;
import de.eskalon.commons.utils.ISuccessCallback;
import de.gg.engine.network.BaseGameServer;
import de.gg.engine.network.ServerDiscoveryHandler;
import de.gg.engine.network.message.DiscoveryResponsePacket;
import de.gg.engine.ui.components.OffsettableTextField;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.events.LobbyDataReceivedEvent;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.network.GameClient;
import de.gg.game.ui.components.BasicDialog;
import de.gg.game.ui.components.SimpleTextDialog;

public class ServerBrowserScreen extends AbstractGGUIScreen {

	@Asset("ui/backgrounds/server_browser_screen.jpg")
	private Texture backgroundTexture;

	private ISuccessCallback connectionCallback;

	private Dialog connectingDialog;
	private Table serverTable;
	/**
	 * This list holds all local LAN servers that were discovered.
	 */
	private List<String> dicoveredServers = new ArrayList<>();
	private Future<Void> discoveryFuture;

	public ServerBrowserScreen(ProjektGGApplication application) {
		super(application);
	}

	@Override
	protected void create() {
		super.create();
		setImage(backgroundTexture);

		this.connectionCallback = new ISuccessCallback() {
			@Override
			public void onSuccess(Object param) {
			}

			@Override
			public void onFailure(Object param) {
				connectingDialog.setVisible(false);
				application.setClient(null);
				SimpleTextDialog.createAndShow(stage, skin,
						Lang.get("ui.generic.error"), (String) param);
			}
		};

		ImageTextButton backButton = new ImageTextButton(
				Lang.get("ui.generic.back"), skin);
		backButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						application.getScreenManager().pushScreen("main_menu",
								"blendingTransition");
					}
				});

		ImageTextButton createLobbyButton = new ImageTextButton(
				Lang.get("screen.server_browser.create_game"), skin);
		createLobbyButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						application.getScreenManager().pushScreen(
								"lobby_creation", "shortBlendingTransition");
					}
				});

		ImageTextButton directConnectButton = new ImageTextButton(
				Lang.get("screen.server_browser.connect_directly"), skin);
		directConnectButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						OffsettableTextField portInputField = new OffsettableTextField(
								String.valueOf(BaseGameServer.DEFAULT_PORT),
								skin, 5);
						portInputField.setTextFieldFilter(
								new TextField.TextFieldFilter.DigitsOnlyFilter());
						OffsettableTextField ipInputField = new OffsettableTextField(
								"127.0.0.1", skin, 5);

						BasicDialog dialog = new BasicDialog(Lang
								.get("screen.server_browser.connect_directly"),
								skin) {
							@Override
							public void result(Object obj) {
								if ((Boolean) obj) {
									// Connect to client
									application.setClient(new GameClient(
											application.getEventBus()));
									application.getClient().connect(
											connectionCallback,
											application.VERSION,
											ipInputField.getText(),
											Integer.valueOf(
													portInputField.getText()));
									connectingDialog = SimpleTextDialog
											.createAndShow(stage, skin, Lang
													.get("ui.generic.connecting"),
													Lang.get(
															"screen.server_browser.joining"),
													false, null);
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
						dialog.getContentTable().add(portInputField).width(150)
								.left();
						dialog.show(stage);
					}
				});

		ImageButton refreshButton = new ImageButton(skin, "refresh");
		refreshButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						discoverServers();
					}
				});

		Table titleTable = new Table();
		titleTable.add(new Label(Lang.get("screen.server_browser.title"), skin,
				"title")).padTop(25);

		Table serverHeaderTable = new Table();
		serverHeaderTable
				.add(new Label(Lang.get("screen.server_browser.header_status"),
						skin))
				.left().padLeft(10).padRight(158);
		serverHeaderTable.add(
				new Label(Lang.get("screen.server_browser.header_name"), skin))
				.left().expandX();
		serverHeaderTable.add(refreshButton).padLeft(15).padRight(10).row();

		serverTable = new Table();
		ScrollPane pane = new ScrollPane(serverTable);

		Table buttonTable = new Table();
		buttonTable.add(backButton);
		buttonTable.add(directConnectButton).width(158).padLeft(45);
		buttonTable.add(createLobbyButton).width(136).padLeft(45);

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment2"));
		mTable.add(titleTable).row();
		mTable.add(serverHeaderTable).width(580).padTop(25).row();
		mTable.add(new Image(skin.getDrawable("white_bar"))).padTop(2).center()
				.expandX().row();
		mTable.add(pane).width(580).height(265).padTop(10).row();
		mTable.add(buttonTable).height(50).bottom();

		mainTable.add(mTable);
	}

	@Override
	protected void setUIValues() {
		if (pushParams != null) {
			// Client was disconnected from a game
			SimpleTextDialog.createAndShow(stage, skin,
					Lang.get("ui.generic.error"),
					Lang.get("ui.generic.disconnected"));
		}

		discoverServers();
	}

	private void discoverServers() {
		serverTable.clear();
		dicoveredServers.clear();
		if (discoveryFuture == null || discoveryFuture.isDone())
			discoveryFuture = ThreadHandler.getInstance()
					.executeRunnable(() -> {
						ServerDiscoveryHandler<DiscoveryResponsePacket> serverDiscoveryHandler = new ServerDiscoveryHandler<>(
								DiscoveryResponsePacket.class, 4500);
						serverDiscoveryHandler.discoverHosts(
								BaseGameServer.UDP_DISCOVER_PORT,
								(address, datagramPacket) -> {
									if (!dicoveredServers.contains(
											datagramPacket.getGameName()
													+ datagramPacket
															.getPort())) {
										dicoveredServers.add(datagramPacket
												.getGameName()
												+ datagramPacket.getPort());
										addServerToUI(serverTable, address,
												datagramPacket);
									}
								});
					});
	}

	private void addServerToUI(Table serverTable, String address,
			DiscoveryResponsePacket packet) {
		ImageTextButton joinButton = new ImageTextButton(
				Lang.get("screen.server_browser.join"), skin);
		joinButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						application.setClient(
								new GameClient(application.getEventBus()));
						application.getClient().connect(connectionCallback,
								application.VERSION, address, packet.getPort());
						connectingDialog = SimpleTextDialog.createAndShow(stage,
								skin, Lang.get("ui.generic.connecting"),
								Lang.get("screen.server_browser.joining"),
								false, null);
					}
				});

		serverTable.left().top()
				.add(new Image(
						packet.getPlayerCount() < packet.getMaxPlayerCount()
								? skin.getDrawable("icon_on")
								: skin.getDrawable("icon_off")))
				.padRight(10).padLeft(10);
		serverTable.add(new Label(
				Lang.get("screen.server_browser.server_players",
						packet.getPlayerCount(), packet.getMaxPlayerCount()),
				skin)).padRight(15);
		serverTable.add(new Label(Lang.get("screen.server_browser.server_title",
				packet.getGameName()), skin)).expandX();
		serverTable.add(joinButton).padRight(10).padLeft(15);
		serverTable.row().padTop(20);
	}

	@Subscribe
	public void onGameDataReceived(LobbyDataReceivedEvent event) {
		connectingDialog.hide();
		application.getScreenManager().pushScreen("lobby",
				"blendingTransition");
	}

}
