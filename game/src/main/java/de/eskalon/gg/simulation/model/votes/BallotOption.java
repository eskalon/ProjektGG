package de.eskalon.gg.simulation.model.votes;

import de.eskalon.commons.lang.ILocalizable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BallotOption implements ILocalizable {

	private String unlocalizedName;
	/**
	 * The value of this option. Is oftentimes an index or ID.
	 */
	private @Getter int value;
	/**
	 * Whether the value of this vote is a character ID.
	 */
	private @Getter boolean character;

	public BallotOption(String text, int value) {
		this(text, value, false);
	}

	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

}
