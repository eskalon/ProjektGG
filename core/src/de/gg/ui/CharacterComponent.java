package de.gg.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.gg.util.ColorUtils;

/**
 * This component represents an character. It displays the character's name and
 * it's opinion of the player.
 */
public class CharacterComponent extends Table {

	// private Image characterImage;
	private Label nameLabel;
	private Label positionLabel;

	private ShapeRenderer shapeRenderer;
	private float opinionPercentage;
	private Color color;

	/**
	 * @param skin
	 *            The game's skin.
	 * @param name
	 *            The name of the character displayed.
	 * @param The
	 *            position of the character. Displayed below its name.
	 * @param opinion
	 *            The opinion this character has of the player.
	 */
	public CharacterComponent(Skin skin, String name, String position,
			int opinion) {
		super();
		this.nameLabel = new Label(name.replace(" ", "  "), skin);
		if (position != null)
			this.positionLabel = new Label(position, skin, "main-white-18");

		if (opinion != -1) {
			this.shapeRenderer = new ShapeRenderer();

			this.opinionPercentage = opinion / 100F;
			color = ColorUtils.getInterpolatedColor(0, 128,
					opinionPercentage <= 0.1F ? 0 : (opinionPercentage - 0.1F),
					75, 70);

			this.addActor(new Actor() {
				@Override
				public void draw(Batch batch, float parentAlpha) {
					batch.end();

					shapeRenderer.begin(ShapeType.Filled);
					shapeRenderer.setColor(color);
					shapeRenderer.rect(getX(),
							getY() + (positionLabel == null ? 25 : 50),
							opinionPercentage * 100, 7);
					shapeRenderer.end();
					batch.begin();
				}
			});
		}

		this.add(nameLabel);

		if (positionLabel != null) {
			this.row();
			this.add(positionLabel);
		}
	}

	public CharacterComponent(Skin skin, String name) {
		this(skin, name, null, -1);
	}

}
