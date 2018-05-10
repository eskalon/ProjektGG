package de.gg.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class OffsetableImageTextButton extends ImageTextButton {

	public OffsetableImageTextButton(String text, Skin skin, int padX) {
		super(text, skin, "small");

		this.getLabelCell().padLeft(padX).padRight(padX);
	}

}
