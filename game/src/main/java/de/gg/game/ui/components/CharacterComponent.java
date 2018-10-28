package de.gg.game.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.gg.engine.lang.Lang;
import de.gg.engine.utils.ColorUtils;
import de.gg.game.entities.Character;

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
	 * @param character
	 *            The character.
	 * @param opinion
	 *            The opinion this character has of the player. If this is
	 *            <code>-1</code> the opinion bar is not rendered.
	 */
	public CharacterComponent(Skin skin, Character character, int opinion) {
		super();
		String fullName = character.getName() + " " + character.getSurname();
		this.nameLabel = new Label(fullName.replace(" ", "  "), skin);
		if (character.getPosition() != null)
			this.positionLabel = new Label(Lang.get(character.getPosition()),
					skin, "main-white-18");

		if (opinion != -1) {
			this.shapeRenderer = new ShapeRenderer();

			this.opinionPercentage = opinion / 100F;
			if (this.opinionPercentage < 0.025F)
				this.opinionPercentage = 0.025F;
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

}
