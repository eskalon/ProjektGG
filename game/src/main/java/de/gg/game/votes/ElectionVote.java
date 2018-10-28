package de.gg.game.votes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.gg.engine.lang.Lang;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.entities.Character;
import de.gg.game.entities.Position;
import de.gg.game.types.PositionType;
import de.gg.game.world.World;

/**
 * This class represents the vote held on electing an {@linkplain Position
 * official}.
 */
public class ElectionVote extends VoteableMatter {

	private World world;
	private PositionType type;
	private Position pos;

	public ElectionVote(World world, PositionType type) {
		this.world = world;
		this.type = type;

		this.pos = world.getPositions().get(type);
	}

	@Override
	public String getInfoText() {
		return Lang.get("vote.election.info", type, pos.getApplicants().size());
	}

	@Override
	public List<Short> getVoters() {
		List<Short> list = new ArrayList<>();

		for (PositionType t : PositionType.getEntitledElectionVoters(type)) {
			short s = world.getPosition(t).getCurrentHolder();

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
		return Lang.get("vote.election.result",
				world.getCharacter((short) results.getOverallResult()), type);
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
