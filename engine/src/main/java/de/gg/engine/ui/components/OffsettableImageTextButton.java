package de.gg.engine.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class OffsettableImageTextButton extends ImageTextButton {

	public OffsettableImageTextButton(String text, Skin skin,
			int padX) {
		super(text, skin);

		this.getLabelCell().padLeft(padX).padRight(padX);
	}

}
