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
import de.eskalon.gg.net.packets.ArrangeVotePacket;
import de.eskalon.gg.simulation.ai.CharacterBehaviour;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.model.votes.Ballot;
import de.eskalon.gg.simulation.model.votes.BallotOption;
import de.eskalon.gg.simulation.model.votes.ImpeachmentBallot;

/**
 * This screen is responsible for the votes cast at the beginning of a round.
 */
public class VoteScreen extends AbstractGameScreen {

	private static final Logger LOG = LoggerService.getLogger(VoteScreen.class);

	private @Inject ISoundManager soundManager;
	private @Inject Skin skin;

	private Label infoText;
	private Table optionTable, voterTable, labelTable, buttonTable;
	private List<Button> buttons = new ArrayList<>();

	private @Nullable Ballot matterToVoteOn = null;
	private CountdownTimer voteTimer = new CountdownTimer();

	public VoteScreen() {
		super(false);
	}

	@Override
	public void show() {
		super.show();

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
			ArrangeVotePacket msg = appContext.getClient()
					.getMattersToHoldVoteOn().poll();

			if (msg == null) {
				LOG.info("[CLIENT] No vote remaining");
				screenManager.pushScreen(MapScreen.class, "circle_open");
				appContext.getGameHandler().startNextRound();
			} else {
				LOG.info("[CLIENT] Preparing next vote");
				matterToVoteOn = createBallot(msg);
				prepareNextBallot(matterToVoteOn);
			}
		} else if (voteTimer.isRunning() && voteTimer.update()) {
			voteTimer.reset();
			matterToVoteOn = null;
		}
	}

	private Ballot createBallot(ArrangeVotePacket msg) {
		switch (msg.getType()) {
		case IMPEACHMENT:
			return new ImpeachmentBallot(
					appContext.getGameHandler().getSimulation().getWorld(),
					appContext.getGameHandler().getSimulation().getWorld()
							.getCharacters().get(msg.getTarget()).getPosition(),
					msg.getCaller());
		case ELECTION:
			// return new
			// ElectionBallot(appContext.getGameHandler().getSimulation().getWorld(),
			// CollectionUtils.getKeyByValue(appContext.getGameHandler().getSimulation().getWorld().getPositions(),
			// p)
		}
		return null;
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

			voterTable.add(new CharacterComponent(skin, world.getCharacter(s),
					isLocalPlayer ? -1
							: CharacterBehaviour.getOpinionOfAnotherCharacter(
									localPlayer.getCurrentlyPlayedCharacterId(),
									s, world)))
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
							CharacterBehaviour.getOpinionOfAnotherCharacter(
									localPlayer.getCurrentlyPlayedCharacterId(),
									(short) option.getValue(), world)))
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

		int result = appContext.getGameHandler().getSimulation()
				.processVotes(matterToVoteOn, ev.getIndividualVotes());

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
