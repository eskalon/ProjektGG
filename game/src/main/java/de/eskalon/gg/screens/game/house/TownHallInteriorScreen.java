package de.eskalon.gg.screens.game.house;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.input.ButtonClickListener;
import de.eskalon.gg.simulation.model.types.SocialStatus;

/**
 * This screen is rendered, when the player is inside of the town hall.
 */
public class TownHallInteriorScreen extends HouseInteriorScreen {

	@Inject
	public TownHallInteriorScreen(SpriteBatch batch,
			ProjektGGApplicationContext appContext, Skin skin,
			ISoundManager soundManager, EskalonScreenManager screenManager) {
		super(batch, screenManager);

		// Buy citizenship
		ImageTextButton applyForCitizenshipButton = new ImageTextButton(
				Lang.get("screen.house.town_hall.apply_for_citizenship"), skin);
		applyForCitizenshipButton
				.addListener(new ButtonClickListener(soundManager) {
					@Override
					protected void onClick() {
						// TODO inform server; after that disable the button;
						// then set a flag in the player to disable it
						// permanently (as there is no point in applying twice)
					}
				});
		applyForCitizenshipButton.setDisabled(!(appContext.getGameHandler()
				.getLocalPlayerCharacter()
				.getStatus() == SocialStatus.NON_CITIZEN
				&& appContext.getGameHandler().getLocalPlayer()
						.getFortune(appContext.getGameHandler().getSimulation()
								.getWorld()) >= SocialStatus.NON_CITIZEN
										.getFortuneRequirement()));
		mainTable.add(applyForCitizenshipButton).padBottom(7).row();

		// Apply for position
		ImageTextButton applyForPositionButton = new ImageTextButton(
				Lang.get("screen.house.town_hall.apply_for_position"), skin);
		applyForPositionButton
				.addListener(new ButtonClickListener(soundManager) {
					@Override
					protected void onClick() {
						// TODO show dialog with all available positions
					}
				});
		// TODO check whether positions are available
		// applyForPositionButton.setDisabled(false);
		mainTable.add(applyForPositionButton);
	}

	@Override
	public void renderGame(float delta) {
		// TODO render 3d background
	}

	@Override
	public Color getClearColor() {
		return Color.DARK_GRAY;
	}

}
