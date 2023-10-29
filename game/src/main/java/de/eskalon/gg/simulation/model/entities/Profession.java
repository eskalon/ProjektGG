package de.eskalon.gg.simulation.model.entities;

import de.eskalon.commons.lang.ILocalizable;
import de.eskalon.gg.simulation.model.types.PlayerTaskType;
import de.eskalon.gg.simulation.model.types.ProfessionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public final class Profession implements ILocalizable {

	private @Getter @Setter ProfessionType profession;
	/**
	 * If the player got enough experience in a profession he can level up.
	 * Starts with <code>1</code>.
	 *
	 * @see PlayerTaskType#UPGRADING_MASTER
	 */
	private @Getter @Setter int level;
	private @Getter @Setter int experience;

	public Profession(ProfessionType profession) {
		this(profession, 1, 0);
	}

	@Override
	public String getUnlocalizedName() {
		return profession.getUnlocalizedName() + level;
	}

}
