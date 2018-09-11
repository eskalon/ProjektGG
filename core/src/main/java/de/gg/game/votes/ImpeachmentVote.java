package de.gg.game.votes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.gg.game.data.vote.VoteOption;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.entities.Character;
import de.gg.game.entities.Position;
import de.gg.game.types.PositionType;
import de.gg.game.world.World;
import de.gg.lang.Lang;

/**
 * This class represents a vote held on impeaching an {@linkplain Position
 * official}.
 */
public class ImpeachmentVote extends VoteableMatter {

	public static final short DONT_IMPEACH_OPTION_INDEX = -1;
	private World world;
	private PositionType type;
	private Position pos;
	/**
	 * The person who called the impeachment vote.
	 */
	private short voteCaller;
	private short currentHolder;

	public ImpeachmentVote(World world, PositionType type, short voteCaller) {
		this.world = world;
		this.type = type;
		this.voteCaller = voteCaller;

		this.pos = world.getPositions().get(type);
		this.currentHolder = pos.getCurrentHolder();
	}

	@Override
	public String getInfoText() {
		Character voteCallerC = world.getCharacter(voteCaller);
		Character currentHolderC = world.getCharacter(currentHolder);
		return Lang.get("vote.impeachment.info", voteCallerC.getPosition(),
				voteCallerC, voteCallerC.isMale(), currentHolderC.getPosition(),
				currentHolderC, currentHolderC.isMale());
	}

	@Override
	public List<Short> getVoters() {
		List<Short> list = new ArrayList<>();

		for (PositionType t : PositionType.getEntitledImpeachmentVoters(type)) {
			short s = world.getPosition(t).getCurrentHolder();

			if (s != -1) {
				list.add(s);
			}
		}

		return list;
	}

	@Override
	public List<VoteOption> getOptions() {
		List<VoteOption> tmp = List.of(
				new VoteOption("vote.impeachment.option1", currentHolder, true),
				new VoteOption("vote.impeachment.option2",
						DONT_IMPEACH_OPTION_INDEX));

		return tmp;
	}

	@Override
	public String getResultText(VoteResults results) {
		if (results.getOverallResult() == DONT_IMPEACH_OPTION_INDEX) {
			Character currentHolderC = world.getCharacter(currentHolder);
			return Lang.get("vote.impeachment.result1", currentHolderC,
					currentHolderC.isMale());
		} else {
			Character currentHolderC = world.getCharacter(currentHolder);
			return Lang.get("vote.impeachment.result2", currentHolderC,
					currentHolderC.isMale(), type);
		}
	}

	public PositionType getType() {
		return type;
	}

	public Position getPos() {
		return pos;
	}

	@Override
	public void processVoteResult(VoteResults result, World world) {
		// Reputation & opinion effects
		for (Entry<Short, Integer> e : result.getIndividualVotes().entrySet()) {
			Character voter = world.getCharacter(e.getKey());

			if (e.getKey() == voteCaller) {
				voter.addOpinionModifier(currentHolder, -18);
			}

			if (e.getValue() == ImpeachmentVote.DONT_IMPEACH_OPTION_INDEX) {
				voter.addOpinionModifier(currentHolder, 7);
			} else {
				voter.addOpinionModifier(currentHolder, -12);
				if (world.getCharacter(currentHolder).getReputation() > 0)
					voter.setReputationModifiers(
							voter.getReputationModifiers() - 1);
			}
		}

		// Actual effect
		if (result
				.getOverallResult() == ImpeachmentVote.DONT_IMPEACH_OPTION_INDEX) {
			// Stay: nothing to do
		} else {
			// Remove
			pos.setCurrentHolder((short) -1);
		}

	}

}
