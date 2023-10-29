package de.eskalon.gg.simulation.model.votes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.eskalon.commons.lang.Lang;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.Character;
import de.eskalon.gg.simulation.model.entities.Position;
import de.eskalon.gg.simulation.model.types.PositionType;
import lombok.Getter;

/**
 * This class represents a vote held on impeaching an {@linkplain Position
 * official}.
 */
public class ImpeachmentBallot extends Ballot {

	public static final short DONT_IMPEACH_OPTION_INDEX = -1;
	private World world;
	private @Getter PositionType type;
	private @Getter Position pos;
	/**
	 * The person who called the impeachment vote.
	 */
	private short voteCaller;
	private short currentHolder;

	public ImpeachmentBallot(World world, PositionType type, short voteCaller) {
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
	public List<BallotOption> getOptions() {
		List<BallotOption> tmp = List.of(
				new BallotOption("vote.impeachment.option1", currentHolder,
						true),
				new BallotOption("vote.impeachment.option2",
						DONT_IMPEACH_OPTION_INDEX));

		return tmp;
	}

	@Override
	public String getResultText(int result) {
		if (result == DONT_IMPEACH_OPTION_INDEX) {
			Character currentHolderC = world.getCharacter(currentHolder);
			return Lang.get("vote.impeachment.result1", currentHolderC,
					currentHolderC.isMale());
		} else {
			Character currentHolderC = world.getCharacter(currentHolder);
			return Lang.get("vote.impeachment.result2", currentHolderC,
					currentHolderC.isMale(), type);
		}
	}

	@Override
	public void processVoteResult(HashMap<Short, Integer> individualVotes,
			int result, World world) {
		// Reputation & opinion effects
		for (Entry<Short, Integer> e : individualVotes.entrySet()) {
			Character voter = world.getCharacter(e.getKey());

			if (e.getKey() == voteCaller) {
				voter.addOpinionModifier(currentHolder, -18);
			}

			if (e.getValue() == ImpeachmentBallot.DONT_IMPEACH_OPTION_INDEX) {
				voter.addOpinionModifier(currentHolder, 7);
			} else {
				voter.addOpinionModifier(currentHolder, -12);
				if (world.getCharacter(currentHolder).getReputation() > 0)
					voter.setReputationModifiers(
							voter.getReputationModifiers() - 1);
			}
		}

		// Actual effect
		if (result == ImpeachmentBallot.DONT_IMPEACH_OPTION_INDEX) {
			// Stay: nothing to do
		} else {
			// Remove
			pos.setCurrentHolder((short) -1);
		}

	}

}
