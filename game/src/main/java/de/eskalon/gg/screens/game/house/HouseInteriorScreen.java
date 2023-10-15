package de.eskalon.gg.screens.game.house;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.gg.input.BackInputProcessor;
import de.eskalon.gg.screens.game.AbstractGameScreen;
import de.eskalon.gg.screens.game.MapScreen;

public abstract class HouseInteriorScreen extends AbstractGameScreen {

	public HouseInteriorScreen(SpriteBatch batch,
			EskalonScreenManager screenManager) {
		super(batch);

		addInputProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				screenManager.pushScreen(MapScreen.class, "circle_crop");
			}
		});
	}

}
