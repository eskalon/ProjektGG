package de.gg.engine.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class OffsetableImageTextButton extends ImageTextButton {

	public OffsetableImageTextButton(String text, Skin skin, String styleName,
			int padX) {
		super(text, skin, styleName);

		this.getLabelCell().padLeft(padX).padRight(padX);
	}

}
