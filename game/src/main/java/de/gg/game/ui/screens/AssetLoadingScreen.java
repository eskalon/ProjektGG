package de.gg.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.asset.Text;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.impl.GLTransitionsShaderTransition;
import de.eskalon.commons.screens.AbstractAssetLoadingScreen;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.misc.DiscordGGHandler;
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

/**
 * This screen takes care of loading the game's assets.
 */
public class AssetLoadingScreen extends AbstractAssetLoadingScreen {

	// Assets of the loading screen itself
	private static final String BACKGROUND_PATH = "ui/backgrounds/loading_screen.jpg";
	private static final String TITLE_PATH = "ui/images/title.png";
	private static final String BAR_TOP_PATH = "ui/images/loading_bar_top.png";
	private static final String BAR_BOTTOM_PATH = "ui/images/loading_bar_bottom.png";
	private Texture backgroundTexture;
	private Texture titleTexture;
	private Texture topBarTexture;
	private Texture bottomBarTexture;

	// Assets for localization
	@Asset("lang/lang")
	public I18NBundle langBundle;
	// Assets for the UI skin
	@Asset(value = "fonts/alemdra_sc/AlmendraSC-Regular.ttf", params = "18")
	private BitmapFont mainFont18;
	@Asset(value = "fonts/alemdra_sc/AlmendraSC-Regular.ttf", params = "19")
	private BitmapFont mainFont19;
	@Asset(value = "fonts/alemdra_sc/AlmendraSC-Regular.ttf", params = "20")
	private BitmapFont mainFont20;
	@Asset(value = "fonts/alemdra_sc/AlmendraSC-Regular.ttf", params = "22")
	private BitmapFont mainFont22;
	@Asset(value = "fonts/jim_nightshade/JimNightshade-Regular.ttf", params = "20")
	private BitmapFont textFont20;
	@Asset(value = "fonts/fredericka_the_great/FrederickatheGreat-Regular.ttf", params = "24")
	private BitmapFont titleFont24;
	@Asset(value = "fonts/homemade_apple/HomemadeApple-Regular.ttf", params = "20")
	private BitmapFont handwrittenFont20;
	private static final String SKIN_PATH = "ui/skin/skin.json";
	private static final String SKIN_TEXTURE_ATLAS_PATH = "ui/skin/skin.atlas";
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
		ObjectMap<String, Object> fontMap = new ObjectMap<>();
		fontMap.put("main-18", mainFont18);
		fontMap.put("main-19", mainFont19);
		fontMap.put("main-20", mainFont20);
		fontMap.put("main-22", mainFont22);
		fontMap.put("text-20", textFont20);
		fontMap.put("title-24", titleFont24);
		fontMap.put("handwritten-20", handwrittenFont20);

		application.getAssetManager().load(SKIN_PATH, Skin.class,
				new SkinLoader.SkinParameter(SKIN_TEXTURE_ATLAS_PATH, fontMap));
		application.getAssetManager().finishLoadingAsset(SKIN_PATH);

		((ProjektGGApplication) application)
				.setUISkin(application.getAssetManager().get(SKIN_PATH));
		((ProjektGGApplication) application).getUISkin().get("with-background",
				LabelStyle.class).background.setLeftWidth(9);

		// Set the type data
		TypeRegistry.getInstance().initialize(application.getAssetManager());

		// Misc stuff
		CharacterFactory.initialize(application.getAssetManager());

		// Transitions
		GLTransitionsShaderTransition simpleZoomShader = new GLTransitionsShaderTransition(
				application.getUICamera(), 0.8F, Interpolation.linear);
		simpleZoomShader.compileGLTransition(simpleZoomText.getString());
		application.getScreenManager().addScreenTransition("simple_zoom",
				simpleZoomShader);

		GLTransitionsShaderTransition circleCropShader = new GLTransitionsShaderTransition(
				application.getUICamera(), 0.8F, Interpolation.linear);
		circleCropShader.compileGLTransition(circleCropText.getString());
		application.getScreenManager().addScreenTransition("circle_crop",
				circleCropShader);

		GLTransitionsShaderTransition circleOpenShader = new GLTransitionsShaderTransition(
				application.getUICamera(), 0.7F, Interpolation.linear);
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
				"assetLoadingTransition");
	}

	@Override
	public void render(float delta, float progress) {
		application.getSpriteBatch().begin();
		application.getSpriteBatch()
				.setProjectionMatrix(application.getUICamera().combined);

		// Draw the background
		application.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
				application.getWidth(), application.getHeight());

		application.getSpriteBatch().draw(titleTexture,
				(application.getWidth() / 2)
						- (titleTexture.getWidth() / 2 * 1.35F) + 1,
				150, titleTexture.getWidth() * 1.35F,
				titleTexture.getHeight() * 1.35F);

		// Get useful values
		float imageWidth = topBarTexture.getWidth();
		float imageHeight = topBarTexture.getHeight();

		// The actual drawing
		application.getSpriteBatch().draw(bottomBarTexture,
				(application.getWidth() / 2) - (imageWidth / 2) + 1,
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
