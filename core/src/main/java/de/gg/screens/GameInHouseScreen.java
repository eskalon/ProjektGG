package de.gg.screens;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;

import de.gg.game.entities.Building;
import de.gg.game.types.BuildingType;
import de.gg.game.types.SocialStatus;
import de.gg.game.world.World;
import de.gg.input.BackInputProcessor;
import de.gg.input.ButtonClickListener;
import de.gg.lang.Lang;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

/**
 * This screen is rendered, when the player is inside of a house.
 */
public class GameInHouseScreen extends BaseGameScreen {

	private short selectedHouseId;

	@Override
	protected void onInit(AnnotationAssetManager assetManager) {
		super.onInit(assetManager);

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
		World world = game.getClient().getWorld();
		Building b = world.getBuildingSlots()[selectedHouseId].getBuilding();

		if (b.getType() == BuildingType.TOWN_HALL) {
			// TOWNHALL
			// Buy citizenship
			if (world.getCharacter(playerCharId)
					.getStatus() == SocialStatus.NON_CITIZEN) {
				if (game.getClient().getLocalPlayer()
						.getFortune(world) >= SocialStatus.NON_CITIZEN.getData()
								.getFortuneRequirement()) {
					ImageTextButton applyForCitizenshipButton = new ImageTextButton(
							Lang.get(
									"screen.house.town_hall.apply_for_citizenship"),
							skin, "small");
					applyForCitizenshipButton.addListener(
							new ButtonClickListener(buttonClickSound,
									game.getSettings()) {
								@Override
								protected void onClick() {
									// TODO Server informieren!
									// TODO anschließend Button deaktivieren;
									// außerdem flag in Player setzen und über
									// dieses den Button dann später nicht
									// anzeigen lassen
								}
							});

					mainTable.add(applyForCitizenshipButton);
				}
			} else {
				// Apply for position
				ImageTextButton applyForPositionButton = new ImageTextButton(
						Lang.get("screen.house.town_hall.apply_for_position"),
						skin, "small");
				applyForPositionButton.addListener(new ButtonClickListener(
						buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						// TODO Dialog mit allen verfügbaren Positionen anzeigen
					}
				});
				mainTable.add(applyForPositionButton);
			}
		} else if (b.getType().getData().isProductionBuilding()) {
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
