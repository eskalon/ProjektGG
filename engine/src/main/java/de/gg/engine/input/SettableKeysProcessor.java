package de.gg.engine.input;

import com.badlogic.gdx.InputProcessor;

import de.gg.engine.core.BaseGame;
import de.gg.engine.setting.ConfigHandler;

/**
 * Is implemented by {@link InputProcessor}s that have changeable key binds.
 * <p>
 * Automatically loads the keys whenever the input processor is registered when
 * used in conjunction with {@link BaseGame}.
 */
public interface SettableKeysProcessor {

	public void loadKeybinds(ConfigHandler settings);

}