package de.gg.game.ui.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.eventbus.Subscribe;

import de.eskalon.commons.lang.Lang;
import de.gg.engine.ui.components.OffsettableImageTextButton;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.events.BallotFinishedEvent;
import de.gg.game.events.NewBallotEvent;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.model.World;
import de.gg.game.model.entities.Player;
import de.gg.game.model.votes.BallotOption;
import de.gg.game.ui.components.CharacterComponent;

/**
 * This screen is responsible for the votes cast at the beginning of a round.
 */
public class GameBallotScreen extends AbstractGameScreen {

	private Label infoText;
	private Table optionTable, voterTable, labelTable, buttonTable;
	private List<Button> buttons = new ArrayList<>();

	public GameBallotScreen(ProjektGGApplication game) {
		super(game);
	}

	@Override
	protected void create() {
		super.create();

		labelTable = new Table();
		infoText = new Label(Lang.get("ui.generic.loading"), skin, "text");
		infoText.setWrap(true);

		optionTable = new Table();
		voterTable = new Table();
		buttonTable = new Table();

		buttonTable.setSkin(skin);
		//mainTable.padTop(-250);
		labelTable.add(infoText).center().width(700).padLeft(180).padRight(180);
		mainTable.add(labelTable).padBottom(80).top().center().row();
		buttonTable.add(voterTable).left();
		buttonTable.add("").expandX();
		buttonTable.add(optionTable).padTop(20).top().right();
		mainTable.add(buttonTable).top().fill();
	}

	@Subscribe
	private void onNewBallot(NewBallotEvent ev) {
		if (ev.getNewBallot() == null) {
			application.getScreenManager().pushScreen("map", "circle_open");
			return;
		}

		World world = application.getClient().getSession().getWorld();
		Player localPlayer = application.getClient().getLocalPlayer();

		optionTable.clear();
		voterTable.clear();
		buttons.clear();

		infoText.setText(ev.getNewBallot().getInfoText());

		// Display the voters
		voterTable.add(new Label(Lang.get("screen.vote.voters"), skin))
				.padBottom(30).row();
		for (short s : ev.getNewBallot().getVoters()) {
			// PositionType posT = world.getCharacter(s).getPosition();
			boolean isLocalPlayer = s == application.getClient()
					.getLocalPlayer().getCurrentlyPlayedCharacterId();

			voterTable
					.add(new CharacterComponent(skin, world.getCharacter(s),
							isLocalPlayer ? -1
									: application.getClient()
											.getOpinionOfOtherCharacter(s)))
					.left().padBottom(25).row();
		}

		// Display the options (if the player can vote)
		if (ev.getNewBallot().getVoters()
				.contains(localPlayer.getCurrentlyPlayedCharacterId())) {
			for (BallotOption option : ev.getNewBallot().getOptions()) {
				ImageTextButton button = new OffsettableImageTextButton(
						Lang.get(option), skin, 5);
				button.addListener(
						new ButtonClickListener(application.getSoundManager()) {
							@Override
							protected void onClick() {
								application.getClient().getActionHandler()
										.castVote(option.getValue());
								for (Button b : buttons) {
									b.setDisabled(true);
									b.setTouchable(Touchable.disabled);
								}
							}
						});
				buttons.add(button);
				if (option.isCharacter() && option.getValue() != application
						.getClient().getLocalPlayer()
						.getCurrentlyPlayedCharacterId()) {
					// PositionType posT = world.getCharacters()
					// .get((short) option.getValue()).getPosition();

					optionTable.add(new CharacterComponent(skin,
							world.getCharacter((short) option.getValue()),
							application.getClient().getOpinionOfOtherCharacter(
									(short) option.getValue())))
							.right().padBottom(8).row();
				}
				optionTable.add(button).right().padBottom(15).row();
			}
		}
	}

	@Subscribe
	private void onVoteFinished(BallotFinishedEvent ev) {
		optionTable.clear();
		// voterTable.clear();
		infoText.setText(ev.getBallot().getResultText(ev.getResults()));

		// TODO display individual votes (voterTable)
		System.out.println("Abgestimmt wurde wie folgt:");
		for (Entry<Short, Integer> e : ev.getResults().getIndividualVotes()
				.entrySet()) {
			System.out.println(String.format(
					" - %s: %d", Lang.get(application.getClient().getSession()
							.getWorld().getCharacter(e.getKey())),
					e.getValue()));
		}
	}

	@Override
	public void renderGame(float delta) {
		// unused
	}

	@Override
	protected void setUIValues() {
		// TODO use push params instead of events
	}

}
