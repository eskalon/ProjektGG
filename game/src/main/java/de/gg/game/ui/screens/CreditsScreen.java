package de.gg.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;
import de.gg.engine.asset.Text;
import de.gg.engine.ui.screens.BaseScreen;
import de.gg.game.core.ProjektGG;
import de.gg.game.input.BackInputProcessor;

public class CreditsScreen extends BaseScreen<ProjektGG> {

	@InjectAsset("ui/backgrounds/town2.jpg")
	private Texture backgroundTexture;
	@InjectAsset("CONTRIBUTORS.md")
	private Text creditsText;

	private String[] creditsTextSplitted;
	private BitmapFont h2Font, h3Font, textFont;

	private float posY = 0;

	@Override
	protected void onInit() {
		creditsTextSplitted = creditsText.getString().replaceAll("\\\\", "")
				.replace(")", "").replace("[", "").replace("](", ", ")
				.split("\n");

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

		for (int i = 0; i < this.creditsTextSplitted.length; i++) {
			float y = posY - i * 30;
			renderMarkdownText(this.creditsTextSplitted[i], y);
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
		//
	}

}
