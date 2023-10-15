package de.eskalon.gg.input;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.collision.Ray;

import de.eskalon.commons.event.EventBus;
import de.eskalon.commons.input.DefaultInputListener;
import de.eskalon.gg.events.HouseEnterEvent;
import de.eskalon.gg.events.HouseSelectionEvent;
import de.eskalon.gg.screens.game.MapScreen.GameMapAxisBinding;
import de.eskalon.gg.screens.game.MapScreen.GameMapBinaryBinding;
import de.eskalon.gg.simulation.model.World;

public class MapSelectionInputController implements
		DefaultInputListener<GameMapAxisBinding, GameMapBinaryBinding> {

	private static final long DOUBLE_CLICK_TIME = 300;

	private EventBus eventBus;
	private PerspectiveCamera camera;

	private boolean selectionTriggered = false;
	private short selectedObjectId = -1;
	private long lastClickTime = -1;

	private World world;

	public MapSelectionInputController(World world, EventBus bus,
			PerspectiveCamera camera) {
		this.world = world;
		this.eventBus = bus;
		this.camera = camera;
	}

	@Override
	public boolean on(GameMapBinaryBinding id) {
		if (id == GameMapBinaryBinding.SELECT_BUILDING) {
			selectionTriggered = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean moved(int screenX, int screenY) {
		if (selectionTriggered) {
			selectionTriggered = false;

			short newObjectId = getObjectAtPositon(screenX, screenY);

			if (selectedObjectId >= 0) { // there is already a building selected
				if (selectedObjectId == newObjectId) {
					if (System.currentTimeMillis()
							- lastClickTime <= DOUBLE_CLICK_TIME) {
						onDoubleSelection(selectedObjectId);
					} else {
						onSingleSelection(newObjectId); // select the
														// building
														// again
					}
				} else {
					onSingleSelection(newObjectId);
				}
			} else {
				onSingleSelection(newObjectId);
			}

		}
		return selectedObjectId >= 0;
	}

	private void onDoubleSelection(int value) {
		eventBus.post(new HouseEnterEvent((short) value));
	}

	private void onSingleSelection(short value) {
		lastClickTime = System.currentTimeMillis();

		// Reset old object
		if (selectedObjectId >= 0) {
			world.getBuildingSlots()[selectedObjectId].getBuilding()
					.getRenderData().isSelected = false;
		}
		// Mark new object
		selectedObjectId = value;
		if (selectedObjectId >= 0) {
			world.getBuildingSlots()[selectedObjectId].getBuilding()
					.getRenderData().isSelected = true;
		}

		eventBus.post(new HouseSelectionEvent(selectedObjectId));
	}

	public void resetInput() {
		onSingleSelection((short) -1);
	}

	private short getObjectAtPositon(int screenX, int screenY) {
		Ray ray = camera.getPickRay(screenX, screenY);
		short result = -1;
		float distance = -1;
		for (short i = 0; i < world.getBuildingSlots().length; ++i) {
			final float dist2 = world.getBuildingSlots()[i].getBuilding()
					.getRenderData().intersects(ray);
			if (dist2 >= 0f && (distance < 0f || dist2 <= distance)) {
				result = i;
				distance = dist2;
			}
		}
		return result;
	}

}
