package de.eskalon.gg.simulation.model.entities;

import de.eskalon.gg.simulation.model.types.ItemType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public final class ItemStack {

	private @Getter @Setter ItemType type;
	private @Getter @Setter int count;

}
