package de.gg.game.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;

import de.eskalon.commons.lang.Lang;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.input.BackInputProcessor;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.model.World;
import de.gg.game.model.types.SocialStatus;

/**
 * This screen is rendered, when the player is inside of the town hall.
 */
public class GameTownHallInteriorScreen extends AbstractGameScreen {

	private ImageTextButton applyForCitizenshipButton, applyForPositionButton;

	public GameTownHallInteriorScreen(ProjektGGApplication application) {
		super(application);
	}

	@Override
	protected void create() {
		super.create();
		addInputProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				application.getScreenManager().pushScreen("map", null);
			}
		});

		// Buy citizenship
		applyForCitizenshipButton = new ImageTextButton(
				Lang.get("screen.house.town_hall.apply_for_citizenship"), skin);
		applyForCitizenshipButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						// TODO inform server; after that disable the button;
						// then set a flag in the player to disable it
						// permanently (as there is no point in applying twice)
					}
				});

		mainTable.add(applyForCitizenshipButton).padBottom(7).row();

		// Apply for position
		applyForPositionButton = new ImageTextButton(
				Lang.get("screen.house.town_hall.apply_for_position"), skin);
		applyForPositionButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						// TODO show dialog with all available positions
					}
				});
		mainTable.add(applyForPositionButton);
	}

	@Override
	protected void setUIValues() {
		World world = application.getClient().getSession().getWorld();

		// Building b =
		// world.getBuildingSlots()[(short)pushParams[0]].getBuilding();

		short playerCharId = application.getClient().getLocalPlayer()
				.getCurrentlyPlayedCharacterId();

		// Buy citizenship
		if (world.getCharacter(playerCharId)
				.getStatus() == SocialStatus.NON_CITIZEN
				&& application.getClient().getLocalPlayer()
						.getFortune(world) >= SocialStatus.NON_CITIZEN
								.getFortuneRequirement()) {
			applyForCitizenshipButton.setDisabled(false);

		} else {
			applyForCitizenshipButton.setDisabled(true);
		}

		// Apply for position
		// TODO check whether positions aer available

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
