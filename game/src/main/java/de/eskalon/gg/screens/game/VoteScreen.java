package de.eskalon.gg.screens.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.event.Subscribe;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.events.VoteFinishedEvent;
import de.eskalon.gg.graphics.ui.actors.CharacterComponent;
import de.eskalon.gg.graphics.ui.actors.OffsettableImageTextButton;
import de.eskalon.gg.input.ButtonClickListener;
import de.eskalon.gg.misc.CountdownTimer;
import de.eskalon.gg.simulation.ai.CharacterBehaviour;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.model.votes.Ballot;
import de.eskalon.gg.simulation.model.votes.BallotOption;
import de.eskalon.gg.simulation.model.votes.BallotUtils;

/**
 * This screen is responsible for the votes cast at the beginning of a round.
 */
public class VoteScreen extends AbstractGameScreen {

	private static final Logger LOG = LoggerService.getLogger(VoteScreen.class);

	private @Inject ISoundManager soundManager;
	private @Inject ProjektGGApplicationContext appContext;
	private Skin skin;

	private Label infoText;
	private Table optionTable, voterTable, labelTable, buttonTable;
	private List<Button> buttons = new ArrayList<>();

	private @Nullable Ballot matterToVoteOn = null;
	private CountdownTimer voteTimer = new CountdownTimer();

	public VoteScreen(SpriteBatch batch, Skin skin) {
		super(batch, false);
		this.skin = skin;

		labelTable = new Table();
		infoText = new Label(Lang.get("ui.generic.loading"), skin, "text");
		infoText.setWrap(true);

		optionTable = new Table();
		voterTable = new Table();
		buttonTable = new Table();

		buttonTable.setSkin(skin);
		// mainTable.padTop(-250);
		labelTable.add(infoText).center().width(700).padLeft(180).padRight(180);
		mainTable.add(labelTable).padBottom(80).top().center().row();
		buttonTable.add(voterTable).left();
		buttonTable.add("").expandX();
		buttonTable.add(optionTable).padTop(20).top().right();
		mainTable.add(buttonTable).top().fill();
	}

	@Override
	public void renderGame(float delta) {
		// PROCESS VOTES
		if (matterToVoteOn == null) {
			matterToVoteOn = appContext.getGameHandler()
					.getMattersToHoldVoteOn().poll();

			if (matterToVoteOn == null) {
				LOG.info("[CLIENT] No vote remaining");
				screenManager.pushScreen(MapScreen.class, "circle_open");
				appContext.getGameHandler().startNextRound();
			} else {
				LOG.info("[CLIENT] Preparing next vote");
				prepareNextBallot(matterToVoteOn);
			}
		} else if (voteTimer.isRunning() && voteTimer.update()) {
			voteTimer.reset();
			matterToVoteOn = null;
		}
	}

	private void prepareNextBallot(Ballot newBallot) {
		World world = appContext.getGameHandler().getSimulation().getWorld();
		Player localPlayer = appContext.getGameHandler().getLocalPlayer();

		optionTable.clear();
		voterTable.clear();
		buttons.clear();

		infoText.setText(newBallot.getInfoText());

		// Display the voters
		voterTable.add(new Label(Lang.get("screen.vote.voters"), skin, "title"))
				.padBottom(12).row();
		for (short s : newBallot.getVoters()) {
			// PositionType posT = world.getCharacter(s).getPosition();
			boolean isLocalPlayer = s == localPlayer
					.getCurrentlyPlayedCharacterId();

			voterTable
					.add(new CharacterComponent(skin, world.getCharacter(s),
							isLocalPlayer ? -1
									: appContext.getGameHandler()
											.getOpinionOfOtherCharacter(s)))
					.left().padBottom(25).row();
		}

		// Display the options (if the player can vote)
		if (newBallot.getVoters()
				.contains(localPlayer.getCurrentlyPlayedCharacterId())) {
			for (BallotOption option : newBallot.getOptions()) {
				ImageTextButton button = new OffsettableImageTextButton(
						Lang.get(option), skin, 5);
				button.addListener(new ButtonClickListener(soundManager) {
					@Override
					protected void onClick() {
						appContext.getClient().castVote(option.getValue());
						for (Button b : buttons) {
							b.setDisabled(true);
							b.setTouchable(Touchable.disabled);
						}
					}
				});
				buttons.add(button);
				if (option.isCharacter() && option.getValue() != localPlayer
						.getCurrentlyPlayedCharacterId()) {
					// PositionType posT = world.getCharacters()
					// .get((short) option.getValue()).getPosition();

					optionTable.add(new CharacterComponent(skin,
							world.getCharacter((short) option.getValue()),
							appContext.getGameHandler()
									.getOpinionOfOtherCharacter(
											(short) option.getValue())))
							.right().padBottom(8).row();
				}
				optionTable.add(button).right().padBottom(15).row();
			}
		}

		if (appContext.isHost()) {
			appContext.getClient().initVoting(matterToVoteOn);
		}
	}

	@Subscribe
	private void onVoteFinished(VoteFinishedEvent ev) {
		LOG.info("[CLIENT] Vote result received");

		// Take care of the votes which weren't cast
		if (ev.getIndividualVotes().size() != matterToVoteOn.getVoters()
				.size()) {
			for (short charId : matterToVoteOn.getVoters()) {
				if (!ev.getIndividualVotes().containsKey(charId)) {
					ev.getIndividualVotes().put(charId,
							CharacterBehaviour.getVoteOption(charId,
									matterToVoteOn, appContext.getGameHandler()
											.getSimulation().getWorld()));
				}
			}
		}

		// Calculate results & process them
		int result = BallotUtils.getBallotResult(matterToVoteOn,
				ev.getIndividualVotes(), appContext.getGameHandler()
						.getSimulation().getWorld().getSeed());
		matterToVoteOn.processVoteResult(ev.getIndividualVotes(), result,
				appContext.getGameHandler().getSimulation().getWorld());

		// Display the results
		optionTable.clear();
		infoText.setText(matterToVoteOn.getResultText(result));
		// TODO display individual votes (voterTable)
		// voterTable.clear();
		System.out.println("Abgestimmt wurde wie folgt:");
		for (Entry<Short, Integer> e : ev.getIndividualVotes().entrySet()) {
			System.out.println(String.format(" - %s: %d",
					Lang.get(appContext.getGameHandler().getSimulation()
							.getWorld().getCharacter(e.getKey())),
					e.getValue()));
		}

		voteTimer.start(7000); // display the result for 7 seconds
	}

}
