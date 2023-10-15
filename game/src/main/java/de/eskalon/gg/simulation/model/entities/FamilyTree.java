package de.eskalon.gg.simulation.model.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a family hierarchy.
 */
public final class FamilyTree {

	private short fatherCharacterId = -1, motherCharacterId = -1;
	private List<Short> childrenCharacterIds = new ArrayList<>();

	public FamilyTree() {
		// default public constructor
	}

	public short getFatherCharacterId() {
		return fatherCharacterId;
	}

	public void setFatherCharacterId(short fatherCharacterId) {
		this.fatherCharacterId = fatherCharacterId;
	}

	public short getMotherCharacterId() {
		return motherCharacterId;
	}

	public void setMotherCharacterId(short motherCharacterId) {
		this.motherCharacterId = motherCharacterId;
	}

	public List<Short> getChildrenCharacterIds() {
		return childrenCharacterIds;
	}

}