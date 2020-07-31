package de.gg.game.ui.screens;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.google.common.eventbus.Subscribe;

import de.damios.guacamole.gdx.Log;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.lang.Lang;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.events.AllPlayersReadyEvent;
import de.gg.game.events.ServerReadyEvent;
import de.gg.game.misc.DiscordGGHandler;
import de.gg.game.misc.GameClock;

/**
 * This screen is rendered after a round ends. When the server is
 * {@linkplain #serverReady ready} all clients can
 * {@linkplain #onAllPlayersReady(AllPlayersReadyEvent) ready up} and after that
 * this screen switches either to the voting or the map screen.
 */
public class GameRoundendScreen extends AbstractGameScreen {

	@Asset("ui/backgrounds/round_end_screen.jpg")
	private Texture backgroundTexture;
	@Asset("audio/page_flip.mp3")
	private Sound flipSound;

	/**
	 * Whether the server is ready to process the next round.
	 *
	 * @see ServerReadyEvent
	 */
	private volatile boolean serverReady = false;

	private ImageTextButton nextButton;
	private Label lastYearTitle, comingYearTitle, lastYearData, comingYearData;

	public GameRoundendScreen(ProjektGGApplication application) {
		super(application, false);
	}

	@Override
	protected void create() {
		super.create();
		setImage(backgroundTexture);

		lastYearTitle = new Label("", skin, "ink_title");
		lastYearTitle.setAlignment(Align.topLeft);
		comingYearTitle = new Label("", skin, "ink_title");
		comingYearTitle.setAlignment(Align.topLeft);

		lastYearData = new Label("", skin, "ink_text");
		lastYearData.setAlignment(Align.topLeft);
		lastYearData.setWrap(true);
		comingYearData = new Label("", skin, "ink_text");
		comingYearData.setAlignment(Align.topLeft);
		comingYearData.setWrap(true);

		nextButton = new ImageTextButton("", skin, "medium");

		nextButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				application.getSoundManager().playSoundEffect("page_flip");
				nextButton.setText(Lang.get("ui.generic.waiting"));

				Log.debug("Client", "Client ist ready for next round");

				application.getClient().getActionHandler().readyUp();

				return true;
			}
		});

		Table dataTable = new Table();
		// ScrollPane pane = new ScrollPane(dataTable);
		dataTable.add(lastYearTitle).top().left().width(310).padLeft(20)
				.padRight(0);
		dataTable.add(comingYearTitle).padLeft(100).width(295).padBottom(10)
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

	@Override
	protected void setUIValues() {
		if (serverReady) {
			lastYearTitle.setText(Lang.get("screen.round_end.last_year"));
			comingYearTitle.setText(Lang.get("screen.round_end.next_year"));

			lastYearData.setText(Lang.get("screen.round_end.test1"));
			comingYearData.setText(Lang.get("screen.round_end.test2"));
		}

		nextButton.setText(Lang.get("ui.generic.continue"));
		nextButton.setDisabled(!serverReady);
		nextButton.setTouchable(
				!serverReady ? Touchable.disabled : Touchable.enabled);
	}

	@Override
	public void renderGame(float delta) {
		ProjektGGApplication game = (ProjektGGApplication) this.application;

		if (game.getClient() != null)
			game.getClient().updatePing(delta);

		// if (game.isHost())
		// game.getServer().update();
	}

	@Subscribe
	@Override
	public synchronized void onServerReady(ServerReadyEvent event) {
		this.serverReady = true;

		setUIValues();
	}

	public void setServerReady() {
		this.serverReady = true;
	}

	@Subscribe
	public void onAllPlayersReady(AllPlayersReadyEvent event) {
		this.serverReady = false;

		if (event.isNextRound()) {
			DiscordGGHandler.getInstance().setGamePresence(
					application.getClient().getSession().getSessionSetup()
							.getMap(),
					GameClock.getYear(application.getClient().getSession()
							.getCurrentRound()),
					1, 6);

			if (!application.getClient().getSession().getWorld()
					.getMattersToHoldVoteOn().isEmpty()) {
				// TODO ersten ballot pollen und übergeben; timer etc. aus
				// Session in BallotScreen!
				application.getScreenManager().pushScreen("vote",
						"blendingTransition");
			} else {
				application.getScreenManager().pushScreen("map", "circle_open");
			}
		}
	}

}
