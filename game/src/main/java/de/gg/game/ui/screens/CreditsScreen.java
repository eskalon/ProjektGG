package de.gg.game.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.Text;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screens.AbstractImageScreen;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.input.BackInputProcessor;

public class CreditsScreen extends AbstractImageScreen {

	@Asset("CONTRIBUTORS.md")
	private Text creditsText;
	private Texture eskalonLogo;
	private Texture titleLogo;

	private Color backgroundColor = new Color(0.03F, 0.03F, 0.03F, 1F);

	private ProjektGGApplication app;

	private String[] creditsTextSplitted;
	private BitmapFont boldFont, h2Font, h3Font, textFont;

	private float posY = 0;

	public CreditsScreen(ProjektGGApplication application) {
		super(application.getWidth(), application.getHeight());
		this.app = application;
	}

	@Override
	protected void create() {
		super.create();
		creditsTextSplitted = creditsText.getString()
				.replaceAll("\\[(.+)\\]\\(([^ ]+?)( \"(.+)\")?\\)", "$1")
				.replaceAll("\\\\", "").replaceAll("- ", "").replace(" ", "  ")
				.split("\n");

		boldFont = app.getUISkin().getFont("ui-element-21");
		h2Font = app.getUISkin().getFont("ui-title-29");
		h3Font = app.getUISkin().getFont("ui-title-24");
		textFont = app.getUISkin().getFont("ui-text-20");

		eskalonLogo = app.getAssetManager()
				.get(EskalonCommonsAssets.LOGO_TEXTURE_PATH);
		titleLogo = app.getAssetManager().get(AssetLoadingScreen.TITLE_PATH);

		addInputProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				app.getScreenManager().pushScreen("main_menu",
						"longBlendingTransition");
			}
		});
	}

	@Override
	public void show() {
		super.show();

		posY = -180;
	}

	@Override
	public void render(float delta) {
		app.getSpriteBatch().begin();
		app.getSpriteBatch().setProjectionMatrix(app.getUICamera().combined);

		// app.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
		// app.getWidth(), app.getHeight());

		for (int i = 0; i < this.creditsTextSplitted.length; i++) {
			// renderMarkdownStuff(this.creditsTextSplitted[i],
			// posY - i * 30 - 2, Color.BLACK);
			renderMarkdownStuff(this.creditsTextSplitted[i], posY - i * 30,
					Color.WHITE);
		}

		app.getSpriteBatch().end();

		this.posY += delta * 25;
	}

	private void renderMarkdownStuff(String line, float yPos, Color color) {
		if (line.startsWith("###")) {
			h3Font.setColor(color);
			h3Font.draw(app.getSpriteBatch(), line.substring(4), 0, yPos,
					app.getWidth(), Align.center, false);
		} else if (line.startsWith("##")) {
			h2Font.setColor(color);
			h2Font.draw(app.getSpriteBatch(), line.substring(3), 0, yPos,
					app.getWidth(), Align.center, false);
		} else if (line.startsWith("**")) {
			boldFont.setColor(color);
			boldFont.draw(app.getSpriteBatch(),
					line.substring(2, line.length() - 2), 0, yPos,
					app.getWidth(), Align.center, false);
		} else if (line.equals("ESKALON")) {
			app.getSpriteBatch().draw(eskalonLogo,
					(app.getWidth() - eskalonLogo.getWidth()) / 2, yPos);
		} else if (line.equals("PROJEKT  GG")) {
			app.getSpriteBatch().draw(titleLogo,
					(app.getWidth() - titleLogo.getWidth()) / 2, yPos - 110);
		} else {
			textFont.setColor(color);
			textFont.draw(app.getSpriteBatch(), line.strip(), 0, yPos,
					app.getWidth(), Align.center, false);
		}
	}

	@Override
	protected EskalonApplication getApplication() {
		return app;
	}

	@Override
	public Color getClearColor() {
		return backgroundColor;
	}

	@Override
	public void dispose() {
		//
	}

}