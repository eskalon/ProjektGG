package de.eskalon.gg.screens.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.event.Subscribe;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.gg.events.AllPlayersReadyEvent;
import de.eskalon.gg.thirdparty.DiscordGGHandler;

/**
 * This screen is rendered after a round ends. After all players readied up, the
 * client receives a {@link AllPlayersReadyEvent} which triggers the switch to
 * either the voting or the map screen.
 */
public class RoundEndScreen extends AbstractGameScreen {

	private static final Logger LOG = LoggerService
			.getLogger(RoundEndScreen.class);

	private @Inject Skin skin;
	private @Inject ISoundManager soundManager;

	@Asset("ui/backgrounds/round_end_screen.jpg")
	private @Inject Texture backgroundTexture;
	@Asset("audio/page_flip.mp3")
	private @Inject Sound flipSound;

	private ImageTextButton nextButton;
	private Label lastYearTitle, comingYearTitle, lastYearData, comingYearData;

	public RoundEndScreen() {
		super(false);
	}

	@Override
	public void show() {
		super.show();

		setImage(backgroundTexture);

		lastYearTitle = new Label(Lang.get("screen.round_end.last_year"), skin,
				"ink_title");
		lastYearTitle.setAlignment(Align.topLeft);
		comingYearTitle = new Label(Lang.get("screen.round_end.next_year"),
				skin, "ink_title");
		comingYearTitle.setAlignment(Align.topLeft);

		lastYearData = new Label(Lang.get("screen.round_end.test1"), skin,
				"ink_text");
		lastYearData.setAlignment(Align.topLeft);
		lastYearData.setWrap(true);
		comingYearData = new Label(Lang.get("screen.round_end.test2"), skin,
				"ink_text");
		comingYearData.setAlignment(Align.topLeft);
		comingYearData.setWrap(true);

		nextButton = new ImageTextButton("", skin, "medium");
		nextButton.setText(Lang.get("ui.generic.continue"));
		nextButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				nextButton.removeListener(this);

				soundManager.playSoundEffect("page_flip");
				nextButton.setText(Lang.get("ui.generic.waiting"));

				LOG.debug("[CLIENT] Client ist ready for next round");

				appContext.getClient().getLocalLobbyPlayer().setReady(true);
				appContext.getClient().changeLocalPlayerData(
						appContext.getClient().getLocalLobbyPlayer());

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
	public void renderGame(float delta) {
		// not needed
	}

	@Subscribe
	public void onAllPlayersReadyEvent(AllPlayersReadyEvent event) {
		DiscordGGHandler.instance().setGamePresence(
				appContext.getGameHandler().getSimulation().getWorld().getMap(),
				appContext.getGameHandler().getClock().getYear(), 1, 6);
		// TODO use real player & max player counts

		LOG.info("[CLIENT] All players ready.");

		if (!appContext.getClient().getMattersToHoldVoteOn().isEmpty()) {
			screenManager.pushScreen(VoteScreen.class, "blendingTransition");
		} else {
			screenManager.pushScreen(MapScreen.class, "circle_open");
			appContext.getGameHandler().startNextRound();
		}
	}

}
