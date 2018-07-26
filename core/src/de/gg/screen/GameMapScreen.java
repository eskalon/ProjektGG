package de.gg.screen;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.eventbus.Subscribe;

import de.gg.event.FullHourEvent;
import de.gg.event.HouseEnterEvent;
import de.gg.event.HouseSelectionEvent;
import de.gg.event.NewNotificationEvent;
import de.gg.game.GameClock;
import de.gg.game.type.PositionTypes;
import de.gg.game.world.City;
import de.gg.input.ButtonClickListener;
import de.gg.input.GameSpeedInputProcessor;
import de.gg.input.MapMovementInputController;
import de.gg.input.MapSelectionInputController;
import de.gg.network.GameClient;
import de.gg.network.GameServer;
import de.gg.render.SceneRenderer;
import de.gg.render.TestShader;
import de.gg.ui.AnimationlessDialog;
import de.gg.util.Log;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen is the main game screen and is rendered when the player is in the
 * city view.
 */
public class GameMapScreen extends BaseGameScreen {

	@Asset(Text.class)
	private static final String FRAGMENT_SHADER_PATH = "shaders/single_color.fragment.glsl";
	@Asset(Sound.class)
	private static final String CLOCK_TICK_SOUND = "audio/clock-tick.wav";

	private SceneRenderer sceneRenderer;

	private Renderable renderable;

	private Sound clockTickSound;

	private MapSelectionInputController selectionInputController;
	private MapMovementInputController movementInputController;

	/**
	 * The pause dialog.
	 */
	private AnimationlessDialog pauseDialog;
	private AnimationlessDialog houseSelectionDialog;
	private boolean pauseShown = false;

	// Sphere stuff (temp)
	private Shader shader;
	private RenderContext renderContext;

	@Override
	protected void onInit() {
		super.onInit();

		clockTickSound = assetManager.get(CLOCK_TICK_SOUND);

		Text t = game.getAssetManager().get(FRAGMENT_SHADER_PATH);
		sceneRenderer = new SceneRenderer(game.getGameCamera().getCamera(),
				game.getClient().getCity(), t.getString());

		// SPHERE (temp)
		ModelBuilder modelBuilder = new ModelBuilder();
		Model model = modelBuilder.createSphere(2f, 2f, 2f, 20, 20,
				new Material(),
				Usage.Position | Usage.Normal | Usage.TextureCoordinates);

		NodePart blockPart = model.nodes.get(0).parts.get(0);

		renderable = new Renderable();
		blockPart.setRenderable(renderable);
		renderable.environment = sceneRenderer.environment;
		renderable.worldTransform.idt();

		renderContext = new RenderContext(
				new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
		shader = new TestShader(game.getAssetManager());
		shader.init();

		// TODO city erst in initUI übergeben
		selectionInputController = new MapSelectionInputController(
				game.getEventBus(), game.getGameCamera().getCamera(),
				game.getClient().getCity());
		addInputProcessor(selectionInputController);
		movementInputController = new MapMovementInputController(
				game.getGameCamera(), game.getSettings());
		addInputProcessor(movementInputController);
		addInputProcessor(new GameSpeedInputProcessor(game.getSettings(),
				game.getClient().getActionHandler()));
	}

	@Override
	protected void initUI() {
		short playerCharId = game.getClient().getLocalPlayer()
				.getCurrentlyPlayedCharacterId();
		City city = game.getClient().getCity();

		// CHARACTER DIALOG
		AnimationlessDialog characterMenuDialog = new AnimationlessDialog(
				"Spielerkonfiguration", skin, "window");
		ImageTextButton closeCharacterMenuButton = new ImageTextButton(
				"Schließen", skin, "small");
		closeCharacterMenuButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						characterMenuDialog.hide();
					}
				});

		Table characterMenuTable = new Table();
		Table changeableCharacterMenuTable = new Table();
		ImageTextButton characterMenuTab1Button = new ImageTextButton(
				"Charakter", skin, "small");
		ImageTextButton characterMenuTab2Button = new ImageTextButton(
				"Privilegien", skin, "small");
		characterMenuTab1Button.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						// CHARACTER & FAMILY
						changeableCharacterMenuTable.clear();
						changeableCharacterMenuTable
								.add(new Label("Name: XYZ, etc.", skin, "dark"))
								.padBottom(50).row();
						changeableCharacterMenuTable
								.add(new Label("Familie", skin, "dark")).row();
						changeableCharacterMenuTable.add(new Label(
								"Lebenspartner suchen...", skin, "dark"));
					}
				});
		characterMenuTab2Button.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						// PRIVILEGES
						changeableCharacterMenuTable.clear();
						changeableCharacterMenuTable.add(
								new Label("Privilegien-Liste", skin, "dark"))
								.row();

						if (city.getCharacter(playerCharId)
								.getPosition() != null) {
							// TODO Amts-Privilegien

							// TODO Abwahlen
							ImageTextButton kickButton = new ImageTextButton(
									"[Test] Bürgermeister herauswerfen", skin,
									"small");
							kickButton.addListener(new ButtonClickListener(
									assetManager, game.getSettings()) {
								@Override
								protected void onClick() {
									short mayor = city
											.getPosition(PositionTypes.MAYOR)
											.getCurrentHolder();

									if (mayor != -1)
										game.getClient().getActionHandler()
												.arrangeImpeachmentVote(mayor);
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
		ImageButton iconButton = new ImageButton(skin, "icon_1");
		iconButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
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
		goldTable.add(new Image(skin.getDrawable("gold_coin"))).left().top()
				.padLeft(4).padBottom(2).padRight(4);
		goldTable.add(new Label("19 Gulden", skin, "dark")).padBottom(5)
				.expandX().left().top();

		// Name
		Table nameTable = new Table();
		nameTable.setBackground(skin.getDrawable("info_background"));
		nameTable.add(
				new Label("Freiherr  Franz  von  Woyzeck", skin, "big-dark"))
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
		dateTable.setBackground(skin.getDrawable("date_background"));
		dateTable.add(new Label(
				String.format("%s - %s a.d.",
						GameClock.getSeason(game.getClient().getGameRound()),
						GameClock.getYear(game.getClient().getGameRound())),
				skin, "dark")).padLeft(9).padBottom(5).expandX().left();

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
		pauseDialog = new AnimationlessDialog("", skin) {
			protected void result(Object object) {
				pauseShown = false;
				if (object == (Integer) 1) {
					((SettingsScreen) game.getScreen("settings"))
							.setCaller(GameMapScreen.this);
					game.pushScreen("settings");
				} else {
					// Zuerst null setzen, damit das Spiel aufhört zu updaten
					final GameClient client = game.getClient();
					game.setClient(null);
					final GameServer server = game.getServer();
					game.setServer(null);

					// Dann in einem Thread das mitunter langwierige
					// Disconnecten durchführen
					(new Thread(new Runnable() {
						@Override
						public void run() {
							client.disconnect();

							Log.info("Client", "Client beendet");

							if (server != null) {
								server.stop();
							}

							Log.info("Server", "Server beendet");
						}
					})).start();

					game.pushScreen("mainMenu");
				}
			};
		};
		pauseDialog.button("Settings", 1).button("Verbindung trennen", 2);

		houseSelectionDialog = new AnimationlessDialog("", skin);
		houseSelectionDialog.button("Test");
	}

	@Override
	public void renderGame(float delta) {
		movementInputController.update();
		selectionInputController.update();

		// Render city
		sceneRenderer.render();

		// Render sphere with shader (temp)
		renderContext.begin();
		shader.begin(game.getGameCamera().getCamera(), renderContext);
		shader.render(renderable);
		shader.end();
		renderContext.end();
	}

	@Subscribe
	public void onHouseSelectionEvent(HouseSelectionEvent ev) {
		Log.debug("Input", "Gebäude ausgewählt: %d", ev.getId());

		if (ev.getId() == -1) {
			houseSelectionDialog.hide();
			return;
		}

		houseSelectionDialog.show(stage);
		houseSelectionDialog.setModal(false);
		// houseSelectionDialog.setPosition(ev.getClickX(),
		// game.getViewportHeight() - ev.getClickY());
		houseSelectionDialog.setPosition(
				game.getViewportWidth() - houseSelectionDialog.getWidth(),
				game.getViewportHeight() - 230
						- houseSelectionDialog.getHeight());
	}

	@Subscribe
	public void onHouseEnterEvent(HouseEnterEvent ev) {
		((GameInHouseScreen) game.getScreen("house"))
				.setSelectedHouseId(ev.getId());
		game.pushScreen("house");
	}

	@Subscribe
	public void onFollHour(FullHourEvent ev) {
		clockTickSound.play(game.getSettings().getEffectVolume()
				* game.getSettings().getMasterVolume());
	}

	@Subscribe
	public void onNewNotification(NewNotificationEvent ev) {
		// TODO update notification ui; @see GameClient#getNotifications()
	}

	@Override
	public void show() {
		super.show();
		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ESCAPE) {
					if (pauseShown)
						pauseDialog.hide();
					else {
						pauseDialog.show(stage);
						// selectionInputController.resetSelection();
					}

					pauseShown = !pauseShown;

					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void dispose() {
		super.dispose();

		if (isLoaded())
			sceneRenderer.dispose();
	}

}
