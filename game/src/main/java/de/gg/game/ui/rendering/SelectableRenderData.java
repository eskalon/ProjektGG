package de.gg.game.ui.rendering;

import com.badlogic.gdx.graphics.g3d.Model;

import de.gg.engine.ui.rendering.BaseRenderData;
import de.gg.game.model.entities.BuildingSlot;

public class SelectableRenderData extends BaseRenderData {

	public SelectableRenderData(Model model) {
		super(model);
	}

	public SelectableRenderData(Model scene, String rootNode,
			boolean mergeTransform) {
		super(scene, rootNode, mergeTransform);
	}

	/**
	 * Whether this model is selected by the player.
	 *
	 * @see GameRenderer#renderOutlines(BuildingSlot[])
	 */
	public boolean isSelected = false;

}
