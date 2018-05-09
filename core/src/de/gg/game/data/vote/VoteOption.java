package de.gg.game.data.vote;

public class VoteOption {

	private String text;
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

	public VoteOption(String text, int value, boolean character) {
		this.text = text;
		this.value = value;
		this.character = character;
	}

	/**
	 * @return whether the vote's value is a character id.
	 */
	public boolean isCharacter() {
		return character;
	}

	public String getText() {
		return text;
	}

	public int getValue() {
		return value;
	}

}
