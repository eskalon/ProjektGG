package de.gg.game.data.vote;

import java.util.ArrayList;
import java.util.List;

import de.gg.game.entity.City;
import de.gg.game.entity.Position;
import de.gg.game.type.PositionTypes;
import de.gg.game.type.PositionTypes.PositionType;

public class ImpeachmentVote extends VoteableMatter {

	private City city;
	private PositionType type;
	private Position pos;
	/**
	 * The person who called the impeachment vote.
	 */
	private short voteCaller;
	private short currentHolder;

	public ImpeachmentVote(City city, PositionType type, short voteCaller) {
		this.city = city;
		this.type = type;
		this.voteCaller = voteCaller;

		this.pos = city.getPositions().get(type);
		this.currentHolder = pos.getCurrentHolder();
	}

	@Override
	public String getInfoText() {
		return String.format(
				"%s hat seine Unzufriedenheit über %s %s deutlich gemacht und hat eine Abstimmung anberaumt, diesen seines Amtes zu entheben.",
				city.getFullCharacterName(voteCaller), type.getName(),
				city.getFullCharacterName(currentHolder));
	}

	@Override
	public List<Short> getVoters() {
		List<Short> list = new ArrayList<>();

		for (PositionType t : PositionTypes
				.getEntitledImpeachmentVoters(type)) {
			short s = city.getPosition(t).getCurrentHolder();

			if (s != -1) {
				list.add(s);
			}
		}

		return list;
	}

	@Override
	public List<VoteOption> getOptions() {
		List<VoteOption> tmp = List.of(
				new VoteOption("Misstrauen aussprechen", currentHolder, true),
				new VoteOption("Im Amt belassen", -1));

		return tmp;
	}

	@Override
	public String getResultText(VoteResults results) {
		if (results.getOverallResult() == 1)
			return String.format(
					"Die Mitglieder des Kabinetts haben %s ihr Misstrauen ausgesprochen. Er wird daher seines Amtes als %s enthoben.",
					city.getFullCharacterName(currentHolder), type.getName());
		else
			return String.format(
					"Die Abstimmung %s seines Amtes zu entheben ist gescheitert.",
					city.getFullCharacterName(currentHolder));
	}

	public PositionType getType() {
		return type;
	}

	public Position getPos() {
		return pos;
	}

}
