package de.gg.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class City {

	private List<Building> buildings;
	private ModelInstance skyBox;

	public City() {
		this.buildings = new ArrayList<>();
	}

	public void setSkybox(ModelInstance skyBox) {
		this.skyBox = skyBox;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public ModelInstance getSkybox() {
		return skyBox;
	}

}
