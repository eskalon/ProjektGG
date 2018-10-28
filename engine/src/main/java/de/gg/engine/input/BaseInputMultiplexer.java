package de.gg.engine.input;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

/**
 * This input multiplexer allows the game to use application wide key binds.
 */
public class BaseInputMultiplexer extends InputMultiplexer {

	/**
	 * Removes all input processors.
	 *
	 * @see #clear()
	 */
	public void removeInputProcessors() {
		this.clear();
	}

	/**
	 * Removes all input processors contained in the given array.
	 *
	 * @param processors
	 *            The processor to remove.
	 * @see #removeProcessor(InputProcessor)
	 */
	public void removeInputProcessors(Array<InputProcessor> processors) {
		for (InputProcessor p : processors) {
			removeProcessor(p);
		}
	}

}