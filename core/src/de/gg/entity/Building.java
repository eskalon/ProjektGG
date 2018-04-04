package de.gg.entity;

import de.gg.render.RenderData;

public class Building {

	private int id;
	private RenderData renderData;

	public Building(int id, RenderData renderData) {
		super();
		this.id = id;
		this.renderData = renderData;
	}

	public int getId() {
		return id;
	}
	
	public RenderData getRenderData() {
		return renderData;
	}

}
