package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.gg.ui.AnimationlessDialog;
import de.gg.util.SimpleListener;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * The base class of all UI screens. It automatically loads the
 * {@linkplain #skin skin} and sets the {@link #stage} as
 * {@linkplain InputProcessor input processor}. All actors have to be added to
 * the {@link #mainTable}.
 */
public abstract class BaseUIScreen extends BaseScreen {

	@Asset(Sound.class)
	protected static final String BUTTON_CLICK_SOUND = "audio/button-tick.mp3";
	protected Sound buttonClickSound;

	/**
	 * Contains a {@linkplain #mainTable table} by default. The stage is cleared
	 * whenever the screen is {@linkplain #show() shown}.
	 */
	protected Stage stage;
	/**
	 * The main table, to which all {@link Actor}s will be added. The mainTable
	 * is recreated whenever the screen is {@linkplain #show() shown}.
	 */
	protected Table mainTable;
	/**
	 * The default UI skin. Is automatically set in the
	 * {@link #onInit()}-method.
	 */
	protected Skin skin;
	/**
	 * An easy way to add a backgroundImage is to set this variable. The screen
	 * automatically takes care of rendering it.
	 */
	protected Texture backgroundTexture;

	@Override
	protected void onInit(AnnotationAssetManager assetManager) {
		skin = game.getUISkin();
		buttonClickSound = assetManager.get(BUTTON_CLICK_SOUND);
		stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
		addInputProcessor(stage);
	}

	/**
	 * This method is the called whenever this screen is {@linkplain #show
	 * shown}. The stage's actors will be constructed in this method.
	 */
	protected abstract void initUI();

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (backgroundTexture != null) {
			game.getSpriteBatch().begin();
			game.getSpriteBatch()
					.setProjectionMatrix(game.getUICamera().combined);
			game.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
					game.getViewportWidth(), game.getViewportHeight());
			game.getSpriteBatch().end();
		}

		stage.getBatch().setProjectionMatrix(game.getUICamera().combined);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		super.show();
		stage.clear();
		mainTable = new Table();
		stage.addActor(mainTable);
		mainTable.setFillParent(true);

		mainTable.setDebug(game.showDebugStuff());

		initUI();

		stage.mouseMoved(1, 1); // verhindert einen kleinen Anzeige-Bug bei
								// erneuten Anzeigen eines Screens
	}

	@Override
	public void dispose() {
		if (stage != null)
			stage.dispose();
	}

	/**
	 * Shows an informational dialog on the current screen.
	 *
	 * @param title
	 *            The dialog's title.
	 * @param text
	 *            The dialog's informational text.
	 * @param showButton
	 *            Whether this dialog should show an "Ok" button to close
	 *            itself.
	 * @param listener
	 *            A result listener for the dialog.
	 * @return The dialog.
	 *
	 * @see AnimationlessDialog#show(Stage)
	 * @see #showInfoDialog(String, String, boolean)
	 * @see #showInfoDialog(String, String)
	 */
	protected AnimationlessDialog showInfoDialog(String title, String text,
			boolean showButton, SimpleListener listener) {
		AnimationlessDialog dialog = new AnimationlessDialog(title, skin) {
			@Override
			public void result(Object obj) {
				if (listener != null)
					listener.listen(obj);
				else
					super.result(obj);
			}
		};
		dialog.text(text);
		if (showButton) {
			dialog.key(Keys.ENTER, true);
			dialog.button("Ok");
		}

		dialog.show(stage);
		return dialog;
	}

	/**
	 * Shows an informational dialog on the current screen.
	 *
	 * @param title
	 *            The dialog's title.
	 * @param text
	 *            The dialog's informational text.
	 * @param showButton
	 *            Whether this dialog should show an "Ok" button to close
	 *            itself.
	 * @return The dialog.
	 *
	 * @see #showInfoDialog(String, String, boolean, SimpleListener)
	 * @see #showInfoDialog(String, String)
	 */
	protected AnimationlessDialog showInfoDialog(String title, String text,
			boolean showButton) {
		return showInfoDialog(title, text, showButton, null);
	}

	/**
	 * Shows an informational dialog on the current screen with an "Ok" button
	 * to close it.
	 *
	 * @param title
	 *            The dialog's title.
	 * @param text
	 *            The dialog's informational text.
	 * @return The dialog.
	 *
	 * @see #showInfoDialog(String, String, boolean, SimpleListener)
	 * @see #showInfoDialog(String, String, boolean)
	 */
	protected AnimationlessDialog showInfoDialog(String title, String text) {
		return showInfoDialog(title, text, true);
	}
}
