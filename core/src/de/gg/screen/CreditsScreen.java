package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.gg.input.BackInputProcessor;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class CreditsScreen extends BaseScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town2.jpg";
	@Asset(Text.class)
	private final String CREDITS_TEXT_PATH = "CONTRIBUTORS.md";
	private Texture backgroundTexture;
	private String[] creditsText;
	private BitmapFont h2Font, h3Font, textFont;

	private float posY = 0;

	@Override
	protected void onInit() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		String markdownText = assetManager.get(CREDITS_TEXT_PATH, Text.class)
				.getString();
		creditsText = markdownText.replaceAll("\\\\", "").replace(")", "")
				.replace("[", "").replace("](", ", ").split("\n");

		h2Font = this.game.getUISkin().getFont("title-24");
		h3Font = this.game.getUISkin().getFont("main-22");
		textFont = this.game.getUISkin().getFont("main-19");

		addInputProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				game.pushScreen("mainMenu");
			}
		});
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.getSpriteBatch().begin();
		game.getSpriteBatch().setProjectionMatrix(game.getUICamera().combined);

		game.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
				game.getViewportWidth(), game.getViewportHeight());

		for (int i = 0; i < this.creditsText.length; i++) {
			float y = posY - i * 30;
			renderMarkdownText(this.creditsText[i], y);
		}

		game.getSpriteBatch().end();

		// move the text
		this.posY += delta * 25;
	}

	private void renderMarkdownText(String line, float yPos) {
		if (line.startsWith("###")) {
			h3Font.draw(game.getSpriteBatch(), line.substring(4), 50, yPos);
		} else if (line.startsWith("##")) {
			h2Font.draw(game.getSpriteBatch(), line.substring(3), 50, yPos);
		} else {
			textFont.draw(game.getSpriteBatch(), line, 50, yPos);
		}
	}

	@Override
	public void dispose() {

	}

}
