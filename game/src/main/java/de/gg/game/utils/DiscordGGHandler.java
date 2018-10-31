package de.gg.game.utils;

import com.jagrosh.discordipc.entities.RichPresence;

import de.gg.engine.integration.DiscordRichPresenceHandler;
import de.gg.engine.lang.Lang;
import de.gg.game.types.GameMap;

public class DiscordGGHandler extends DiscordRichPresenceHandler {

	private static final DiscordGGHandler instance = new DiscordGGHandler();

	private DiscordGGHandler() {
		super(506219018160701470L);
	}

	public void setMenuPresence() {
		if (enabled)
			client.sendRichPresence(createBasicBuilder(
					Lang.get("integration.discord.in_the_menus")).build());
	}

	public void setGamePresence(GameMap map, int year, int playerCount,
			int maxPlayerCount) {
		if (enabled)
			client.sendRichPresence(
					createBasicBuilder(Lang.get("integration.discord.ingame"))
							.setDetails(
									Lang.get("integration.discord.game_details",
											map, year))
							.setParty("--", playerCount, maxPlayerCount)
							.build());
	}

	protected RichPresence.Builder createBasicBuilder(String state) {
		return super.createBasicBuilder(state).setLargeImage("castle3", null);
	}

	public static DiscordGGHandler getInstance() {
		return instance;
	}
}
