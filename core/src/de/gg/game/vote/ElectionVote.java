package de.gg.game.vote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.gg.game.data.vote.VoteOption;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.entity.Character;
import de.gg.game.entity.Position;
import de.gg.game.type.PositionTypes;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.world.City;

/**
 * This class represents the vote held on electing an {@linkplain Position
 * official}.
 */
public class ElectionVote extends VoteableMatter {

	private City city;
	private PositionType type;
	private Position pos;

	public ElectionVote(City city, PositionType type) {
		this.city = city;
		this.type = type;

		this.pos = city.getPositions().get(type);
	}

	@Override
	public String getInfoText() {
		return String.format(
				"Das Amt des %s ist neu zu besetzen. Es haben sich %d Kandidaten zur Wahl aufstellen lassen.",
				type.getName(), pos.getApplicants().size());
	}

	@Override
	public List<Short> getVoters() {
		List<Short> list = new ArrayList<>();

		for (PositionType t : PositionTypes.getEntitledElectionVoters(type)) {
			short s = city.getPosition(t).getCurrentHolder();

			if (s != -1) {
				list.add(s);
			}
		}

		return list;
	}

	@Override
	public List<VoteOption> getOptions() {
		List<VoteOption> list = new ArrayList<>();

		for (Short s : pos.getApplicants()) {
			list.add(new VoteOption("Auswählen", s, true));
		}

		return list;
	}

	@Override
	public String getResultText(VoteResults results) {
		return String.format("%s ist zum neuen %s gewählt worden. Glückwunsch!",
				city.getFullCharacterName((short) results.getOverallResult()),
				type.getName());
	}

	public PositionType getType() {
		return type;
	}

	public Position getPos() {
		return pos;
	}

	@Override
	public void processVoteResult(VoteResults result, City city) {
		// Reputation & opinion effects
		for (Entry<Short, Integer> e : result.getIndividualVotes().entrySet()) {
			Character voter = city.getCharacter(e.getKey());
			for (VoteOption option : this.getOptions()) {
				if (option.getValue() == e.getValue()) {
					voter.addOpinionModifier((short) option.getValue(), 12);
				} else {
					voter.addOpinionModifier((short) option.getValue(), -8);
				}
			}
		}

		// Actual effect
		pos.setCurrentHolder(
				pos.getApplicants().get(result.getOverallResult()));
		pos.getApplicants().clear();
	}

}
