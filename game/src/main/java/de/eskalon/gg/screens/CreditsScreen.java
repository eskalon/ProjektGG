package de.eskalon.gg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.damios.guacamole.gdx.assets.Text;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;
import de.eskalon.gg.input.BackInputProcessor;

public class CreditsScreen extends AbstractEskalonScreen {

	private @Inject SpriteBatch batch;
	private Viewport viewport;

	@Asset("CONTRIBUTORS.md")
	private @Inject Text creditsText;
	private Texture eskalonLogo;
	private Texture titleLogo;

	private Color backgroundColor = new Color(0.03F, 0.03F, 0.03F, 1F);

	private String[] creditsTextSplitted;
	private BitmapFont boldFont, h2Font, h3Font, textFont;

	private float posY = -180;

	@Inject
	public CreditsScreen(AssetManager assetManager, Skin skin,
			EskalonScreenManager screenManager) {
		this.viewport = new ScreenViewport();

		String text = "PROJEKT GG\n" + "\n"
				+ "This Game Was Produced by eskalon\n" + "\n" + "\n" + "\n"
				+ "\n" + "\n" + "ESKALON\n" + "\n" + "\n"
				+ creditsText.getString() + "\n" + "\n" + "\n" + "\n"
				+ "\nAnd a Special Thanks to You!";
		creditsTextSplitted = text
				.replaceAll("\\[(.+)\\]\\(([^ ]+?)( \"(.+)\")?\\)", "$1")
				.replaceAll("\\\\", "").replaceAll("- ", "").replace(" ", "  ")
				.split("\n");

		boldFont = skin.getFont("ui-element-21");
		h2Font = skin.getFont("ui-title-29");
		h3Font = skin.getFont("ui-title-24");
		textFont = skin.getFont("ui-text-20");

		eskalonLogo = assetManager.get(EskalonCommonsAssets.LOGO_TEXTURE_PATH);
		titleLogo = assetManager.get(AssetLoadingScreen.TITLE_PATH);

		addInputProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				screenManager.pushScreen(MainMenuScreen.class,
						"longBlendingTransition");
			}
		});
	}

	@Override
	public void render(float delta) {
		viewport.apply();
		batch.setProjectionMatrix(viewport.getCamera().combined);

		batch.begin();

		// batch.draw(this.backgroundTexture, 0, 0,
		// app.getWidth(), app.getHeight());

		for (int i = 0; i < this.creditsTextSplitted.length; i++) {
			// renderMarkdownStuff(this.creditsTextSplitted[i],
			// posY - i * 30 - 2, Color.BLACK);
			renderMarkdownStuff(this.creditsTextSplitted[i], posY - i * 30,
					Color.WHITE);
		}

		batch.end();

		this.posY += delta * 25;
	}

	private void renderMarkdownStuff(String line, float yPos, Color color) {
		if (line.startsWith("###")) {
			h3Font.setColor(color);
			h3Font.draw(batch, line.substring(4), 0, yPos,
					Gdx.graphics.getWidth(), Align.center, false);
		} else if (line.startsWith("##")) {
			h2Font.setColor(color);
			h2Font.draw(batch, line.substring(3), 0, yPos,
					Gdx.graphics.getWidth(), Align.center, false);
		} else if (line.startsWith("**")) {
			boldFont.setColor(color);
			boldFont.draw(batch, line.substring(2, line.length() - 2), 0, yPos,
					Gdx.graphics.getWidth(), Align.center, false);
		} else if (line.equals("ESKALON")) {
			batch.draw(eskalonLogo,
					(Gdx.graphics.getWidth() - eskalonLogo.getWidth()) / 2,
					yPos);
		} else if (line.equals("PROJEKT  GG")) {
			batch.draw(titleLogo,
					(Gdx.graphics.getWidth() - titleLogo.getWidth()) / 2,
					yPos - 110);
		} else {
			textFont.setColor(color);
			textFont.draw(batch, line.trim(), 0, yPos, Gdx.graphics.getWidth(),
					Align.center, false);
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public Color getClearColor() {
		return backgroundColor;
	}

	@Override
	public void dispose() {
		// there is nothing to dispose
	}

}