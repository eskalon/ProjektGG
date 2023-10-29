package de.eskalon.gg.simulation.model.entities;

import de.eskalon.gg.simulation.model.types.CrimeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public final class Evidence {

	private @Getter @Setter Character offender;
	/**
	 * The victim of the crime this evidence is for. Can either be a character
	 * or a building.
	 */
	private @Getter @Setter Object victim;
	/**
	 * Whether the evidence is fabricated.
	 */
	private @Getter @Setter boolean isStaged;
	private @Getter @Setter CrimeType crime;

}
