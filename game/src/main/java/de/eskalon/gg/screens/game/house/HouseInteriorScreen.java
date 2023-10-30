package de.eskalon.gg.screens.game.house;

import de.eskalon.gg.input.BackInputProcessor;
import de.eskalon.gg.screens.game.AbstractGameScreen;
import de.eskalon.gg.screens.game.MapScreen;

public abstract class HouseInteriorScreen extends AbstractGameScreen {

	@Override
	public void show() {
		super.show();

		addInputProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				screenManager.pushScreen(MapScreen.class, "circle_crop");
			}
		});
	}

}
