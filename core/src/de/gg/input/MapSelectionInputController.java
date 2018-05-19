package de.gg.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.collision.Ray;
import com.google.common.eventbus.EventBus;

import de.gg.event.HouseEnterEvent;
import de.gg.event.HouseSelectionEvent;
import de.gg.game.world.City;
import de.gg.setting.GameSettings;

public class MapSelectionInputController implements DefaultInputProcessor {

	private int SELECTION_BUTTON = Buttons.LEFT;

	private GameSettings settings;
	private EventBus eventBus;
	private PerspectiveCamera camera;

	private short clickedObjectId = -1;
	private short newSelectionId = -1;
	private short selectedObjectID = -1;

	private long lastClickTime = -1;
	private static final long DOUBLE_CLICK_TIME = 300;

	private City city;

	private int clickX, clickY;

	public MapSelectionInputController(GameSettings settings, EventBus bus,
			PerspectiveCamera camera, City city) {
		this.settings = settings;
		this.eventBus = bus;
		this.camera = camera;
		this.city = city;
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
		if (button == SELECTION_BUTTON) {
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
		if (button == SELECTION_BUTTON) {
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
			city.getBuildingSlots()[selectedObjectID].getBuilding()
					.getRenderData().isSelected = false;
		}
		// Neues Objekt markieren
		selectedObjectID = value;
		if (selectedObjectID >= 0) {
			city.getBuildingSlots()[selectedObjectID].getBuilding()
					.getRenderData().isSelected = true;
		}

		eventBus.post(new HouseSelectionEvent((short) selectedObjectID, clickX,
				clickY));
	}

	private short getObjectAtPositon(int screenX, int screenY) {
		Ray ray = camera.getPickRay(screenX, screenY);
		short result = -1;
		float distance = -1;
		for (short i = 0; i < city.getBuildingSlots().length; ++i) {
			final float dist2 = city.getBuildingSlots()[i].getBuilding()
					.getRenderData().intersects(ray);
			if (dist2 >= 0f && (distance < 0f || dist2 <= distance)) {
				result = i;
				distance = dist2;
			}
		}
		return result;
	}

}
