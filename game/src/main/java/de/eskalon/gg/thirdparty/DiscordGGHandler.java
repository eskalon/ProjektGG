package de.eskalon.gg.thirdparty;

import com.jagrosh.discordipc.entities.RichPresence;

import de.eskalon.commons.lang.Lang;
import de.eskalon.gg.simulation.model.types.GameMap;

public class DiscordGGHandler extends DiscordRichPresenceHandler {

	private static final DiscordGGHandler instance = new DiscordGGHandler();

	private DiscordGGHandler() {
		super(506219018160701470L);
	}

	public void setMenuPresence() {
		setPresence(
				createBasicBuilder(Lang.get("integration.discord.in_the_menus"))
						.build());

	}

	public void setGamePresence(GameMap map, int year, int playerCount,
			int maxPlayerCount) {
		setPresence(createBasicBuilder(
				Lang.get("integration.discord.game_details", map, year))
						.setState(Lang.get("integration.discord.ingame"))
						.setParty("--", playerCount, maxPlayerCount).build());
	}

	protected RichPresence.Builder createBasicBuilder(String details) {
		return super.createBasicBuilder(details).setLargeImage("castle3", null);
	}

	public static DiscordGGHandler instance() {
		return instance;
	}
}
