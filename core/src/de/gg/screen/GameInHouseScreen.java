package de.gg.screen;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;

import de.gg.game.entity.Building;
import de.gg.game.type.BuildingTypes;
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
		// TODO Auto-generated method stub
	}

	@Override
	protected void initUI() {
		Building b = game.getClient().getCity()
				.getBuildingSlots()[selectedHouseId].getBuilding();

		// TOWNHALL
		if (b.getType() == BuildingTypes.TOWN_HALL) {
			// TODO UI-Komponenten für Rathaus-Test hinzufügen (Heiraten nach
			// Verlobung, Bürger-Status kaufen)

			ImageTextButton applyButton = new ImageTextButton("Standesamt",
					skin, "small");

			ImageTextButton kickButton = new ImageTextButton(
					"[Test] Bürgermeister herauswerfen", skin, "small");
			kickButton.addListener(
					new ButtonClickListener(assetManager, game.getSettings()) {
						@Override
						protected void onClick() {
							game.getClient().getActionHandler()
									.arrangeImpeachmentVote((short) 1);
						}
					});

			mainTable.add(kickButton);
		}
	}

	@Override
	public void renderGame(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		super.show();
		game.getInputMultiplexer().addProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				game.pushScreen("map");
			}
		});
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
