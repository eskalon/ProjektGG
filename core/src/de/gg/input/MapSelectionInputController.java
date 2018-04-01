package de.gg.input;

import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.google.common.eventbus.EventBus;

import de.gg.entity.Building;
import de.gg.render.RenderData;
import de.gg.setting.GameSettings;

public class MapSelectionInputController implements DefaultInputProcessor {

	private GameSettings settings;
	private EventBus bus;
	private PerspectiveCamera camera;

	private int clickedObjectId = -1;
	private int selectedObjectID = -1;

	private Material selectionMaterial;
	private Material originalMaterial;
	private List<Building> selectableObjects;

	public MapSelectionInputController(GameSettings settings, EventBus bus,
			PerspectiveCamera camera, List<Building> selectableObjects) {
		this.settings = settings;
		this.bus = bus;
		this.camera = camera;
		this.selectableObjects = selectableObjects;

		selectionMaterial = new Material();
		selectionMaterial.set(ColorAttribute.createDiffuse(Color.ORANGE));
		originalMaterial = new Material();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		if (button == Input.Buttons.LEFT) {
			clickedObjectId = getObjectAtPositon(screenX, screenY);
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
		if (button == Input.Buttons.LEFT) {
			if (clickedObjectId >= 0) {
				if (clickedObjectId == getObjectAtPositon(screenX, screenY))
					if (selectedObjectID != clickedObjectId)
						onNewSelection(clickedObjectId);

				clickedObjectId = -1;
				return true;
			}
		}

		return false;
	}

	private void onNewSelection(int value) {
		// Altes Objekt reseten
		if (selectedObjectID >= 0) {
			Material mat = selectableObjects.get(selectedObjectID)
					.getRenderData().materials.get(0);
			mat.clear();
			mat.set(originalMaterial);
		}
		// Neues Objekt markieren
		selectedObjectID = value;
		if (selectedObjectID >= 0) {
			// TODO event bus benachrichtigen
			Material mat = selectableObjects.get(selectedObjectID)
					.getRenderData().materials.get(0);
			originalMaterial.clear();
			originalMaterial.set(mat);
			mat.clear();
			mat.set(selectionMaterial);
		}

	}

	private int getObjectAtPositon(int screenX, int screenY) {
		Ray ray = camera.getPickRay(screenX, screenY);
		int result = -1;
		float distance = -1;
		for (int i = 0; i < selectableObjects.size(); ++i) {
			final float dist2 = selectableObjects.get(i).getRenderData()
					.intersects(ray);
			if (dist2 >= 0f && (distance < 0f || dist2 <= distance)) {
				result = i;
				distance = dist2;
			}
		}
		return result;
	}

}
