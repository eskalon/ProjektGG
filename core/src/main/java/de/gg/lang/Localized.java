package de.gg.lang;

/**
 * This interface is used to mark an already localized entity, i.e. a named one.
 */
public interface Localized {
	/**
	 * @return the localized name of the entity.
	 * @see Lang#get(Localized)
	 */
	public String getLocalizedName();
}