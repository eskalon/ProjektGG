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

import de.gg.engine.lang.Lang;
import de.gg.engine.ui.components.OffsetableImageTextButton;
import de.gg.game.entities.Player;
import de.gg.game.events.NewVoteEvent;
import de.gg.game.events.VoteFinishedEvent;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.types.PositionType;
import de.gg.game.ui.components.CharacterComponent;
import de.gg.game.votes.VoteOption;
import de.gg.game.world.World;

/**
 * This screen is responsible for the votes cast at the beginning of a round.
 */
public class GameVoteScreen extends BaseGameScreen {

	private Label infoText;
	private Table optionTable, voterTable, labelTable, buttonTable;
	private List<Button> buttons = new ArrayList<>();

	private World world;
	private Player localPlayer;

	@Override
	protected void initUI() {
		world = game.getClient().getSession().getWorld();
		localPlayer = game.getClient().getLocalPlayer();

		labelTable = new Table();
		infoText = new Label(Lang.get("ui.generic.loading"), skin,
				"text-white-20");
		infoText.setWrap(true);

		optionTable = new Table();
		voterTable = new Table();
		buttonTable = new Table();

		buttonTable.setSkin(skin);
		mainTable.padTop(-250);
		labelTable.add(infoText).center().width(700).padLeft(180).padRight(180);
		mainTable.add(labelTable).padBottom(80).top().center().row();
		buttonTable.add(voterTable).left();
		buttonTable.add("").expandX();
		buttonTable.add(optionTable).padTop(20).top().right();
		mainTable.add(buttonTable).top().fill();
	}

	@Subscribe
	private void onNewVote(NewVoteEvent ev) {
		if (ev.getMatterToVoteOn() == null) {
			game.pushScreen("map");
			return;
		}

		optionTable.clear();
		voterTable.clear();
		buttons.clear();

		infoText.setText(ev.getMatterToVoteOn().getInfoText());

		// Display the voters
		voterTable.add(new Label(Lang.get("screen.vote.voters"), skin))
				.padBottom(30).row();
		for (short s : ev.getMatterToVoteOn().getVoters()) {
			PositionType posT = world.getCharacter(s).getPosition();
			boolean isLocalPlayer = s == game.getClient().getLocalPlayer()
					.getCurrentlyPlayedCharacterId();

			voterTable
					.add(new CharacterComponent(skin, world.getCharacter(s),
							isLocalPlayer ? -1
									: game.getClient()
											.getOpinionOfOtherCharacter(s)))
					.left().padBottom(25).row();
		}

		// Display the options (if the player can vote)
		if (ev.getMatterToVoteOn().getVoters()
				.contains(localPlayer.getCurrentlyPlayedCharacterId())) {
			for (VoteOption option : ev.getMatterToVoteOn().getOptions()) {

				ImageTextButton button = new OffsetableImageTextButton(
						Lang.get(option), skin, "small", 5);
				button.addListener(new ButtonClickListener(buttonClickSound,
						game.getSettings()) {
					@Override
					protected void onClick() {
						game.getClient().getActionHandler()
								.castVote(option.getValue());
						for (Button b : buttons) {
							b.setDisabled(true);
							b.setTouchable(Touchable.disabled);
						}
					}
				});
				buttons.add(button);
				if (option.isCharacter() && option.getValue() != game
						.getClient().getLocalPlayer()
						.getCurrentlyPlayedCharacterId()) {
					PositionType posT = world.getCharacters()
							.get((short) option.getValue()).getPosition();

					optionTable.add(new CharacterComponent(skin,
							world.getCharacter((short) option.getValue()),
							game.getClient().getOpinionOfOtherCharacter(
									(short) option.getValue())))
							.right().padBottom(8).row();
				}
				optionTable.add(button).right().padBottom(15).row();
			}
		}
	}

	@Subscribe
	private void onVoteFinished(VoteFinishedEvent ev) {
		optionTable.clear();
		// voterTable.clear();
		infoText.setText(ev.getMatterToVoteOn().getResultText(ev.getResults()));

		// TODO display individual votes (voterTable)
		System.out.println("Abgestimmt wurde folgendermaßen:");
		for (Entry<Short, Integer> e : ev.getResults().getIndividualVotes()
				.entrySet()) {
			System.out.println(String.format(" - %s: %d",
					Lang.get(world.getCharacter(e.getKey())), e.getValue()));
		}
	}

	@Override
	public void renderGame(float delta) {
		// unused
	}

}