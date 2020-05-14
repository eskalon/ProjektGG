package de.gg.game.model.votes;

import de.eskalon.commons.lang.ILocalizable;

public class BallotOption implements ILocalizable {

	private String unlocalizedName;
	/**
	 * The value of this option. Is oftentimes an index or ID.
	 */
	private int value;
	/**
	 * Whether the value of this vote is a character ID.
	 */
	private boolean character;

	public BallotOption(String text, int value) {
		this(text, value, false);
	}

	public BallotOption(String unlocalizedName, int value, boolean character) {
		this.unlocalizedName = unlocalizedName;
		this.value = value;
		this.character = character;
	}

	/**
	 * @return whether the vote's value is a character ID.
	 */
	public boolean isCharacter() {
		return character;
	}

	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public int getValue() {
		return value;
	}

}
