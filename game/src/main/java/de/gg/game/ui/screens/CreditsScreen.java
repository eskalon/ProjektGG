package de.gg.game.ui.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

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
	private BitmapFont h2Font, h3Font, textFont;

	private float posY = 0;

	public CreditsScreen(ProjektGGApplication application) {
		super(application.getWidth(), application.getHeight());
		this.app = application;
	}

	@Override
	protected void create() {
		super.create();
		creditsTextSplitted = creditsText.getString().replaceAll("\\\\", "")
				.replace(")", "").replace("[", "").replace("](", ", ")
				.split("\n");

		h2Font = app.getUISkin().getFont("title-24");
		h3Font = app.getUISkin().getFont("main-22");
		textFont = app.getUISkin().getFont("main-19");

		addInputProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				app.getScreenManager().pushScreen("main_menu", null);
			}
		});
	}

	@Override
	public void show() {
		super.show();

		posY = 0;
	}

	@Override
	public void render(float delta) {
		app.getSpriteBatch().begin();
		app.getSpriteBatch().setProjectionMatrix(app.getUICamera().combined);

		app.getSpriteBatch().draw(this.backgroundTexture, 0, 0, app.getWidth(),
				app.getHeight());

		for (int i = 0; i < this.creditsTextSplitted.length; i++) {
			renderMarkdownText(this.creditsTextSplitted[i], posY - i * 30);
		}

		app.getSpriteBatch().end();

		this.posY += delta * 25;
	}

	private void renderMarkdownText(String line, float yPos) {
		if (line.startsWith("###")) {
			h3Font.draw(app.getSpriteBatch(), line.substring(4), 50, yPos);
		} else if (line.startsWith("##")) {
			h2Font.draw(app.getSpriteBatch(), line.substring(3), 50, yPos);
		} else {
			textFont.draw(app.getSpriteBatch(), line, 50, yPos);
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