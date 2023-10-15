package de.eskalon.commons.net.packets.data;

import java.util.HashMap;

import javax.annotation.Nullable;

public class LobbyData<G, S, P> {

	private G sessionSetup;
	private @Nullable S gameState;
	private HashMap<Short, P> players;

	public LobbyData(G sessionSetup, @Nullable S gameState) {
		this.sessionSetup = sessionSetup;
		this.gameState = gameState;
		this.players = new HashMap<>();
	}

	public void setSessionSetup(G sessionSetup) {
		this.sessionSetup = sessionSetup;
	}

	public void setGameState(@Nullable S gameState) {
		this.gameState = gameState;
	}

	public G getSessionSetup() {
		return sessionSetup;
	}

	public @Nullable S getGameState() {
		return gameState;
	}

	public HashMap<Short, P> getPlayers() {
		return players;
	}

}
