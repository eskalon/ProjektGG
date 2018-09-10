package de.gg.lang;

import com.badlogic.gdx.utils.I18NBundle;

/**
 * This utility class takes care of the localization.
 * 
 * @see I18NBundle
 * @see Localizable
 * @see Localized
 */
public class Lang {

	public static final String LANG_BUNDLE_PATH = "lang/lang";
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
	 * @param key
	 *            the localization key (= unlocalized name).
	 * @param args
	 *            the used parameters. Each one of these will be localized as
	 *            well, if possible.
	 * @return
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
