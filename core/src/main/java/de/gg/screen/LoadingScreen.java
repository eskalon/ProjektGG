package de.gg.screen;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;

import de.gg.game.factory.CharacterFactory;
import de.gg.game.type.BuildingTypes;
import de.gg.game.type.GameMaps;
import de.gg.game.type.ItemTypes;
import de.gg.game.type.LawTypes;
import de.gg.game.type.PositionTypes;
import de.gg.game.type.ProfessionTypes;
import de.gg.game.type.SocialStatusS;
import de.gg.input.ButtonClickListener;
import de.gg.render.TestShader;
import de.gg.util.PlayerUtils;
import de.gg.util.json.SaveGameParser;

/**
 * This screen takes care of loading the assets for all screens except the
 * ingame ones.
 */
public class LoadingScreen extends BaseLoadingScreen {

	public final AssetDescriptor<BitmapFont> MAIN_FONT_18_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/AlemdraSC/AlmendraSC-Regular.ttf";
		font.fontParameters.size = 18;
		return new AssetDescriptor<BitmapFont>("mainFont18.ttf",
				BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> MAIN_FONT_19_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/AlemdraSC/AlmendraSC-Regular.ttf";
		font.fontParameters.size = 19;
		return new AssetDescriptor<BitmapFont>("mainFont19.ttf",
				BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> MAIN_FONT_20_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/AlemdraSC/AlmendraSC-Regular.ttf";
		font.fontParameters.size = 20;
		return new AssetDescriptor<BitmapFont>("mainFont20.ttf",
				BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> MAIN_FONT_22_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/AlemdraSC/AlmendraSC-Regular.ttf";
		font.fontParameters.size = 22;
		return new AssetDescriptor<BitmapFont>("mainFont22.ttf",
				BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> TEXT_FONT_20_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/jim-nightshade/JimNightshade-Regular.ttf";
		font.fontParameters.size = 20;
		return new AssetDescriptor<BitmapFont>("textFont20.ttf",
				BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> TITLE_FONT_24_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/Fredericka_the_Great/FrederickatheGreat-Regular.ttf";
		font.fontParameters.size = 24;
		return new AssetDescriptor<BitmapFont>("titleFont24.ttf",
				BitmapFont.class, font);
	}

	public final AssetDescriptor<BitmapFont> HANDWRITTEN_FONT_20_PATH() {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = "fonts/HomemadeApple/HomemadeApple-Regular.ttf";
		font.fontParameters.size = 20;
		return new AssetDescriptor<BitmapFont>("handwrittenFont20.ttf",
				BitmapFont.class, font);
	}

	private final String SKIN_PATH = "ui/skin/skin.json";
	private final String SKIN_TEXTURE_ATLAS_PATH = "ui/skin/skin.atlas";

	public LoadingScreen() {
		super("ui/backgrounds/baker.jpg");
	}

	@Override
	protected synchronized void initAssets() {
		// Add assets to loading queue
		assetManager.load(PlayerUtils.class);
		assetManager.load(GameMaps.class);
		assetManager.load(ProfessionTypes.class);
		assetManager.load(CharacterFactory.class);
		assetManager.load(TestShader.class);
		assetManager.load(BuildingTypes.class);
		assetManager.load(PositionTypes.class);
		assetManager.load(LawTypes.class);
		assetManager.load(SocialStatusS.class);
		assetManager.load(ItemTypes.class);
		assetManager.load(ItemTypes.class);

		assetManager.load(ButtonClickListener.class);

		assetManager.load(MAIN_FONT_18_PATH());
		assetManager.load(MAIN_FONT_19_PATH());
		assetManager.load(MAIN_FONT_20_PATH());
		assetManager.load(MAIN_FONT_22_PATH());
		assetManager.load(TITLE_FONT_24_PATH());
		assetManager.load(TEXT_FONT_20_PATH());
		assetManager.load(HANDWRITTEN_FONT_20_PATH());

		if (!game.isInDevEnv())
			assetManager.load(game.getScreen("credits"));
		assetManager.load(game.getScreen("mainMenu"));
		assetManager.load(game.getScreen("serverBrowser"));
		assetManager.load(game.getScreen("lobby"));
		assetManager.load(game.getScreen("lobbyCreation"));
	}

	@Override
	protected void onFinishedLoading() {
		BitmapFont main18Font = assetManager.get(MAIN_FONT_18_PATH());
		BitmapFont main19Font = assetManager.get(MAIN_FONT_19_PATH());
		main19Font.getData().markupEnabled = true;
		BitmapFont main20Font = assetManager.get(MAIN_FONT_20_PATH());
		BitmapFont main22Font = assetManager.get(MAIN_FONT_22_PATH());
		BitmapFont text20Font = assetManager.get(TEXT_FONT_20_PATH());
		BitmapFont title24Font = assetManager.get(TITLE_FONT_24_PATH());
		BitmapFont handwritten20Font = assetManager
				.get(HANDWRITTEN_FONT_20_PATH());

		ObjectMap<String, Object> fontMap = new ObjectMap<String, Object>();
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

		// VisUI.load();
		// game.setUISkin(VisUI.getSkin());

		PlayerUtils.initialize(assetManager);
		GameMaps.initialize(assetManager);
		ProfessionTypes.initialize(assetManager);
		BuildingTypes.initialize(assetManager);
		PositionTypes.initialize(assetManager);
		LawTypes.initialize(assetManager);
		SocialStatusS.initialize(assetManager);
		ItemTypes.initialize(assetManager);
		ItemTypes.initialize(assetManager);
		CharacterFactory.initialize(assetManager);
		SaveGameParser.initialize();

		// Notify loaded screens
		game.getScreen("mainMenu").finishLoading();
		game.getScreen("serverBrowser").finishLoading();
		game.getScreen("lobby").finishLoading();
		game.getScreen("lobbyCreation").finishLoading();

		game.pushScreen("mainMenu");
	}

}
