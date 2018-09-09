package de.gg.screens;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;

import de.gg.game.factories.CharacterFactory;
import de.gg.game.types.BuildingType;
import de.gg.game.types.GameMap;
import de.gg.game.types.LawType;
import de.gg.game.types.NPCCharacterTrait;
import de.gg.game.types.PlayerTaskType;
import de.gg.game.types.PositionType;
import de.gg.game.types.ProfessionType;
import de.gg.game.types.SocialStatus;
import de.gg.game.types.TypeRegistry;
import de.gg.lang.Lang;
import de.gg.ui.rendering.TestShader;
import de.gg.utils.PlayerUtils;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

/**
 * This screen takes care of loading the assets for all screens except the
 * ingame ones.
 */
public class LoadingScreen extends BaseLoadingScreen {

	public final AssetDescriptor<BitmapFont> MAIN_FONT_18_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/AlemdraSC/AlmendraSC-Regular.ttf";
		font.fontParameters.size = 18;
		return new AssetDescriptor<>("mainFont18.ttf", BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> MAIN_FONT_19_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/AlemdraSC/AlmendraSC-Regular.ttf";
		font.fontParameters.size = 19;
		return new AssetDescriptor<>("mainFont19.ttf", BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> MAIN_FONT_20_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/AlemdraSC/AlmendraSC-Regular.ttf";
		font.fontParameters.size = 20;
		return new AssetDescriptor<>("mainFont20.ttf", BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> MAIN_FONT_22_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/AlemdraSC/AlmendraSC-Regular.ttf";
		font.fontParameters.size = 22;
		return new AssetDescriptor<>("mainFont22.ttf", BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> TEXT_FONT_20_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/jim-nightshade/JimNightshade-Regular.ttf";
		font.fontParameters.size = 20;
		return new AssetDescriptor<>("textFont20.ttf", BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> TITLE_FONT_24_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/Fredericka_the_Great/FrederickatheGreat-Regular.ttf";
		font.fontParameters.size = 24;
		return new AssetDescriptor<>("titleFont24.ttf", BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> HANDWRITTEN_FONT_20_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/HomemadeApple/HomemadeApple-Regular.ttf";
		font.fontParameters.size = 20;
		return new AssetDescriptor<>("handwrittenFont20.ttf", BitmapFont.class,
				font);
	}

	private final String SKIN_PATH = "ui/skin/skin.json";
	private final String SKIN_TEXTURE_ATLAS_PATH = "ui/skin/skin.atlas";

	public LoadingScreen() {
		super("ui/backgrounds/baker.jpg");
	}

	@Override
	protected synchronized void initAssets(
			AnnotationAssetManager assetManager) {
		// Add assets to loading queue
		// Types
		for (GameMap t : GameMap.values()) {
			assetManager.load(t.getJSONAssetDescriptor());
		}
		for (BuildingType t : BuildingType.values()) {
			assetManager.load(t.getJSONAssetDescriptor());
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

		// Names, player presets
		assetManager.load(PlayerUtils.class);
		assetManager.load(CharacterFactory.class);

		// Shader
		assetManager.load(TestShader.class);

		// Language bundle
		assetManager.load(Lang.LANG_BUNDLE_PATH, I18NBundle.class);

		// Basic UI Stuff
		assetManager.load(BaseUIScreen.class);

		assetManager.load(MAIN_FONT_18_PATH());
		assetManager.load(MAIN_FONT_19_PATH());
		assetManager.load(MAIN_FONT_20_PATH());
		assetManager.load(MAIN_FONT_22_PATH());
		assetManager.load(TITLE_FONT_24_PATH());
		assetManager.load(TEXT_FONT_20_PATH());
		assetManager.load(HANDWRITTEN_FONT_20_PATH());

		// (Non-game) Screens
		if (!game.IN_DEV_ENV)
			assetManager.load(game.getScreen("credits"));
		assetManager.load(game.getScreen("mainMenu"));
		assetManager.load(game.getScreen("serverBrowser"));
		assetManager.load(game.getScreen("lobby"));
		assetManager.load(game.getScreen("lobbyCreation"));
	}

	@Override
	protected void onFinishedLoading(AnnotationAssetManager assetManager) {
		// Load the skin
		BitmapFont main18Font = assetManager.get(MAIN_FONT_18_PATH());
		BitmapFont main19Font = assetManager.get(MAIN_FONT_19_PATH());
		main19Font.getData().markupEnabled = true;
		BitmapFont main20Font = assetManager.get(MAIN_FONT_20_PATH());
		BitmapFont main22Font = assetManager.get(MAIN_FONT_22_PATH());
		BitmapFont text20Font = assetManager.get(TEXT_FONT_20_PATH());
		BitmapFont title24Font = assetManager.get(TITLE_FONT_24_PATH());
		BitmapFont handwritten20Font = assetManager
				.get(HANDWRITTEN_FONT_20_PATH());

		ObjectMap<String, Object> fontMap = new ObjectMap<>();
		fontMap.put("main-18", main18Font);
		fontMap.put("main-19", main19Font);
		fontMap.put("main-20", main20Font);
		fontMap.put("main-22", main22Font);
		fontMap.put("text-20", text20Font);
		fontMap.put("title-24", title24Font);
		fontMap.put("handwritten-20", handwritten20Font);
		assetManager.load(SKIN_PATH, Skin.class,
				new SkinLoader.SkinParameter(SKIN_TEXTURE_ATLAS_PATH, fontMap));
		assetManager.finishLoadingAsset(SKIN_PATH);

		game.setUISkin(assetManager.get(SKIN_PATH));
		game.getUISkin().get("with-background", LabelStyle.class).background
				.setLeftWidth(9);

		// Set the localization
		I18NBundle langBundle = assetManager.get(Lang.LANG_BUNDLE_PATH,
				I18NBundle.class);
		langBundle.setExceptionOnMissingKey(false);
		Lang.setBundle(langBundle);

		// Set the type data
		TypeRegistry.getInstance().initialize(assetManager);

		// Set name presets
		CharacterFactory.initialize(assetManager);

		// Notify loaded screens
		game.getScreen("mainMenu").finishLoading(assetManager);
		game.getScreen("serverBrowser").finishLoading(assetManager);
		game.getScreen("lobby").finishLoading(assetManager);
		game.getScreen("lobbyCreation").finishLoading(assetManager);

		game.pushScreen("mainMenu");
	}

}
