package de.gg.game.model.votes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.eskalon.commons.lang.Lang;
import de.gg.game.model.World;
import de.gg.game.model.entities.Character;
import de.gg.game.model.entities.Position;
import de.gg.game.model.types.PositionType;

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
	public String getResultText(BallotResults results) {
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
	public void processVoteResult(BallotResults result, World world) {
		// Reputation & opinion effects
		for (Entry<Short, Integer> e : result.getIndividualVotes().entrySet()) {
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
		pos.setCurrentHolder(
				pos.getApplicants().get(result.getOverallResult()));
		pos.getApplicants().clear();
	}

}
