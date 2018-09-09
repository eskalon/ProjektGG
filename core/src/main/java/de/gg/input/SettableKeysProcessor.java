package de.gg.input;

import com.badlogic.gdx.InputProcessor;

import de.gg.screens.BaseScreen;
import de.gg.setting.GameSettings;

/**
 * Is implemented by {@link InputProcessor}s that have changeable key binds.
 * <p>
 * Automatically loads the keys whenever the input processor is registered when
 * used in conjunction with {@link BaseScreen}.
 */
public interface SettableKeysProcessor {

	public void loadKeybinds(GameSettings settings);

}
