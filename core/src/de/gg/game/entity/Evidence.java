package de.gg.game.entity;

import de.gg.game.entity.CrimeTypes.CrimeType;

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

	public Character getOffender() {
		return offender;
	}

	public void setOffender(Character offender) {
		this.offender = offender;
	}

	public Object getVictim() {
		return victim;
	}

	public void setVictim(Object victim) {
		this.victim = victim;
	}

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
