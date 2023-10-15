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

/**
 * This class represents the vote held on electing an {@linkplain Position
 * official}.
 */
public class ElectionBallot extends Ballot {

	private World world;
	private PositionType type;
	private Position pos;

	public ElectionBallot(World world, PositionType type) {
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
	public List<BallotOption> getOptions() {
		List<BallotOption> list = new ArrayList<>();

		for (Short s : pos.getApplicants()) {
			list.add(new BallotOption("Auswählen", s, true));
		}

		return list;
	}

	@Override
	public String getResultText(int result) {
		return Lang.get("vote.election.result",
				world.getCharacter((short) result), type);
	}

	public PositionType getType() {
		return type;
	}

	public Position getPos() {
		return pos;
	}

	@Override
	public void processVoteResult(HashMap<Short, Integer> individualVotes,
			int result, World world) {
		// Reputation & opinion effects
		for (Entry<Short, Integer> e : individualVotes.entrySet()) {
			Character voter = world.getCharacter(e.getKey());
			for (BallotOption option : this.getOptions()) {
				if (option.getValue() == e.getValue()) {
					voter.addOpinionModifier((short) option.getValue(), 12);
				} else {
					voter.addOpinionModifier((short) option.getValue(), -8);
				}
			}
		}

		// Actual effect
		pos.setCurrentHolder(pos.getApplicants().get(result));
		pos.getApplicants().clear();
	}

}
