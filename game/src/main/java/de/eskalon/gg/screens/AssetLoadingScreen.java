package de.eskalon.gg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.damios.guacamole.gdx.assets.Text;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.screen.transition.impl.GLTransitionsShaderTransition;
import de.eskalon.commons.screens.AbstractAssetLoadingScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.simulation.model.factories.CharacterFactory;
import de.eskalon.gg.simulation.model.types.BuildingType;
import de.eskalon.gg.simulation.model.types.GameMap;
import de.eskalon.gg.simulation.model.types.LawType;
import de.eskalon.gg.simulation.model.types.NPCCharacterTrait;
import de.eskalon.gg.simulation.model.types.PlayerTaskType;
import de.eskalon.gg.simulation.model.types.PositionType;
import de.eskalon.gg.simulation.model.types.ProfessionType;
import de.eskalon.gg.simulation.model.types.SocialStatus;
import de.eskalon.gg.simulation.model.types.TypeRegistry;
import de.eskalon.gg.thirdparty.DiscordGGHandler;

/**
 * This screen takes care of loading the game's assets.
 */
public class AssetLoadingScreen extends AbstractAssetLoadingScreen {

	private @Inject EskalonScreenManager screenManager;
	private @Inject ProjektGGApplicationContext appContext;
	private @Inject ISoundManager soundManager;
	private @Inject SpriteBatch spriteBatch;

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

	@Inject
	public AssetLoadingScreen(AnnotationAssetManager assetManager) {
		super(assetManager, "de.gg.game");
		this.viewport = new ScreenViewport();
	}

	@Override
	protected void loadOwnAssets() {
		// Don't use injection for the screen's own assets, because they have to
		// be loaded first
		assetManager.load(BACKGROUND_PATH, Texture.class);
		assetManager.load(TITLE_PATH, Texture.class);
		assetManager.load(BAR_TOP_PATH, Texture.class);
		assetManager.load(BAR_BOTTOM_PATH, Texture.class);
		assetManager.finishLoading();
		backgroundTexture = assetManager.get(BACKGROUND_PATH);
		titleTexture = assetManager.get(TITLE_PATH);
		topBarTexture = assetManager.get(BAR_TOP_PATH);
		bottomBarTexture = assetManager.get(BAR_BOTTOM_PATH);
	}

	@Override
	public void show() {
		// Add other stuff to loading queue
		for (GameMap t : GameMap.values()) {
			assetManager.load(t.getJSONAssetDescriptor());
			assetManager.load(t.getSkyboxPath(), Model.class);
		}
		for (BuildingType t : BuildingType.values()) {
			assetManager.load(t.getJSONAssetDescriptor());
			assetManager.load(t.getModelPath(), Model.class);
		}
		for (PositionType t : PositionType.values()) {
			assetManager.load(t.getJSONAssetDescriptor());
		}
		for (LawType t : LawType.values()) {
			assetManager.load(t.getJSONAssetDescriptor());
		}
		for (SocialStatus t : SocialStatus.values()) {
			assetManager.load(t.getJSONAssetDescriptor());
		}
		for (ProfessionType t : ProfessionType.values()) {
			assetManager.load(t.getJSONAssetDescriptor());
		}
		for (PlayerTaskType t : PlayerTaskType.values()) {
			assetManager.load(t.getJSONAssetDescriptor());
		}
		for (NPCCharacterTrait t : NPCCharacterTrait.values()) {
			assetManager.load(t.getJSONAssetDescriptor());
		}
	}

	@Override
	protected void onFinishedLoading() {
		assetManager.injectAssets(this);

		// Load the localization
		I18NBundle.setExceptionOnMissingKey(false);
		Lang.setBundle(langBundle);

		// Load the skin
		EskalonInjector.instance().bindToInstance(Skin.class, skin);
		skin.get("ui-element-19", BitmapFont.class)
				.getData().markupEnabled = true;
		skin.get("ui-text-20", BitmapFont.class).getData().markupEnabled = true;

		// Set some data
		TypeRegistry.instance().initialize(assetManager);
		CharacterFactory.initialize(assetManager);

		// Transitions
		GLTransitionsShaderTransition simpleZoomShader = new GLTransitionsShaderTransition(
				simpleZoomText.getString(), 0.9F, Interpolation.smooth);
		appContext.getTransitions().put("simple_zoom", simpleZoomShader);

		GLTransitionsShaderTransition circleCropShader = new GLTransitionsShaderTransition(
				circleCropText.getString(), 0.8F, Interpolation.linear);
		appContext.getTransitions().put("circle_crop", circleCropShader);

		GLTransitionsShaderTransition circleOpenShader = new GLTransitionsShaderTransition(
				circleOpenText.getString(), 0.5F, Interpolation.linear);
		appContext.getTransitions().put("circle_open", circleOpenShader);

		// Sounds
		soundManager.addSoundEffect(buttonClickSound, "button_click");
		soundManager.addSoundEffect(clockTickSound, "clock_tick");
		soundManager.addSoundEffect(flipSound, "page_flip");

		// Music
		PlaylistDefinition.addPlaylistDefinitionToSoundManager(defaultPlaylist,
				soundManager, assetManager);
		soundManager.playMusic("default");

		// Cursor
		Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursorPixmap, 0, 0));

		// Discord integration
		DiscordGGHandler.instance().setMenuPresence();

		// Change screen
		screenManager.pushScreen(MainMenuScreen.class,
				"longBlendingTransition");
	}

	@Override
	public void render(float delta, float progress) {
		viewport.apply();
		spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
		spriteBatch.begin();

		// Draw the background
		spriteBatch.draw(this.backgroundTexture, 0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		spriteBatch.draw(titleTexture,
				(Gdx.graphics.getWidth() / 2) - (titleTexture.getWidth() / 2)
						- 18,
				187, titleTexture.getWidth(), titleTexture.getHeight());

		// Tmp vars
		float imageWidth = topBarTexture.getWidth();
		float imageHeight = topBarTexture.getHeight();

		// The actual drawing
		spriteBatch.draw(bottomBarTexture,
				(Gdx.graphics.getWidth() / 2) - (imageWidth / 2),
				(Gdx.graphics.getHeight() / 4) - imageHeight / 2 + 95);
		spriteBatch.draw(topBarTexture,
				(Gdx.graphics.getWidth() / 2) - (imageWidth / 2),
				(Gdx.graphics.getHeight() / 4) - imageHeight / 2 + 95, 0, 0,
				Math.round(imageWidth * progress), (int) imageHeight);

		spriteBatch.end();
	}

	@Override
	public void dispose() {
		// nothing to dispose that isn't disposed elsewhere
	}

}
