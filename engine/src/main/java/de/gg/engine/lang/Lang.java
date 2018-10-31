package de.gg.engine.lang;

import java.text.ChoiceFormat;

import com.badlogic.gdx.utils.I18NBundle;

/**
 * This utility class takes care of the localization.
 * 
 * @see I18NBundle
 * @see Localizable
 * @see Localized
 */
public class Lang {

	private static I18NBundle bundle;

	private Lang() {
		// shouldn't get instantiated
	}

	/**
	 * @param bundle
	 *            the used language bundle.
	 */
	public static void setBundle(I18NBundle bundle) {
		Lang.bundle = bundle;
	}

	/**
	 * @param key
	 * @return the localization for a given key.
	 * @see I18NBundle#get(String)
	 */
	public static String get(String key) {
		return bundle.get(key);
	}

	/**
	 * @param localizable
	 * @return the localization for a localizable object.
	 */
	public static String get(Localizable localizable) {
		return get(localizable.getUnlocalizedName());
	}

	/**
	 * @param localized
	 * @return the localization for an already localized object.
	 */
	public static String get(Localized localized) {
		return localized.getLocalizedName();
	}

	/**
	 * Returns the localization for the given key formatted with the given
	 * arguments. The arguments are localized as well, if they implement either
	 * {@link Localizable} or {@link Localized}. <code>Boolean</code> arguments
	 * are cast to integers (<code>0</code> denoting <code>false</code> and
	 * <code>1</code> denoting <code>true</code>) so they can be used for
	 * {@link ChoiceFormat}.
	 * 
	 * @param key
	 *            the localization key (= unlocalized name).
	 * @param args
	 *            the used parameters. Each one of these will be localized as
	 *            well, if possible.
	 * @return the localization for the key formatted with the given arguments
	 * @see I18NBundle#format(String, Object...)
	 */
	public static String get(String key, Object... args) {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof Boolean) {
					// Cast boolean to integer -> is usable for ChoiceFormat
					args[i] = ((Boolean) args[i]) ? 1 : 0;
				} else if (args[i] instanceof Localizable) {
					// Localize the given object
					args[i] = get((Localizable) args[i]);
				} else if (args[i] instanceof Localized) {
					// If the object is already localized use its name
					args[i] = get(((Localized) args[i]));
				}
			}
		}

		return bundle.format(key, args);
	}

}
