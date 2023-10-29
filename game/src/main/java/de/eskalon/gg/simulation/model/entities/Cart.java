package de.eskalon.gg.simulation.model.entities;

import java.util.ArrayList;
import java.util.List;

import de.eskalon.gg.simulation.model.types.CartType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public final class Cart {

	private @Getter @Setter CartType type;
	private @Getter @Setter List<ItemStack> stacks = new ArrayList<>();
	private @Getter @Setter int hp;
	private @Getter @Setter short owner;

}
