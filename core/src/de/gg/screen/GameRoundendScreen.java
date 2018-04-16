package de.gg.screen;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.google.common.eventbus.Subscribe;

import de.gg.data.RoundEndData;
import de.gg.event.RoundEndEvent;
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
	private Label lastYearTitle, comingYearTitle, lastYearData, comingYearData;

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
		lastYearTitle = new Label("", skin, "big");
		lastYearTitle.setAlignment(Align.topLeft);
		comingYearTitle = new Label("", skin, "big");
		comingYearTitle.setAlignment(Align.topLeft);

		lastYearData = new Label("", skin);
		lastYearData.setAlignment(Align.topLeft);
		lastYearData.setWrap(true);
		comingYearData = new Label("", skin);
		comingYearData.setAlignment(Align.topLeft);
		comingYearData.setWrap(true);

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

		Table dataTable = new Table();
		// ScrollPane pane = new ScrollPane(dataTable);
		dataTable.add(lastYearTitle).top().left().width(310).padRight(20);
		dataTable.add(comingYearTitle).padLeft(55).width(295).padBottom(6)
				.row();

		dataTable.add(lastYearData).padRight(20).width(310).height(395);
		dataTable.add(comingYearData).padLeft(55).width(295).height(395);

		Table buttonTable = new Table();
		buttonTable.add(nextButton).width(200).padLeft(405);

		Table mTable = new Table();
		mTable.setBackground(skin.getDrawable("book"));
		mTable.add(dataTable).top().left().width(660).height(425).row();
		mTable.add(buttonTable).height(50).bottom();

		mainTable.add(mTable);
	}

	private void updateUI() {
		// Data anzeigen
		if (data != null) {
			lastYearTitle.setText("Das vergangene Jahr");
			comingYearTitle.setText("Kommendes Jahr");

			lastYearData.setText("-3 Gold für XYZ");
			comingYearData.setText(String.format(
					"-15 Gold für XYZ \n+3 AP\n Die Geschäfte öffnen um %d",
					data.getOpeningHourNextDay()));
		}

		nextButton.setText("Weiter");
		nextButton.setDisabled(data == null);
		nextButton.setTouchable(
				data == null ? Touchable.disabled : Touchable.enabled);
	}

	@Override
	public void renderGame(float delta) {
		game.getNetworkHandler().updatePing(delta);
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
