package de.eskalon.gg.graphics.rendering;

import com.badlogic.gdx.graphics.g3d.Model;

import de.eskalon.gg.simulation.model.entities.BuildingSlot;

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
