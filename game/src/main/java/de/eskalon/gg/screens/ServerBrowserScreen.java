package de.eskalon.gg.screens;

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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import de.damios.guacamole.ICallback;
import de.damios.guacamole.concurrent.ThreadHandler;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.event.EventBus;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.net.ServerDiscoveryHandler;
import de.eskalon.commons.net.SimpleGameServer;
import de.eskalon.commons.net.packets.S2CDiscoveryResponsePacket;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.screens.AbstractEskalonUIScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.graphics.ui.actors.OffsettableTextField;
import de.eskalon.gg.graphics.ui.actors.dialogs.BasicDialog;
import de.eskalon.gg.graphics.ui.actors.dialogs.SimpleTextDialog;
import de.eskalon.gg.input.BackInputProcessor;
import de.eskalon.gg.input.BackInputProcessor.BackInputActorListener;
import de.eskalon.gg.input.ButtonClickListener;
import de.eskalon.gg.net.GameClient;

public class ServerBrowserScreen extends AbstractEskalonUIScreen {

	private @Inject EskalonScreenManager screenManager;
	private @Inject EventBus eventBus;
	private @Inject ISoundManager soundManager;
	private @Inject Skin skin;
	private @Inject ProjektGGApplicationContext appContext;

	@Asset("ui/backgrounds/server_browser_screen.jpg")
	private @Inject Texture backgroundTexture;

	private ICallback connectionCallback;

	private Dialog connectingDialog;
	private Table serverTable;
	/**
	 * This list holds all local LAN servers that were discovered.
	 */
	private List<String> dicoveredServers = new ArrayList<>();
	private Future<Void> discoveryFuture;
	private long lastRefreshTime = -1;

	private boolean connectionLost = false;

	@Override
	public void show() {
		super.show();

		setImage(backgroundTexture);
		setMode(ImageScreenMode.CENTERED_ORIGINAL_SIZE);

		BackInputProcessor backInput = new BackInputProcessor() {
			@Override
			public void onBackAction() {
				screenManager.pushScreen(MainMenuScreen.class,
						"blendingTransition");
			}
		};
		addInputProcessor(backInput);
		mainTable.addListener(new BackInputActorListener() {
			@Override
			public void onBackAction() {
				backInput.onBackAction();
			}
		});

		this.connectionCallback = new ICallback() {
			@Override
			public void onSuccess(Object param) {
				connectingDialog.hide();
				LobbyScreen screen = EskalonInjector.instance()
						.getInstance(LobbyScreen.class);
				screen.setLobbyData((LobbyData) param);
				screenManager.pushScreen(screen,
						appContext.getTransitions().get("blendingTransition"));
			}

			@Override
			public void onFailure(Object param) {
				connectingDialog.setVisible(false);
				appContext.setClient(null);
				SimpleTextDialog.createAndShow(stage, skin,
						Lang.get("ui.generic.error"), (String) param);
			}
		};

		ImageTextButton backButton = new ImageTextButton(
				Lang.get("ui.generic.back"), skin);
		backButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				screenManager.pushScreen(MainMenuScreen.class,
						"blendingTransition");
			}
		});

		ImageTextButton createLobbyButton = new ImageTextButton(
				Lang.get("screen.server_browser.create_game"), skin);
		createLobbyButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				screenManager.pushScreen(LobbyCreationScreen.class,
						"shortBlendingTransition");
			}
		});

		ImageTextButton directConnectButton = new ImageTextButton(
				Lang.get("screen.server_browser.connect_directly"), skin);
		directConnectButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				OffsettableTextField portInputField = new OffsettableTextField(
						String.valueOf(SimpleGameServer.DEFAULT_PORT), skin, 5);
				portInputField.setTextFieldFilter(
						new TextField.TextFieldFilter.DigitsOnlyFilter());
				OffsettableTextField ipInputField = new OffsettableTextField(
						"127.0.0.1", skin, 5);

				BasicDialog dialog = new BasicDialog(
						Lang.get("screen.server_browser.connect_directly"),
						skin) {
					@Override
					public void result(Object obj) {
						if ((Boolean) obj) {
							// Connect to client
							appContext.setClient(EskalonInjector.instance()
									.getInstance(GameClient.class));
							appContext.getClient().connect(connectionCallback,
									appContext.getVersion(),
									ipInputField.getText(),
									Integer.valueOf(portInputField.getText()));
							connectingDialog = SimpleTextDialog.createAndShow(
									stage, skin,
									Lang.get("ui.generic.connecting"),
									Lang.get("screen.server_browser.joining"),
									false, null);
						}
					}
				};
				dialog.text(Lang.get("screen.server_browser.ip"))
						.button(Lang.get("ui.generic.back"), false)
						.button(Lang.get("ui.generic.connect"), true)
						.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
				dialog.getContentTable().add(ipInputField).width(170).row();
				dialog.getContentTable()
						.add(new Label(Lang.get("screen.server_browser.port"),
								skin, "dark_text"));
				dialog.getContentTable().add(portInputField).width(150).left();
				dialog.show(stage);
			}
		});

		ImageButton refreshButton = new ImageButton(skin, "refresh");
		refreshButton.addListener(new ButtonClickListener(soundManager) {
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

		if (connectionLost) { // i.e., connection to previous game
								// was lost
			SimpleTextDialog.createAndShow(stage, skin,
					Lang.get("ui.generic.error"),
					Lang.get("ui.generic.disconnected"));
		}

		discoverServers();
	}

	private void discoverServers() {
		if (discoveryFuture != null && !discoveryFuture.isDone()
				&& System.currentTimeMillis() - lastRefreshTime < 750)
			return;

		lastRefreshTime = System.currentTimeMillis();

		serverTable.clearChildren();
		dicoveredServers.clear();

		discoveryFuture = ThreadHandler.instance().executeRunnable(() -> {
			ServerDiscoveryHandler<S2CDiscoveryResponsePacket> serverDiscoveryHandler = new ServerDiscoveryHandler<>(
					S2CDiscoveryResponsePacket.class, 4500);
			serverDiscoveryHandler.discoverHosts(
					SimpleGameServer.UDP_DISCOVER_PORT,
					(address, datagramPacket) -> {
						if (!dicoveredServers
								.contains(datagramPacket.getGameName()
										+ datagramPacket.getPort())) {
							dicoveredServers.add(datagramPacket.getGameName()
									+ datagramPacket.getPort());
							addServerToUI(serverTable, address, datagramPacket);
						}
					});
		});

		System.out.println(ThreadHandler.instance().getActiveThreadCount());
	}

	private void addServerToUI(Table serverTable, String address,
			S2CDiscoveryResponsePacket packet) {
		ImageTextButton joinButton = new ImageTextButton(
				Lang.get("screen.server_browser.join"), skin);
		joinButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				appContext.setClient(EskalonInjector.instance()
						.getInstance(GameClient.class));
				appContext.getClient().connect(connectionCallback,
						appContext.getVersion(), address, packet.getPort());
				connectingDialog = SimpleTextDialog.createAndShow(stage, skin,
						Lang.get("ui.generic.connecting"),
						Lang.get("screen.server_browser.joining"), false, null);
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

	public void setConnectionLost(boolean connectionLost) {
		this.connectionLost = connectionLost;
	}

}
