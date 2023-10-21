package de.eskalon.gg.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.crashinvaders.vfx.effects.ChainVfxEffect;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.event.EventBus;
import de.eskalon.commons.event.Subscribe;
import de.eskalon.commons.graphics.PostProcessingPipeline;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.input.DefaultInputHandler;
import de.eskalon.commons.input.IInputHandler;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.events.ConnectionLostEvent;
import de.eskalon.gg.events.FullHourEvent;
import de.eskalon.gg.events.HouseEnterEvent;
import de.eskalon.gg.events.HouseSelectionEvent;
import de.eskalon.gg.graphics.rendering.CameraWrapper;
import de.eskalon.gg.graphics.ui.actors.dialogs.BasicDialog;
import de.eskalon.gg.input.ButtonClickListener;
import de.eskalon.gg.input.GameSpeedInputProcessor;
import de.eskalon.gg.input.MapMovementInputController;
import de.eskalon.gg.input.MapSelectionInputController;
import de.eskalon.gg.net.packets.data.VoteType;
import de.eskalon.gg.screens.MainMenuScreen;
import de.eskalon.gg.screens.SettingsScreen;
import de.eskalon.gg.screens.game.house.TownHallInteriorScreen;
import de.eskalon.gg.simulation.model.types.PositionType;

/**
 * This screen is the main game screen and is rendered when the player is in the
 * city view.
 */
public class MapScreen extends AbstractGameScreen {

	public enum GameMapAxisBinding {
		MOVE_LEFT_RIGHT, MOVE_FORWARDS_BACKWARDS, ZOOM;
	}

	public enum GameMapBinaryBinding {
		INCREASE_SPEED, DECREASE_SPEED, ROTATE_CAMERA_BUTTON, SELECT_BUILDING;
	}

	private static final Logger LOG = LoggerService.getLogger(MapScreen.class);

	private @Inject ISoundManager soundManager;
	private @Inject Skin skin;
	private @Inject EskalonSettings settings;
	private @Inject EventBus eventBus;

	private CameraWrapper camera;

	private IInputHandler<GameMapAxisBinding, GameMapBinaryBinding> inputHandler;

	private MapSelectionInputController selectionInputController;
	private MapMovementInputController movementInputController;
	private GameSpeedInputProcessor gameSpeedInputProcessor;

	/**
	 * The pause dialog.
	 */
	private BasicDialog pauseDialog;
	private BasicDialog houseSelectionDialog;
	private boolean pauseShown = false;

	private @Inject PostProcessingPipeline postProcessor;
	private ChainVfxEffect pausePostProcessingEffect;

	@Override
	public void show() {
		super.show();

		/*
		 * RENDERING
		 */
		camera = new CameraWrapper(appContext.getGameRenderer().getCamera());
		// camera.setPosition(Gdx.graphics.getWidth() / 2,
		// Gdx.graphics.getHeight() / 2, 0);

		pausePostProcessingEffect = new GaussianBlurEffect();

		/*
		 * INPUT
		 */
		inputHandler = new DefaultInputHandler<>(settings,
				GameMapAxisBinding.class, GameMapBinaryBinding.class);
		addInputProcessor((DefaultInputHandler) inputHandler);

		// Object selection
		selectionInputController = new MapSelectionInputController(
				appContext.getGameHandler().getSimulation().getWorld(),
				eventBus, camera.getCamera());
		inputHandler.addListener(selectionInputController);

		// Game speed
		gameSpeedInputProcessor = new GameSpeedInputProcessor(
				appContext.getGameHandler());
		inputHandler.addListener(gameSpeedInputProcessor);

		// Map movement
		movementInputController = new MapMovementInputController(camera,
				settings);
		inputHandler.addListener(movementInputController);

		/*
		 * UI
		 */
		// CHARACTER DIALOG
		BasicDialog characterMenuDialog = new BasicDialog(
				Lang.get("screen.map.character_config"), skin, "big");
		ImageTextButton closeCharacterMenuButton = new ImageTextButton(
				Lang.get("ui.generic.close"), skin);
		closeCharacterMenuButton
				.addListener(new ButtonClickListener(soundManager) {
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
		characterMenuTab1Button
				.addListener(new ButtonClickListener(soundManager) {
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
		characterMenuTab2Button
				.addListener(new ButtonClickListener(soundManager) {
					@Override
					protected void onClick() {
						// PRIVILEGES
						changeableCharacterMenuTable.clear();
						changeableCharacterMenuTable.add(new Label(Lang.get(
								"screen.map.character_config.privileges_list"),
								skin, "dark")).row();

						if (appContext.getGameHandler()
								.getLocalPlayerCharacter()
								.getPosition() != null) {
							ImageTextButton kickButton = new ImageTextButton(
									Lang.get(
											"screen.map.character_config.privilege.impeach"),
									skin);
							kickButton.addListener(
									new ButtonClickListener(soundManager) {
										@Override
										protected void onClick() {
											short mayor = appContext
													.getGameHandler()
													.getSimulation().getWorld()
													.getPosition(
															PositionType.MAYOR)
													.getCurrentHolder();

											if (mayor != -1) {
												// TODO check whether there
												// already is an impeachment
												// vote for this person
												appContext.getClient()
														.arrangeVote(
																VoteType.IMPEACHMENT,
																appContext
																		.getGameHandler()
																		.getLocalPlayer()
																		.getCurrentlyPlayedCharacterId(),
																mayor);
											}
										}
									});
							changeableCharacterMenuTable.add(kickButton);
						}

						// TODO: ELECTIONS
				// @formatter:off
//						if (pos.getCurrentHolder() == (short) -1
//								&& pos.getApplicants().size() < 4) {
//							// ...
//						}
						// @formatter:on	
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
		ImageButton iconButton = new ImageButton(
				skin.getDrawable(appContext.getGameHandler().getLocalPlayer()
						.getIcon().getShieldDrawableName()));
		iconButton.addListener(new ButtonClickListener(soundManager) {
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
		Label dateLabel = new Label("", skin, "dark");
		dateLabel.setText(Lang.get("screen.map.time",
				appContext.getGameHandler().getClock().getSeason(),
				appContext.getGameHandler().getClock().getYear()));
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
					screenManager.pushScreen(SettingsScreen.class,
							"blendingTransition");
				} else {
					LOG.info("[CLIENT] Disconnecting from the server");
					appContext.getClient().disconnect();

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

		if (appContext.getObjectStorage()
				.containsKey("game_has_just_started")) { // i.e., this is a new
															// game
			appContext.getObjectStorage().remove("game_has_just_started");
			// TODO show welcome message, tutorial hints, etc.
		}

		// selectionInputController.resetInput();
		// inputHandler.reset();
		// movementInputController.resetInput();
	}

	@Override
	public void renderGame(float delta) {
		movementInputController.update(delta);

		/* Render city */
		if (appContext.getClient() != null) { // Null while disconnecting
			postProcessor.beginCapture();
			appContext.getGameRenderer().render(
					appContext.getGameHandler().getSimulation().getWorld());

			postProcessor.endCapture();
			postProcessor.renderEffectsToScreen(delta);
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
				Gdx.graphics.getWidth() - houseSelectionDialog.getWidth(),
				Gdx.graphics.getHeight() - 230
						- houseSelectionDialog.getHeight());
	}

	@Subscribe
	public void onHouseEnterEvent(HouseEnterEvent ev) {
		// TODO switch screen depending on house
		screenManager.pushScreen(TownHallInteriorScreen.class, "circle_crop");
	}

	@Subscribe
	public void onFullHour(FullHourEvent ev) {
		soundManager.playSoundEffect("clock_tick");
	}

	@Subscribe
	@Override
	public void onConnectionLost(ConnectionLostEvent ev) {
		appContext.handleDisconnection();
		screenManager.pushScreen(MainMenuScreen.class);
	}

}
