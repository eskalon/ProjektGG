package de.gg.game.model.types;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.lang.ILocalizable;
import de.gg.game.asset.JSON;
import de.gg.game.asset.JSONLoader.JSONLoaderParameter;

/**
 * Represents a position/post a character can take in the city or state.
 */
public enum PositionType implements ILocalizable {
	/* CITIZEN LEVEL */
	/* City Servants */
	CITY_SERVANT_1, CITY_SERVANT_2, TOWN_CLERK_1, TOWN_CLERK_2, COUNCIL_ASSISTANT,
	/* Informants */
	SPY_1, SPY_2, INFORMER, COUNCIL_INFORMANT,
	/* Guards */
	GUARDSMAN_1, GUARDSMAN_2, GUARDSMAN_3, ENSIGN, CAPTAIN,
	/* PATRICIAN LEVEL */
	/* Council */
	COUNCILMAN_1, COUNCILMAN_2, COUNCILMAN_3, TREASURER, MAYOR,
	/* Court */
	JAILER, JUROR, JUDGE_1, JUDGE_2, CHAIRMAN_JUDGE,
	/* CAVALIER LEVEL */
	MARSHAL, GREY_EMINENCE, CHANCELLOR, RULER;

	public final static String POSITION_JSON_DIR = "data/positions";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", POSITION_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(PositionTypeData.class));
	}

	private PositionTypeData getData() {
		return TypeRegistry.getInstance().POSITION_TYPE_DATA.get(this);
	}

	public int getLevel() {
		return getData().level;
	}

	public int getSalary() {
		return getData().salary;
	}

	/**
	 * @return whether the holder of this position is elected/impeached by the
	 *         positions directly below (<code>true</code>) or by his cabinet
	 *         (<code>false</code>). Is only in very few instances
	 *         <code>true</code>.
	 */
	public boolean hasPopularVote() {
		return getData().popularVote;
	}

	/**
	 * @return the index of the cabinet this position is in.
	 */
	public int getCabinet() {
		return getData().cabinet;
	}

	/**
	 * @return The required status for this position. <code>Null</code> if no
	 *         status is needed.
	 */
	public SocialStatus getStatusRequirement() {
		if (getData().statusRequirementIndex == -1)
			return null;

		return SocialStatus.values()[getData().statusRequirementIndex];
	}

	/**
	 * @return
	 * @see #getLawsToVoteFor()
	 */
	public boolean hasLawsToVoteFor() {
		return getData().lawsToVoteForIndices != null;
	}

	/**
	 * @return The indices of all laws the holder of this position can vote on.
	 *         Is an empty list if there are none.
	 * @see LawType#getVoters()
	 */
	public List<Integer> getIndicesOfLawsToVoteFor() {
		return getData().lawsToVoteForIndices;
	}

	@Override
	public String getUnlocalizedName() {
		return "type.position." + this.name().toLowerCase() + ".name";
	}

	/**
	 * @param type
	 *            The position to vote on.
	 * @return a list of every position type that is entitled to vote on the
	 *         impeachment of the given position.
	 */
	public static List<PositionType> getEntitledImpeachmentVoters(
			PositionType type) {
		List<PositionType> list = new ArrayList<>();

		if (type.getLevel() >= 7) { // state level
			list.add(MAYOR); // TODO LANDESHERR
		} else {
			if (type.hasPopularVote()) { // popular vote used for council
											// members
				for (PositionType p : PositionType.values()) {
					if (p.getLevel() == (type.getLevel() - 1)) {
						list.add(p);
					}
				}
			} else { // normal impeachment process in a cabinet
				for (PositionType p : PositionType.values()) {
					if (p.getCabinet() == type.getCabinet()) {
						list.add(p);
					}
				}
			}
		}

		return list;
	}

	/**
	 * @param type
	 *            The position to vote on.
	 * @return a list of every position type that is entitled to vote on the
	 *         election of the given position.
	 */
	public static List<PositionType> getEntitledElectionVoters(
			PositionType type) {
		List<PositionType> list = new ArrayList<>();

		if (type.getLevel() >= 7) { // state level
			list.add(MAYOR); // TODO LANDESHERR
		} else {
			if (type.hasPopularVote()) { // popular vote used for council
											// members
				for (PositionType p : PositionType.values()) {
					if (p.getLevel() == (type.getLevel() - 1)) {
						list.add(p);
					}
				}
			} else {
				if (type.getLevel() % 3 == 0) { // office is elected in a
												// cabinet
					for (PositionType p : PositionType.values()) {
						if (p.getCabinet() == type.getCabinet()) {
							list.add(p);
						}
					}
				} else { // office is selected by superior
					for (PositionType p : PositionType.values()) {
						if (p.getCabinet() == type.getCabinet()
								&& p.getLevel() == (type.getLevel() + 1)) {
							list.add(p);
						}
					}
				}
			}
		}

		return list;
	}

	public class PositionTypeData {
		private int level;
		private int statusRequirementIndex;
		private int salary;
		private int cabinet;
		private List<Integer> lawsToVoteForIndices;
		private boolean popularVote = false;

		PositionTypeData() {
			// default public constructor
		}
	}

}