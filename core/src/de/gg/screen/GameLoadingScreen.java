package de.gg.screen;

import de.gg.entity.BuildingTypes;
import de.gg.entity.ItemTypes;
import de.gg.entity.PositionTypes;
import de.gg.entity.ProfessionTypes;
import de.gg.entity.SocialStatusS;
import de.gg.render.TestShader;
import de.gg.util.Log;

/**
 * This screen takes care of loading the assets for all ingame screens.
 */
public class GameLoadingScreen extends BaseLoadingScreen {

	public GameLoadingScreen() {
		super("ui/backgrounds/altar.jpg");
	}

	@Override
	protected void initAssets() {
		assetManager.load(TestShader.class);
		assetManager.load(BuildingTypes.class);
		assetManager.load(PositionTypes.class);
		assetManager.load(ProfessionTypes.class);
		assetManager.load(SocialStatusS.class);
		assetManager.load(ItemTypes.class);
		assetManager.load(ItemTypes.class);

		assetManager.load(game.getScreen("map"));
		assetManager.load(game.getScreen("house"));
		assetManager.load(game.getScreen("roundEnd"));
	}

	@Override
	protected synchronized void onFinishedLoading() {
		game.getScreen("map").finishLoading();
		game.getScreen("house").finishLoading();
		game.getScreen("roundEnd").finishLoading();
		BuildingTypes.finishLoading(assetManager);
		PositionTypes.finishLoading(assetManager);
		ProfessionTypes.finishLoading(assetManager);
		SocialStatusS.finishLoading(assetManager);
		ItemTypes.finishLoading(assetManager);
		ItemTypes.finishLoading(assetManager);

		Log.info("Client", "Spiel gestartet");
		game.pushScreen("roundEnd");
	}

}
