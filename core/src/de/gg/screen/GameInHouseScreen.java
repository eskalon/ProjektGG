package de.gg.screen;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;

import de.gg.game.entity.Building;
import de.gg.game.type.BuildingTypes;
import de.gg.game.type.SocialStatusS;
import de.gg.game.world.City;
import de.gg.input.BackInputProcessor;
import de.gg.input.ButtonClickListener;

/**
 * This screen is rendered, when the player is inside of a house.
 */
public class GameInHouseScreen extends BaseGameScreen {

	private short selectedHouseId;

	@Override
	protected void onInit() {
		super.onInit();

		addInputProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				game.pushScreen("map");
			}
		});
	}

	@Override
	protected void initUI() {
		short playerCharId = game.getClient().getLocalPlayer()
				.getCurrentlyPlayedCharacterId();
		City city = game.getClient().getCity();
		Building b = city.getBuildingSlots()[selectedHouseId].getBuilding();

		if (b.getType() == BuildingTypes.TOWN_HALL) {
			// TOWNHALL
			// Buy citizenship
			if (city.getCharacter(playerCharId)
					.getStatus() == SocialStatusS.NON_CITIZEN) {
				if (game.getClient().getLocalPlayer()
						.getFortune(city) >= SocialStatusS.NON_CITIZEN
								.getFortuneRequirement()) {
					ImageTextButton applyForCitizenshipButton = new ImageTextButton(
							"Bürgerrecht erwerben", skin, "small");
					applyForCitizenshipButton
							.addListener(new ButtonClickListener(assetManager,
									game.getSettings()) {
								@Override
								protected void onClick() {
									// TODO Server informieren!
									// TODO Bei Server-Antwort (?): Popup
								}
							});

					mainTable.add(applyForCitizenshipButton);
				}
			}
		} else if (b.getType().isProductionBuilding()) {
			// PRODUCTION BUILDING
			// TODO
		}
	}

	@Override
	public void renderGame(float delta) {
		// TODO Auto-generated method stub

	}

	public void setSelectedHouseId(short selectedHouseId) {
		this.selectedHouseId = selectedHouseId;
	}

	@Override
	public void dispose() {
		super.dispose();

		// if (isLoaded())
		// dispose loaded stuff
	}

}
