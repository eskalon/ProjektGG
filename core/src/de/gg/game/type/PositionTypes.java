package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.type.LawTypes.LawType;
import de.gg.game.type.SocialStatusS.SocialStatus;
import de.gg.util.JSONParser;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class PositionTypes {

	public static PositionType MAYOR, COUNCILMAN_1;
	private static List<PositionType> VALUES;

	@Asset(Text.class)
	private static final String MAYOR_JSON_PATH = "data/positions/mayor.json";
	@Asset(Text.class)
	private static final String COUNCILMAN_1_JSON_PATH = "data/positions/councilman1.json";

	private PositionTypes() {
		// shouldn't get instantiated
	}

	public static void finishLoading(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		MAYOR = JSONParser.parseFromJson(
				assetManager.get(MAYOR_JSON_PATH, Text.class).getString(),
				PositionType.class);
		VALUES.add(MAYOR);

		COUNCILMAN_1 = JSONParser.parseFromJson(assetManager
				.get(COUNCILMAN_1_JSON_PATH, Text.class).getString(),
				PositionType.class);
		VALUES.add(COUNCILMAN_1);
	}

	public static List<PositionType> getValues() {
		return VALUES;
	}

	public static PositionType getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
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

		if (type.getLevel() >= 7) { // Landesebene
			list.add(MAYOR); // TODO LANDESHERR
		} else {
			if (type.hasPopularVote()) { // Popular Vote bei den Stadträten
				for (PositionType p : VALUES) {
					if (p.getLevel() == (type.getLevel() - 1)) {
						list.add(p);
					}
				}
			} else { // normale Abwahl innerhalb des Kabinnets
				for (PositionType p : VALUES) {
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

		if (type.getLevel() >= 7) { // Landesebene
			list.add(MAYOR); // TODO LANDESHERR
		} else {
			if (type.hasPopularVote()) { // Popular Vote bei den Stadträten
				for (PositionType p : VALUES) {
					if (p.getLevel() == (type.getLevel() - 1)) {
						list.add(p);
					}
				}
			} else {
				if (type.getLevel() % 3 == 0) { // Amt wird innerhalb des
												// Kabinetts gewählt
					for (PositionType p : VALUES) {
						if (p.getCabinet() == type.getCabinet()) {
							list.add(p);
						}
					}
				} else { // Amt wird vom Darüber bestimmt
					for (PositionType p : VALUES) {
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

	/**
	 * Represents a position/post a character can take in the city or state.
	 */
	public class PositionType {

		private String name;
		private int level;
		private int statusRequirementIndex;
		private int salary;
		private int cabinet;
		private List<Integer> lawsToVoteFor;
		private List<LawType> lawsToVoteFor2 = null;
		private boolean popularVote = false;

		PositionType() {
		}

		public String getName() {
			return name;
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
			return SocialStatusS.getByIndex(statusRequirementIndex);
		}

		/**
		 * 
		 * @return All laws the holder of this position can vote on. Is an empty
		 *         list if there are none.
		 */
		public List<LawType> getLawsToVoteFor() {
			if (lawsToVoteFor2 == null) {
				lawsToVoteFor2 = new ArrayList<>();

				for (Integer i : lawsToVoteFor) {
					lawsToVoteFor2.add(LawTypes.getByIndex(i));
				}
			}

			return lawsToVoteFor2;
		}

	}

}
