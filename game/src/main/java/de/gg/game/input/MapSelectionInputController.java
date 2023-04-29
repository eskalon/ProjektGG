package de.gg.game.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.collision.Ray;
import com.google.common.eventbus.EventBus;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.event.EventQueueBus;
import de.gg.game.events.HouseEnterEvent;
import de.gg.game.events.HouseSelectionEvent;
import de.gg.game.model.World;

public class MapSelectionInputController implements DefaultInputProcessor {

	private int selectionButton = Buttons.LEFT;

	private EventBus eventBus;
	private PerspectiveCamera camera;

	private short clickedObjectId = -1;
	private short newSelectionId = -1;
	private short selectedObjectID = -1;

	private long lastClickTime = -1;
	private static final long DOUBLE_CLICK_TIME = 300;

	private World world;

	private int clickX, clickY;

	public MapSelectionInputController(EventBus bus,
			PerspectiveCamera camera) {
		this.eventBus = bus;
		this.camera = camera;
	}

	public void update() {
		if (newSelectionId >= 0) {
			if (System.currentTimeMillis()
					- lastClickTime > DOUBLE_CLICK_TIME) {
				// Einzelklick
				onSingleSelection(newSelectionId);

				newSelectionId = -1;
			}
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		if (button == selectionButton) {
			clickedObjectId = getObjectAtPositon(screenX, screenY);
			this.clickX = screenX;
			this.clickY = screenY;
			return clickedObjectId >= 0;
		} else {
			return false;
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return clickedObjectId >= 0;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == selectionButton) {
			if (clickedObjectId >= 0) { // Wenn auf Objekt geklickt
				if (clickedObjectId == getObjectAtPositon(screenX, screenY)) {
					if (System.currentTimeMillis()
							- lastClickTime <= DOUBLE_CLICK_TIME) {
						if (clickedObjectId == newSelectionId) {
							// Doppelklick
							onDoubleSelection(newSelectionId);
							newSelectionId = -1;
						}
					} else {
						newSelectionId = clickedObjectId;
						lastClickTime = System.currentTimeMillis();
					}
				}

				clickedObjectId = -1;
				return true;
			} else { // Wenn neben Objekt geklickt
				resetSelection(); // Altes Objekt deselektieren
			}
		}

		return false;
	}

	private void onDoubleSelection(int value) {
		eventBus.post(new HouseEnterEvent((short) value));
	}

	public void resetSelection() {
		onSingleSelection((short) -1);
	}

	private void onSingleSelection(short value) {
		// Altes Objekt reseten
		if (selectedObjectID >= 0) {
			world.getBuildingSlots()[selectedObjectID].getBuilding()
					.getRenderData().isSelected = false;
		}
		// Neues Objekt markieren
		selectedObjectID = value;
		if (selectedObjectID >= 0) {
			world.getBuildingSlots()[selectedObjectID].getBuilding()
					.getRenderData().isSelected = true;
		}

		eventBus.post(
				new HouseSelectionEvent(selectedObjectID, clickX, clickY));
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

	public void setWorld(World world) {
		this.world = world;
	}

}
