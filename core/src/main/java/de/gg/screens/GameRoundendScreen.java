package de.gg.screens;

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

import de.gg.events.AllPlayersReadyEvent;
import de.gg.events.ServerReadyEvent;
import de.gg.lang.Lang;
import de.gg.utils.Log;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This screen is rendered after a round ends. When the server is
 * {@linkplain #serverReady ready} all clients can
 * {@linkplain #onAllPlayersReady(AllPlayersReadyEvent) ready up} and after that
 * this screen switches either to the voting or the map screen.
 */
public class GameRoundendScreen extends BaseGameScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/table.jpg";
	@Asset(Sound.class)
	private final String FLIP_SOUND = "audio/flip-page.mp3";
	private Sound flipSound;

	/**
	 * Whether the server is ready to process the next round.
	 *
	 * @see ServerReadyEvent
	 */
	private volatile boolean serverReady = false;

	private ImageTextButton nextButton;
	private Label lastYearTitle, comingYearTitle, lastYearData, comingYearData;

	public GameRoundendScreen() {
		super(false);
	}

	@Override
	protected void onInit(AnnotationAssetManager assetManager) {
		super.onInit(assetManager);
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

		nextButton = new ImageTextButton(Lang.get("ui.generic.continue"), skin,
				"normal");

		nextButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				flipSound.play(game.getSettings().getUIVolumeLevel());
				nextButton.setText(Lang.get("ui.generic.waiting"));

				Log.debug("Client", "Client ist bereit");

				game.getClient().getActionHandler().readyUp();

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

	private synchronized void updateUI() {
		// Informationen zu Rundenende anzeigen
		if (serverReady) {
			lastYearTitle.setText(Lang.get("screen.round_end.last_year"));
			comingYearTitle.setText(Lang.get("screen.round_end.next_year"));

			lastYearData.setText(Lang.get("screen.round_end.test1"));
			comingYearData.setText(Lang.get("screen.round_end.test2"));
		}

		nextButton.setText("Weiter");
		nextButton.setDisabled(!serverReady);
		nextButton.setTouchable(
				!serverReady ? Touchable.disabled : Touchable.enabled);
	}

	@Override
	public void renderGame(float delta) {
		if (game.getClient() != null)
			game.getClient().updatePing(delta);

		// if (game.isHost())
		// game.getServer().update();
	}

	@Subscribe
	@Override
	public synchronized void onServerReady(ServerReadyEvent event) {
		this.serverReady = true;

		updateUI();
	}

	public void setServerReady() {
		this.serverReady = true;
	}

	@Subscribe
	public void onAllPlayersReady(AllPlayersReadyEvent event) {
		this.serverReady = false;

		if (event.isNextRound()) {
			if (!game.getClient().getCity().getMattersToHoldVoteOn().isEmpty())
				game.pushScreen("vote");
			else
				game.pushScreen("map");
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		// if(isLoaded())
		// dispose loaded stuff
	}

}
