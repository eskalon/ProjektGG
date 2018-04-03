package de.gg.screen;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.eventbus.Subscribe;

import de.gg.data.RoundEndData;
import de.gg.event.RoundEndEvent;
import de.gg.util.Log;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen is rendered after a round ends.
 */
public class GameRoundendScreen extends BaseGameScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/table.jpg";
	@Asset(Sound.class)
	private final String FLIP_SOUND = "audio/flip-page.mp3";
	private Sound flipSound;

	private volatile RoundEndData data = null;

	private ImageTextButton nextButton;

	public GameRoundendScreen() {
		super(false);
	}

	@Override
	protected void onInit() {
		super.onInit();
		this.backgroundColor = Color.DARK_GRAY;
		this.backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		this.flipSound = assetManager.get(FLIP_SOUND);
	}

	@Override
	protected void initUI() {
		Table dataTable = new Table();
		ScrollPane pane = new ScrollPane(dataTable);

		nextButton = new ImageTextButton("Weiter", skin, "normal");

		nextButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				flipSound.play(1F);
				nextButton.setText("Warten...");
				game.getNetworkHandler().readyUp();

				return true;
			}
		});

		updateUI();

		Table buttonTable = new Table();
		buttonTable.add(nextButton).width(200).padLeft(400);

		Table mTable = new Table();
		mTable.setBackground(skin.getDrawable("book"));
		mTable.add(dataTable).width(580).height(415).row();
		mTable.add(buttonTable).height(50).bottom();

		mainTable.add(mTable);
	}

	private void updateUI() {
		if (data != null) {
			Log.debug("Client", "RoundEndData angekommen: %d", data.test);
			// Data anzeigen
		}

		nextButton.setText("Weiter");
		nextButton.setDisabled(data == null);
		nextButton.setTouchable(
				data == null ? Touchable.disabled : Touchable.enabled);
	}

	@Override
	public void renderGame(float delta) {
		game.getNetworkHandler().updateServer();
	}

	@Subscribe
	public void onRoundEndDataArrived(RoundEndEvent event) {
		this.setData(event.getData());

		updateUI();
	}

	/**
	 * Set the round end data that should get applied.
	 * 
	 * @param data
	 */
	public void setData(RoundEndData data) {
		this.data = data;
	}

	@Override
	public void dispose() {
		super.dispose();

		// if(isLoaded())
		// dispose loaded stuff
	}

}
