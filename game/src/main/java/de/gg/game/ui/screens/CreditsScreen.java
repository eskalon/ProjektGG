package de.gg.game.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.Text;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screens.AbstractImageScreen;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.input.BackInputProcessor;

public class CreditsScreen extends AbstractImageScreen {

	@Asset("ui/backgrounds/lobby_screen.jpg")
	private Texture backgroundTexture;
	@Asset("CONTRIBUTORS.md")
	private Text creditsText;

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

		for (String s : creditsTextSplitted) {
			System.out.println("[" + s + "]");
		}

		boldFont = app.getUISkin().getFont("ui-element-21");
		h2Font = app.getUISkin().getFont("ui-title-29");
		h3Font = app.getUISkin().getFont("ui-title-24");
		textFont = app.getUISkin().getFont("ui-text-20");

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

		posY = -20;
	}

	@Override
	public void render(float delta) {
		app.getSpriteBatch().begin();
		app.getSpriteBatch().setProjectionMatrix(app.getUICamera().combined);

		app.getSpriteBatch().draw(this.backgroundTexture, 0, 0, app.getWidth(),
				app.getHeight());

		for (int i = 0; i < this.creditsTextSplitted.length; i++) {
			renderMarkdownText(this.creditsTextSplitted[i], 48,
					posY - i * 30 - 2, Color.BLACK);
			renderMarkdownText(this.creditsTextSplitted[i], 50, posY - i * 30,
					Color.WHITE);
		}

		app.getSpriteBatch().end();

		this.posY += delta * 25;
	}

	private void renderMarkdownText(String line, float xPos, float yPos,
			Color color) {
		if (line.startsWith("###")) {
			h3Font.setColor(color);
			h3Font.draw(app.getSpriteBatch(), line.substring(4), xPos, yPos,
					app.getWidth(), Align.center, false);
		} else if (line.startsWith("##")) {
			h2Font.setColor(color);
			h2Font.draw(app.getSpriteBatch(), line.substring(3), xPos, yPos,
					app.getWidth(), Align.center, false);
		} else if (line.startsWith("**")) {
			boldFont.setColor(color);
			boldFont.draw(app.getSpriteBatch(),
					line.substring(2, line.length() - 2), xPos, yPos,
					app.getWidth(), Align.center, false);
		} else {
			textFont.setColor(color);
			textFont.draw(app.getSpriteBatch(), line.strip(), xPos, yPos,
					app.getWidth(), Align.center, false);
		}
	}

	@Override
	protected EskalonApplication getApplication() {
		return app;
	}

	@Override
	public void dispose() {
		//
	}

}