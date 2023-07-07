package de.gg.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.damios.guacamole.gdx.assets.Text;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.impl.GLTransitionsShaderTransition;
import de.eskalon.commons.screens.AbstractAssetLoadingScreen;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.model.factories.CharacterFactory;
import de.gg.game.model.types.BuildingType;
import de.gg.game.model.types.GameMap;
import de.gg.game.model.types.LawType;
import de.gg.game.model.types.NPCCharacterTrait;
import de.gg.game.model.types.PlayerTaskType;
import de.gg.game.model.types.PositionType;
import de.gg.game.model.types.ProfessionType;
import de.gg.game.model.types.SocialStatus;
import de.gg.game.model.types.TypeRegistry;
import de.gg.game.thirdparty.DiscordGGHandler;

/**
 * This screen takes care of loading the game's assets.
 */
public class AssetLoadingScreen extends AbstractAssetLoadingScreen {

	private Viewport viewport;

	// Assets of the loading screen itself
	private static final String BACKGROUND_PATH = "ui/backgrounds/loading_screen.jpg";
	static final String TITLE_PATH = "ui/title.png";
	private static final String BAR_TOP_PATH = "ui/loading_bar_top.png";
	private static final String BAR_BOTTOM_PATH = "ui/loading_bar_bottom.png";
	private Texture backgroundTexture;
	private Texture titleTexture;
	private Texture topBarTexture;
	private Texture bottomBarTexture;

	// Assets for localization
	@Asset("lang/lang")
	public I18NBundle langBundle;
	// Assets for the UI skin
	// UNUSED: "fonts/jim_nightshade/JimNightshade-Regular.ttf" (normal text
	// font)
	@Asset(value = "ui/skin/skin.json", params = "ui/skin/skin.atlas")
	public Skin skin;
	// Sounds
	@Asset("audio/button_click.mp3")
	protected Sound buttonClickSound;
	@Asset("audio/clock_tick.wav")
	private Sound clockTickSound;
	@Asset("audio/page_flip.mp3")
	private Sound flipSound;
	// Music
	@Asset("audio/music/playlist.json")
	private PlaylistDefinition defaultPlaylist;
	// Transitions
	@Asset("shaders/simple_zoom.trans")
	public Text simpleZoomText;
	@Asset("shaders/circle_crop.trans")
	public Text circleCropText;
	@Asset("shaders/circle_open.trans")
	public Text circleOpenText;
	// Cursor
	@Asset("ui/cursor.png")
	private Pixmap cursorPixmap;

	public AssetLoadingScreen(ProjektGGApplication application) {
		super(application, "de.gg.game");
		this.viewport = new ScreenViewport();
	}

	@Override
	protected void loadOwnAssets() {
		// don't use injection for the own assets so the skin, the lang bundle,
		// etc. can be loaded normally
		application.getAssetManager().load(BACKGROUND_PATH, Texture.class);
		application.getAssetManager().load(TITLE_PATH, Texture.class);
		application.getAssetManager().load(BAR_TOP_PATH, Texture.class);
		application.getAssetManager().load(BAR_BOTTOM_PATH, Texture.class);
		application.getAssetManager().finishLoading();
		backgroundTexture = application.getAssetManager().get(BACKGROUND_PATH);
		titleTexture = application.getAssetManager().get(TITLE_PATH);
		topBarTexture = application.getAssetManager().get(BAR_TOP_PATH);
		bottomBarTexture = application.getAssetManager().get(BAR_BOTTOM_PATH);
	}

	@Override
	protected void create() {
		super.create();
		// Load other stuff
		for (GameMap t : GameMap.values()) {
			application.getAssetManager().load(t.getJSONAssetDescriptor());
		}
		for (BuildingType t : BuildingType.values()) {
			application.getAssetManager().load(t.getJSONAssetDescriptor());
		}
		for (PositionType t : PositionType.values()) {
			application.getAssetManager().load(t.getJSONAssetDescriptor());
		}
		for (LawType t : LawType.values()) {
			application.getAssetManager().load(t.getJSONAssetDescriptor());
		}
		for (SocialStatus t : SocialStatus.values()) {
			application.getAssetManager().load(t.getJSONAssetDescriptor());
		}
		for (ProfessionType t : ProfessionType.values()) {
			application.getAssetManager().load(t.getJSONAssetDescriptor());
		}
		for (PlayerTaskType t : PlayerTaskType.values()) {
			application.getAssetManager().load(t.getJSONAssetDescriptor());
		}
		for (NPCCharacterTrait t : NPCCharacterTrait.values()) {
			application.getAssetManager().load(t.getJSONAssetDescriptor());
		}
	}

	@Override
	protected void onFinishedLoading() {
		application.getAssetManager().injectAssets(this);

		// Load the localization
		I18NBundle.setExceptionOnMissingKey(false);
		Lang.setBundle(langBundle);

		// Load the skin
		application.setUISkin(skin);
		application.getUISkin().get("ui-element-19", BitmapFont.class)
				.getData().markupEnabled = true;
		application.getUISkin().get("ui-text-20", BitmapFont.class)
				.getData().markupEnabled = true;

		// Set some data
		TypeRegistry.getInstance().initialize(application.getAssetManager());
		CharacterFactory.initialize(application.getAssetManager());

		// Transitions
		GLTransitionsShaderTransition simpleZoomShader = new GLTransitionsShaderTransition(
				0.9F, Interpolation.smooth);
		simpleZoomShader.compileGLTransition(simpleZoomText.getString());
		application.getScreenManager().addScreenTransition("simple_zoom",
				simpleZoomShader);

		GLTransitionsShaderTransition circleCropShader = new GLTransitionsShaderTransition(
				0.8F, Interpolation.linear);
		circleCropShader.compileGLTransition(circleCropText.getString());
		application.getScreenManager().addScreenTransition("circle_crop",
				circleCropShader);

		GLTransitionsShaderTransition circleOpenShader = new GLTransitionsShaderTransition(
				0.5F, Interpolation.linear);
		circleOpenShader.compileGLTransition(circleOpenText.getString());
		application.getScreenManager().addScreenTransition("circle_open",
				circleOpenShader);

		// Inject assets in screens & initialize them
		for (AbstractEskalonScreen s : application.getScreenManager()
				.getScreens()) {
			if (s != this) {// exclude loading screen
				application.getAssetManager().injectAssets(s);
				s.initializeScreen();
			}
		}

		for (ScreenTransition t : application.getScreenManager()
				.getScreenTransitions()) {
			application.getAssetManager().injectAssets(t);
			t.initializeScreenTransition();
		}

		// Sounds
		application.getSoundManager().addSoundEffect(buttonClickSound,
				"button_click");
		application.getSoundManager().addSoundEffect(clockTickSound,
				"clock_tick");
		application.getSoundManager().addSoundEffect(flipSound, "page_flip");

		// Music
		PlaylistDefinition.addPlaylistDefinitionToSoundManager(defaultPlaylist,
				application.getSoundManager(), application.getAssetManager());
		application.getSoundManager().playMusic("default");

		// Cursor
		Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursorPixmap, 0, 0));

		// Discord integration
		DiscordGGHandler.getInstance().setMenuPresence();

		// Change screen
		application.getScreenManager().pushScreen("main_menu",
				"longBlendingTransition");
	}

	@Override
	public void render(float delta, float progress) {
		viewport.apply();
		application.getSpriteBatch()
				.setProjectionMatrix(viewport.getCamera().combined);
		application.getSpriteBatch().begin();

		// Draw the background
		application.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
				application.getWidth(), application.getHeight());

		application.getSpriteBatch().draw(titleTexture,
				(application.getWidth() / 2) - (titleTexture.getWidth() / 2)
						- 18,
				187, titleTexture.getWidth(), titleTexture.getHeight());

		// Get useful values
		float imageWidth = topBarTexture.getWidth();
		float imageHeight = topBarTexture.getHeight();

		// The actual drawing
		application.getSpriteBatch().draw(bottomBarTexture,
				(application.getWidth() / 2) - (imageWidth / 2),
				(application.getHeight() / 4) - imageHeight / 2 + 95);
		application.getSpriteBatch().draw(topBarTexture,
				(application.getWidth() / 2) - (imageWidth / 2),
				(application.getHeight() / 4) - imageHeight / 2 + 95, 0, 0,
				Math.round(imageWidth * progress), (int) imageHeight);

		application.getSpriteBatch().end();
	}

	@Override
	public void dispose() {
		// nothing to dispose that isn't disposed elsewhere
	}

}
