package de.gg.game.data.vote;

import java.util.ArrayList;
import java.util.List;

import de.gg.game.entity.City;
import de.gg.game.entity.Position;
import de.gg.game.type.PositionTypes;
import de.gg.game.type.PositionTypes.PositionType;

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

}
