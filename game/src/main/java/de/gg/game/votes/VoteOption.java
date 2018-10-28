package de.gg.game.votes;

import de.gg.engine.lang.Localizable;

public class VoteOption implements Localizable {

	private String unlocalizedName;
	/**
	 * The value of this option. Is oftentimes an index or id.
	 */
	private int value;
	/**
	 * Whether the value of this vote is a character id.
	 */
	private boolean character;

	public VoteOption(String text, int value) {
		this(text, value, false);
	}

	public VoteOption(String unlocalizedName, int value, boolean character) {
		this.unlocalizedName = unlocalizedName;
		this.value = value;
		this.character = character;
	}

	/**
	 * @return whether the vote's value is a character id.
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
