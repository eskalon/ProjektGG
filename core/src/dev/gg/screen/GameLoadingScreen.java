package dev.gg.screen;

/**
 * This screen takes care of loading the assets for all ingame screens.
 */
public class GameLoadingScreen extends BaseLoadingScreen {

	@Override
	protected void initAssets() {
		assetManager.load(game.getScreen("map"));
		assetManager.load(game.getScreen("house"));
		assetManager.load(game.getScreen("roundEnd"));
	}
	@Override
	protected void onFinishedLoading() {
		game.getScreen("map").finishLoading();
		game.getScreen("house").finishLoading();
		game.getScreen("roundEnd").finishLoading();

		game.pushScreen("map");
	}

}
