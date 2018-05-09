package de.gg.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.eventbus.Subscribe;

import de.gg.event.NewVoteEvent;
import de.gg.event.VoteFinishedEvent;
import de.gg.game.data.vote.VoteOption;
import de.gg.game.entity.City;
import de.gg.game.entity.Player;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.input.ButtonClickListener;
import de.gg.ui.CharacterComponent;

/**
 * This screen is responsible for the votes cast at the beginning of a round.
 */
public class GameVoteScreen extends BaseGameScreen {

	private Label infoText;
	private Table optionTable, voterTable, labelTable;
	private List<Button> buttons = new ArrayList<>();

	private City city;
	private Player localPlayer;

	@Override
	protected void initUI() {
		city = game.getClient().getCity();
		localPlayer = game.getClient().getLocalPlayer();

		labelTable = new Table();
		infoText = new Label("Laden...", skin);
		infoText.setWrap(true);

		optionTable = new Table();
		voterTable = new Table();

		labelTable.add(infoText).width(700).padLeft(20).padRight(20);
		mainTable.add(labelTable).padBottom(30).top().center().row();
		mainTable.add(voterTable).left();
		mainTable.add(optionTable).right();
		stage.setDebugAll(true);
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

		// TODO berechtigte voters anzeigen (voterTable)
		System.out.println("Zur Wahl berechtigt sind:");
		for (short s : ev.getMatterToVoteOn().getVoters()) {
			System.out.println("- " + city.getFullCharacterName(s));
		}

		if (ev.getMatterToVoteOn().getVoters()
				.contains(localPlayer.getCurrentlyPlayedCharacterId())) {
			for (VoteOption option : ev.getMatterToVoteOn().getOptions()) {

				ImageTextButton button = new ImageTextButton(option.getText(),
						skin, "small");
				button.addListener(new ButtonClickListener(assetManager,
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
				if (option.isCharacter()) {
					PositionType posT = city.getCharacters()
							.get((short) option.getValue()).getPosition();

					optionTable
							.add(new CharacterComponent(skin,
									city.getFullCharacterName(
											(short) option.getValue()),
									posT.getName(),
									game.getClient().getOpinionOfOtherCharacter(
											(short) option.getValue())))
							.right().padBottom(5).row();
				}
				optionTable.add(button).right().padBottom(10).row();
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
			System.out.println("- " + city.getFullCharacterName(e.getKey())
					+ ": " + e.getValue());
		}
	}

	@Override
	public void renderGame(float delta) {
		// unused
	}

}
