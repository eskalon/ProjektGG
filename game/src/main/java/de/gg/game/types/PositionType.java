package de.gg.game.types;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.gg.engine.asset.JSON;
import de.gg.engine.asset.JSONLoader.JSONLoaderParameter;
import de.gg.engine.lang.Localizable;

/**
 * Represents a position/post a character can take in the city or state.
 */
public enum PositionType implements Localizable {
	MAYOR, COUNCILMAN_1;

	public final static String POSITION_JSON_DIR = "data/positions";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", POSITION_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(PositionTypeData.class));
	}

	public PositionTypeData getData() {
		return TypeRegistry.getInstance().POSITION_TYPE_DATA.get(this);
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
		PositionTypeData data = type.getData();

		if (data.getLevel() >= 7) { // Landesebene
			list.add(MAYOR); // TODO LANDESHERR
		} else {
			if (data.hasPopularVote()) { // Popular Vote bei den Stadträten
				for (PositionType p : PositionType.values()) {
					if (p.getData().getLevel() == (data.getLevel() - 1)) {
						list.add(p);
					}
				}
			} else { // normale Abwahl innerhalb des Kabinnets
				for (PositionType p : PositionType.values()) {
					if (p.getData().getCabinet() == data.getCabinet()) {
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
		PositionTypeData data = type.getData();

		if (data.getLevel() >= 7) { // Landesebene
			list.add(MAYOR); // TODO LANDESHERR
		} else {
			if (data.hasPopularVote()) { // Popular Vote bei den Stadträten
				for (PositionType p : PositionType.values()) {
					if (p.getData().getLevel() == (data.getLevel() - 1)) {
						list.add(p);
					}
				}
			} else {
				if (data.getLevel() % 3 == 0) { // Amt wird innerhalb des
												// Kabinetts gewählt
					for (PositionType p : PositionType.values()) {
						if (p.getData().getCabinet() == data.getCabinet()) {
							list.add(p);
						}
					}
				} else { // Amt wird vom Darüber bestimmt
					for (PositionType p : PositionType.values()) {
						if (p.getData().getCabinet() == data.getCabinet()
								&& p.getData()
										.getLevel() == (data.getLevel() + 1)) {
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

		public int getLevel() {
			return level;
		}

		public int getSalary() {
			return salary;
		}

		/**
		 * @return whether the holder of this position is elected/impeached by
		 *         the positions directly below (<code>true</code>) or by his
		 *         cabinet (<code>false</code>). Is only in very few instances
		 *         <code>true</code>.
		 */
		public boolean hasPopularVote() {
			return popularVote;
		}

		/**
		 * @return the index of the cabinet this position is in.
		 */
		public int getCabinet() {
			return cabinet;
		}

		/**
		 * @return The required status for this position. <code>Null</code> if
		 *         no status is needed.
		 */
		public SocialStatus getStatusRequirement() {
			if (statusRequirementIndex == -1)
				return null;

			return SocialStatus.values()[statusRequirementIndex];
		}

		/**
		 * @return
		 * @see #getLawsToVoteFor()
		 */
		public boolean hasLawsToVoteFor() {
			return lawsToVoteForIndices != null;
		}

		/**
		 * @return The indices of all laws the holder of this position can vote
		 *         on. Is an empty list if there are none.
		 * @see LawType#getVoters()
		 */
		public List<Integer> getIndicesOfLawsToVoteFor() {
			return lawsToVoteForIndices;
		}
	}

}