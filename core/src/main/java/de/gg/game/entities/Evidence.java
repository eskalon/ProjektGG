package de.gg.game.entities;

import de.gg.game.types.CrimeType;

public class Evidence {

	private Character offender;
	/**
	 * Can either be a character or a building.
	 */
	private Object victim;
	/**
	 * Whether the evidence is fabricated.
	 */
	private boolean isStaged;
	private CrimeType crime;

	public Evidence() {
		// default public constructor
	}

	public Character getOffender() {
		return offender;
	}

	public void setOffender(Character offender) {
		this.offender = offender;
	}

	/**
	 * @return the victim of the crime this evidence is for. Can either be a
	 *         character or a building.
	 */
	public Object getVictim() {
		return victim;
	}

	public void setVictim(Object victim) {
		this.victim = victim;
	}

	/**
	 * @return Whether the evidence is fabricated.
	 */
	public boolean isStaged() {
		return isStaged;
	}

	public void setStaged(boolean isStaged) {
		this.isStaged = isStaged;
	}

	public CrimeType getCrime() {
		return crime;
	}

	public void setCrime(CrimeType crime) {
		this.crime = crime;
	}

}
