package de.gg.screen;

import de.gg.render.TestShader;
import de.gg.util.Log;

/**
 * This screen takes care of loading the assets for all ingame screens.
 */
public class GameLoadingScreen extends BaseLoadingScreen {

	@Override
	protected void initAssets() {
		assetManager.load(TestShader.class);
		assetManager.load(game.getScreen("map"));
		assetManager.load(game.getScreen("house"));
		assetManager.load(game.getScreen("roundEnd"));
	}
	@Override
	protected synchronized void onFinishedLoading() {
		game.getScreen("map").finishLoading();
		game.getScreen("house").finishLoading();
		game.getScreen("roundEnd").finishLoading();

		Log.info("Client", "Spiel gestartet");
		game.pushScreen("roundEnd");
	}

}
