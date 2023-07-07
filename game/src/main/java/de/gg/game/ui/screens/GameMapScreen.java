package de.gg.game.ui.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.crashinvaders.vfx.effects.ChainVfxEffect;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;

import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.assets.Text;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.event.Subscribe;
import de.eskalon.commons.graphics.PostProcessingPipeline;
import de.eskalon.commons.input.DefaultInputHandler;
import de.eskalon.commons.input.IInputHandler;
import de.eskalon.commons.lang.Lang;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.events.FullHourEvent;
import de.gg.game.events.HouseEnterEvent;
import de.gg.game.events.HouseSelectionEvent;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.input.GameSpeedInputProcessor;
import de.gg.game.input.MapMovementInputController;
import de.gg.game.input.MapSelectionInputController;
import de.gg.game.misc.GameClock;
import de.gg.game.model.World;
import de.gg.game.model.entities.Player;
import de.gg.game.model.types.PlayerIcon;
import de.gg.game.model.types.PositionType;
import de.gg.game.network.GameClient;
import de.gg.game.network.GameServer;
import de.gg.game.session.GameSession;
import de.gg.game.thirdparty.DiscordGGHandler;
import de.gg.game.ui.components.BasicDialog;
import de.gg.game.ui.components.SimpleTextDialog;
import de.gg.game.ui.rendering.CameraWrapper;
import de.gg.game.ui.rendering.GameRenderer;

/**
 * This screen is the main game screen and is rendered when the player is in the
 * city view.
 */
public class GameMapScreen extends AbstractGameScreen {

	public enum GameMapAxisBinding {
		MOVE_LEFT_RIGHT, MOVE_FORWARDS_BACKWARDS, ZOOM;
	}

	public enum GameMapBinaryBinding {
		INCREASE_SPEED, DECREASE_SPEED, ROTATE_CAMERA_BUTTON, SELECT_BUILDING;
	}

	private static final Logger LOG = LoggerService
			.getLogger(GameMapScreen.class);

	@Asset("shaders/single_color_selection.frag")
	private Text fragmentShader;

	private CameraWrapper camera;
	private GameRenderer gameRenderer;

	private IInputHandler<GameMapAxisBinding, GameMapBinaryBinding> inputHandler;

	private MapSelectionInputController selectionInputController;
	private MapMovementInputController movementInputController;
	private GameSpeedInputProcessor gameSpeedInputProcessor;

	private Label dateLabel;
	private ImageButton iconButton;

	/**
	 * The pause dialog.
	 */
	private BasicDialog pauseDialog;
	private BasicDialog houseSelectionDialog;
	private boolean pauseShown = false;

	private PostProcessingPipeline postProcessor;
	private ChainVfxEffect pausePostProcessingEffect;

	public GameMapScreen(ProjektGGApplication application) {
		super(application);
	}

	@Override
	protected void create() {
		super.create();

		/*
		 * RENDERING
		 */
		camera = new CameraWrapper(67, application.getWidth(),
				application.getHeight());
		camera.setPosition(application.getWidth() / 2,
				application.getHeight() / 2, 0);

		postProcessor = new PostProcessingPipeline(application.getWidth(),
				application.getHeight(), true);
		pausePostProcessingEffect = new GaussianBlurEffect();

		gameRenderer = new GameRenderer(camera.getCamera(),
				fragmentShader.getString());

		/*
		 * INPUT
		 */
		inputHandler = new DefaultInputHandler<>(application.getSettings(),
				GameMapAxisBinding.class, GameMapBinaryBinding.class);
		addInputProcessor((DefaultInputHandler) inputHandler);

		// Object selection
		selectionInputController = new MapSelectionInputController(
				application.getEventBus(), camera.getCamera());
		inputHandler.addListener(selectionInputController);

		// Game speed
		gameSpeedInputProcessor = new GameSpeedInputProcessor();
		inputHandler.addListener(gameSpeedInputProcessor);

		// Map movement
		movementInputController = new MapMovementInputController(camera,
				application.getSettings());
		inputHandler.addListener(movementInputController);

		/*
		 * UI
		 */
		// CHARACTER DIALOG
		BasicDialog characterMenuDialog = new BasicDialog(
				Lang.get("screen.map.character_config"), skin, "big");
		ImageTextButton closeCharacterMenuButton = new ImageTextButton(
				Lang.get("ui.generic.close"), skin);
		closeCharacterMenuButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						characterMenuDialog.hide();
					}
				});

		Table characterMenuTable = new Table();
		Table changeableCharacterMenuTable = new Table();
		ImageTextButton characterMenuTab1Button = new ImageTextButton(
				Lang.get("screen.map.character_config.character"), skin);
		ImageTextButton characterMenuTab2Button = new ImageTextButton(
				Lang.get("screen.map.character_config.privileges"), skin);
		characterMenuTab1Button.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						// CHARACTER & FAMILY
						changeableCharacterMenuTable.clear();
						changeableCharacterMenuTable.add(new Label(
								Lang.get("screen.map.character_config.name"),
								skin, "dark")).padBottom(50).row();
						changeableCharacterMenuTable.add(new Label(
								Lang.get("screen.map.character_config.family"),
								skin, "dark")).row();
						changeableCharacterMenuTable.add(new Label(
								Lang.get("screen.map.character_config.find_so"),
								skin, "dark"));
					}
				});
		characterMenuTab2Button.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						// PRIVILEGES
						changeableCharacterMenuTable.clear();
						changeableCharacterMenuTable.add(new Label(Lang.get(
								"screen.map.character_config.privileges_list"),
								skin, "dark")).row();

						if (application.getClient().getLocalPlayerCharacter()
								.getPosition() != null) {
							// TODO Position privileges

							// TODO Impeachment
							ImageTextButton kickButton = new ImageTextButton(
									Lang.get(
											"screen.map.character_config.privilege.impeach"),
									skin);
							kickButton.addListener(new ButtonClickListener(
									application.getSoundManager()) {
								@Override
								protected void onClick() {
									short mayor = application.getClient()
											.getSession().getWorld()
											.getPosition(PositionType.MAYOR)
											.getCurrentHolder();

									if (mayor != -1)
										application.getClient()
												.getActionHandler()
												.arrangeImpeachmentVote(mayor,
														(param) -> {
															if ((Boolean) param == true) {
																SimpleTextDialog
																		.createAndShow(
																				stage,
																				skin,
																				Lang.get(
																						"screen.map.character_config.privilege.success"),
																				Lang.get(
																						"ui.generic.wip"));
															} else {
																SimpleTextDialog
																		.createAndShow(
																				stage,
																				skin,
																				Lang.get(
																						"ui.generic.error"),
																				Lang.get(
																						"ui.generic.rmi_error"));
															}
														});
								}
							});
							changeableCharacterMenuTable.add(kickButton);
						}
					}
				});

		Table characterMenuTabTable = new Table();
		characterMenuTabTable.add(characterMenuTab1Button).padRight(5);
		characterMenuTabTable.add(characterMenuTab2Button);

		characterMenuTable.add(characterMenuTabTable).padRight(30).padBottom(15)
				.center().top().row();
		characterMenuTable.add(changeableCharacterMenuTable).padBottom(15).top()
				.height(315).row();
		characterMenuTable.add(closeCharacterMenuButton).padRight(30).center()
				.top();

		characterMenuDialog.add(characterMenuTable).pad(30);

		// PLAYER ICON
		iconButton = new ImageButton(skin
				.getDrawable(PlayerIcon.values()[0].getShieldDrawableName()));
		iconButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						characterMenuDialog.show(stage);
						characterMenuDialog.setPosition(0, 103);
					}
				});
		Table iconTable = new Table();
		iconTable.setBackground(skin.getDrawable("icon_background"));
		iconTable.add(iconButton).padLeft(8).padRight(10).padBottom(1);

		// INFO STUFF
		Table infoTable = new Table();

		// Gold
		Table goldTable = new Table();
		goldTable.setBackground(skin.getDrawable("info_background"));
		goldTable.add(new Image(skin.getDrawable("icon_gold_coin"))).left()
				.top().padLeft(4).padBottom(2).padRight(4);
		goldTable.add(new Label("19 Gulden", skin, "dark")).padBottom(5)
				.expandX().left().top();

		// Name
		Table nameTable = new Table();
		nameTable.setBackground(skin.getDrawable("info_background"));
		nameTable.add(
				new Label("Freiherr  Franz  von  Woyzeck", skin, "big_dark"))
				.padBottom(2).expandX().left();

		// Notifications
		Table notificationTable = new Table();

		infoTable.add(goldTable).row();
		infoTable.add(nameTable).left().row();
		infoTable.add(notificationTable).left().padLeft(2).padTop(2).row();

		// DATE STUFF
		Table dateTimeTable = new Table();

		// Date
		Table dateTable = new Table();
		dateLabel = new Label("", skin, "dark");
		dateTable.setBackground(skin.getDrawable("date_background"));
		dateTable.add(dateLabel).padLeft(9).padBottom(5).expandX().left();

		// Clock
		Table clockTable = new Table();
		clockTable.setBackground(skin.getDrawable("clock_background"));

		dateTimeTable.add(dateTable).row();
		dateTimeTable.add(clockTable).right();

		// MISC
		Table miscTable = new Table();
		Table misc2Table = new Table();

		// Inventory
		ImageButton inventoryButton = new ImageButton(skin, "inventory");

		// Book
		ImageButton bookButton = new ImageButton(skin, "leaning_book");

		// Minimap
		Table minimapTable = new Table();
		minimapTable.setBackground(skin.getDrawable("minimap_test"));

		misc2Table.add(inventoryButton).padRight(2);
		misc2Table.add(bookButton).padTop(2).row();

		miscTable.add(misc2Table).right().row();
		miscTable.add(minimapTable);

		mainTable.setSkin(skin);
		mainTable.setFillParent(true);

		mainTable.add(iconTable).top().left();
		mainTable.add(infoTable).top().left();
		mainTable.add(dateTimeTable).right().expandX().row();
		mainTable.add("").fill();
		mainTable.add("").expand().fill();
		mainTable.add(miscTable).expandY().bottom().right();

		// PAUSE DIALOG
		pauseDialog = new BasicDialog("", skin) {
			@Override
			protected void result(Object object) {
				pauseShown = false;
				postProcessor.removeEffect(pausePostProcessingEffect);
				if (object == (Integer) 1) {
					application.getScreenManager().pushScreen("settings",
							"blendingTransition");
				} else {
					LOG.info("[CLIENT] Verbindung wird getrennt");
					final GameClient client = application.getClient();
					final GameServer server = application.getServer();
					// Set stuff to null to stop updates
					application.setClient(null);
					application.setServer(null);

					ThreadHandler.getInstance().executeRunnable(() -> {
						client.disconnect();

						if (server != null) {
							server.stop();
						}
					});

					DiscordGGHandler.getInstance().setMenuPresence();
					application.getScreenManager().pushScreen("main_menu",
							null);
				}
			};
		};
		pauseDialog.button(Lang.get("screen.map.pause.settings"), 1)
				.button(Lang.get("screen.map.pause.disconnect"), 2);

		houseSelectionDialog = new BasicDialog("", skin);
		houseSelectionDialog.button("Test");

		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ESCAPE) {
					if (pauseShown) {
						pauseDialog.hide();
						postProcessor.removeEffect(pausePostProcessingEffect);
					} else {
						pauseDialog.show(stage);
						postProcessor.addEffect(pausePostProcessingEffect);
					}

					pauseShown = !pauseShown;

					return true;
				}
				return false;
			}
		});
	}

	@Override
	protected void setUIValues() {
		GameSession session = application.getClient().getSession();
		World world = session.getWorld();
		Player player = application.getClient().getLocalPlayer();

		if (pushParams != null) { // Is a new game
			gameRenderer.resetCamera();
			selectionInputController.setWorld(world);
			gameSpeedInputProcessor.setClientActionHandler(
					application.getClient().getActionHandler());
			iconButton.getStyle().imageUp = skin
					.getDrawable(player.getIcon().getShieldDrawableName());
		}

		inputHandler.reset();
		selectionInputController.resetInput();
		movementInputController.resetInput();

		dateLabel.setText(Lang.get("screen.map.time",
				GameClock.getSeason(session.getCurrentRound()),
				GameClock.getYear(session.getCurrentRound())));
	}

	@Override
	public void renderGame(float delta) {
		movementInputController.update(delta);

		// Render city
		if (application.getClient() != null) {// Null while disconnecting
			boolean doPostProcessing = postProcessor.hasEffects();
			if (doPostProcessing) {
				postProcessor.beginCapture();
			}
			gameRenderer
					.render(application.getClient().getSession().getWorld());

			if (doPostProcessing) {
				postProcessor.endCapture();
				postProcessor.renderEffectsToScreen(delta);
			}

		}
	}

	@Subscribe
	public void onHouseSelectionEvent(HouseSelectionEvent ev) {
		LOG.debug("[INPUT] Building selected: %d", ev.getId());

		if (ev.getId() == -1) {
			houseSelectionDialog.hide();
			return;
		}

		houseSelectionDialog.show(stage);
		houseSelectionDialog.setModal(false);
		// houseSelectionDialog.setPosition(ev.getClickX(),
		// game.getViewportHeight() - ev.getClickY());
		houseSelectionDialog.setPosition(
				application.getWidth() - houseSelectionDialog.getWidth(),
				application.getHeight() - 230
						- houseSelectionDialog.getHeight());
	}

	@Subscribe
	public void onHouseEnterEvent(HouseEnterEvent ev) {
		application.getScreenManager().pushScreen("house_town_hall",
				"circle_crop", ev.getId());
	}

	@Subscribe
	public void onFullHour(FullHourEvent ev) {
		application.getSoundManager().playSoundEffect("clock_tick");
	}

	@Override
	public void dispose() {
		super.dispose();

		if (gameRenderer != null)
			gameRenderer.dispose();
	}

}
