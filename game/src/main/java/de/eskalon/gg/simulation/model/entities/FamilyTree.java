package de.eskalon.gg.simulation.model.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a family hierarchy.
 */
@NoArgsConstructor
public final class FamilyTree {

	private @Getter @Setter short fatherCharacterId = -1,
			motherCharacterId = -1;
	private @Getter List<Short> childrenCharacterIds = new ArrayList<>();

}