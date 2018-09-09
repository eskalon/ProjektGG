package de.gg.lang;

/**
 * This interface is used to mark localizable entities.
 */
public interface Localizable {
	/**
	 * @return the unlocalized name of the entity.
	 * @see Lang#get(Localizable)
	 */
	public String getUnlocalizedName();
}
