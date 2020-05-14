package de.gg.game.ui.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screens.AbstractImageScreen;
import de.gg.game.core.ProjektGGApplication;

/**
 * The base class of all UI screens. It automatically loads the
 * {@linkplain #skin skin} and sets the {@link #stage} as
 * {@linkplain InputProcessor input processor}. All actors have to be added to
 * the {@link #mainTable}.
 */
public abstract class AbstractGGUIScreen extends AbstractImageScreen {

	/**
	 * Contains a {@linkplain #mainTable table} by default.
	 */
	protected Stage stage;
	/**
	 * The main table, to which all {@link Actor}s will be added.
	 */
	protected Table mainTable;
	/**
	 * The default UI skin. Is automatically set in the
	 * {@link #create()}-method.
	 */
	protected Skin skin;

	protected ProjektGGApplication application;

	public AbstractGGUIScreen(ProjektGGApplication application) {
		super(application.getWidth(), application.getHeight());

		this.application = application;
		this.setMode(ImageScreenMode.CENTERED_ORIGINAL_SIZE);
	}

	@Override
	protected void create() {
		skin = ((ProjektGGApplication) application).getUISkin();

		stage = new Stage(new ScreenViewport(), application.getSpriteBatch());
		mainTable = new Table();
		stage.addActor(mainTable);
		mainTable.setFillParent(true);
		addInputProcessor(stage);
	}

	protected abstract void setUIValues();

	@Override
	public void render(float delta) {
		renderBackground(delta);

		stage.getBatch()
				.setProjectionMatrix(application.getUICamera().combined);
		stage.act(delta);
		stage.draw();

		stage.getBatch().setColor(Color.WHITE);
	}

	protected void renderBackground(float delta) {
		super.render(delta);
	}

	@Override
	public void show() {
		super.show();

		// Fixes ui elements still being selected when the stage is shown a
		// second time
		stage.mouseMoved(1, 1);
		stage.touchUp(1, 1, 0, 0);

		setUIValues();
	}

	@Override
	protected EskalonApplication getApplication() {
		return application;
	}

	@Override
	public void dispose() {
		super.dispose();

		if (stage != null)
			stage.dispose();
	}

}
