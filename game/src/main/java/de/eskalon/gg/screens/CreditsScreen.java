package de.eskalon.gg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.damios.guacamole.gdx.assets.Text;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.screens.AbstractImageScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;
import de.eskalon.gg.input.BackInputProcessor;

public class CreditsScreen extends AbstractImageScreen {

	private @Inject Skin skin;
	private @Inject AssetManager assetManager;
	private @Inject EskalonScreenManager screenManager;

	@Asset("ui/backgrounds/credits.png")
	private @Inject Texture backgroundTexture;
	@Asset("CONTRIBUTORS.md")
	private @Inject Text creditsText;
	private Texture eskalonLogo, eskalonLogoDark, titleLogo;

	private String[] creditsTextSplitted;
	private BitmapFont boldFont, h2Font, h3Font, textFont;

	private float posY = -210;

	@Override
	public void show() {
		super.show();

		setImage(backgroundTexture);

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
		eskalonLogoDark = assetManager
				.get(EskalonCommonsAssets.LOGO_DARK_TEXTURE_PATH);
		titleLogo = assetManager.get(AssetLoadingScreen.TITLE_PATH);

		addInputProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				screenManager.pushScreen(MainMenuScreen.class,
						"blendingTransition");
			}
		});
	}

	@Override
	public void renderWithinBatch(SpriteBatch batch, float delta) {
		for (int i = 0; i < this.creditsTextSplitted.length; i++) {
			renderMarkdownStuff(this.creditsTextSplitted[i], posY - i * 30);
		}
		this.posY += delta * 40;
	}

	private void renderMarkdownStuff(String line, float yPos) {
		if (line.startsWith("###")) {
			renderString(h3Font, line.substring(4), 0, yPos - 2, Color.BLACK);
			renderString(h3Font, line.substring(4), 0, yPos, Color.WHITE);
			renderString(h3Font, line.substring(4), 0, yPos, Color.WHITE);
		} else if (line.startsWith("##")) {
			renderString(h2Font, line.substring(3), 0, yPos - 2, Color.BLACK);
			renderString(h2Font, line.substring(3), 0, yPos, Color.WHITE);
		} else if (line.startsWith("**")) {
			renderString(boldFont, line.substring(2, line.length() - 2), 0,
					yPos - 2, Color.BLACK);
			renderString(boldFont, line.substring(2, line.length()), 0, yPos,
					Color.WHITE);
		} else if (line.equals("ESKALON")) {
			batch.draw(eskalonLogoDark,
					(Gdx.graphics.getWidth() - eskalonLogo.getWidth()) / 2,
					yPos - 5);
			batch.draw(eskalonLogo,
					(Gdx.graphics.getWidth() - eskalonLogo.getWidth()) / 2,
					yPos);
		} else if (line.equals("PROJEKT  GG")) {
			batch.draw(titleLogo,
					(Gdx.graphics.getWidth() - titleLogo.getWidth()) / 2,
					yPos - 110);
		} else {
			renderString(textFont, line.trim(), 0, yPos - 2, Color.BLACK);
			renderString(textFont, line.trim(), 0, yPos, Color.WHITE);
		}
	}

	private void renderString(BitmapFont font, String text, float xPos,
			float yPos, Color color) {
		font.setColor(color);
		font.draw(batch, text, xPos, yPos, Gdx.graphics.getWidth(),
				Align.center, false);
	}

}