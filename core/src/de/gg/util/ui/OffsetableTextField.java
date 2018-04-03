package de.gg.util.ui;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class OffsetableTextField extends TextField {

	private final int offset;

	public OffsetableTextField(String text, Skin skin, int offset) {
		super(text, skin);

		this.offset = offset;
		this.textOffset = offset;
	}

	protected void calculateOffsets() {
		super.calculateOffsets();

		this.textOffset = offset;
	}

}