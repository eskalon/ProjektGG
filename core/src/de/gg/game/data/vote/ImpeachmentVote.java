package de.gg.game.data.vote;

import java.util.ArrayList;
import java.util.List;

import de.gg.game.entity.Position;
import de.gg.game.type.PositionTypes;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.world.City;

/**
 * This class represents a vote held on impeaching an {@linkplain Position
 * official}.
 */
public class ImpeachmentVote extends VoteableMatter {

	public static short DONT_IMPEACH_OPTION_INDEX = -1;
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
				"%s hat %s Unzufriedenheit über %s %s deutlich gemacht und hat eine Abstimmung anberaumt, %s Amtes zu entheben.",
				city.getFullCharacterName(voteCaller),
				(city.getCharacter(voteCaller).isMale() ? "seine" : "ihre"),
				type.getName(), city.getFullCharacterName(currentHolder),
				(city.getCharacter(currentHolder).isMale()
						? "diesen seines"
						: "diese ihres"));
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
				new VoteOption("Im Amt belassen", DONT_IMPEACH_OPTION_INDEX));

		return tmp;
	}

	@Override
	public String getResultText(VoteResults results) {
		if (results.getOverallResult() == DONT_IMPEACH_OPTION_INDEX)
			return String.format(
					"Die Abstimmung %s %s Amtes zu entheben ist gescheitert.",
					city.getFullCharacterName(currentHolder),
					(city.getCharacter(currentHolder).isMale()
							? "seines"
							: "ihres"));
		else
			return String.format(
					"Die Mitglieder des Kabinetts haben %s ihr Misstrauen ausgesprochen. %s wird daher %s Amtes als %s enthoben.",
					city.getFullCharacterName(currentHolder),
					(city.getCharacter(currentHolder).isMale() ? "Er" : "Sie"),
					(city.getCharacter(currentHolder).isMale()
							? "seines"
							: "ihres"),
					type.getName());
	}

	public PositionType getType() {
		return type;
	}

	public Position getPos() {
		return pos;
	}
	
	public short getCharacterToImpeach() {
		return currentHolder;
	}

}
